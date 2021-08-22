package autheventsanalyse;

import autheventsanalyse.entity.AuthenticationEvent;
import autheventsanalyse.entity.EnrichedAuthenticationEvent;
import autheventsanalyse.entity.ObjectDeserializationSchema;
import autheventsanalyse.entity.ObjectSerializationSchema;
import autheventsanalyse.functions.CountryByIpDetector;
import autheventsanalyse.functions.FailedLoginsSinceLastSuccessfulCounter;
import autheventsanalyse.functions.RiskyLoginsDetector;
import autheventsanalyse.functions.TooManyLoginsAlert;
import autheventsanalyse.functions.UserdataDetector;
import autheventsanalyse.source.AuthenticationEventSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Properties;

public class EnrichAuthenticationEventJob {

    private static String KAFKA_BROKER_URL = "kafka1:9092";

    public static void execute() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        env.setRestartStrategy(RestartStrategies.exponentialDelayRestart(
                Time.milliseconds(1), //initialBackoff
                Time.seconds(5), //maxBackoff
                2, // exponential multiplier
                Time.milliseconds(10000), // threshold duration to reset delay to its initial value
                0.1 // jitter
        ));

        //DataStream<AuthenticationEvent> authEvents = randomlyGeneratedAuthenticationEvents(env);
        DataStream<AuthenticationEvent> authEvents = readAuthenticationEventsFromKafka(env);

        DataStream<EnrichedAuthenticationEvent> enrichedAuthEvents = transformAuthEventToEnrichedAuthEvent(authEvents);

        enrichedAuthEvents = enrichWithFailedLoginsSinceLastSuccessfulLogin(enrichedAuthEvents);

        alertOnTooManyFailedLogins(enrichedAuthEvents);

        enrichedAuthEvents = detectUserdata(enrichedAuthEvents);

        enrichedAuthEvents = detectCountryByIp(enrichedAuthEvents);

        enrichedAuthEvents = detectRiskyAuthenticationEvent(enrichedAuthEvents);

        alertOnRiskyAuthenticationEvent(enrichedAuthEvents);

        enrichedAuthEvents
                .addSink(kafkaSink("EnrichedAuthenticationEvents"))
                .name("kafka-sink-EnrichedAuthenticationEvents");

        env.execute("Enrich AuthenticationEvent");
    }

    private static void alertOnRiskyAuthenticationEvent(DataStream<EnrichedAuthenticationEvent> enrichedAuthEvents) {
        enrichedAuthEvents
                .filter(e -> e.getRisk() > 75)
                .startNewChain()
                .addSink(kafkaSink("RiskyAuthenticationEventAlerts"))
                .name("kafka-sink-alert-on-risky-authentication-event");

    }

    private static SingleOutputStreamOperator<EnrichedAuthenticationEvent> detectRiskyAuthenticationEvent(DataStream<EnrichedAuthenticationEvent> enrichedAuthEvents) {
        return enrichedAuthEvents
                .map(new RiskyLoginsDetector())
                .name("detect-risky-logins")
                .startNewChain();
    }

    private static SingleOutputStreamOperator<EnrichedAuthenticationEvent> detectCountryByIp(DataStream<EnrichedAuthenticationEvent> enrichedAuthEvents) {
        return enrichedAuthEvents
                .map(new CountryByIpDetector())
                .name("detect-country-by-ip")
                .startNewChain();
    }

    private static SingleOutputStreamOperator<EnrichedAuthenticationEvent> detectUserdata(DataStream<EnrichedAuthenticationEvent> enrichedAuthEvents) {
        return enrichedAuthEvents
                .map(new UserdataDetector())
                .name("detect-userdata")
                .startNewChain();
    }

    private static void alertOnTooManyFailedLogins(DataStream<EnrichedAuthenticationEvent> enrichedAuthEvents) {
        enrichedAuthEvents
                .filter(a -> a.getFailedLoginsSinceLastSuccessful() > 7)
                .map(a -> new TooManyLoginsAlert(a.getUsername(), a.getFailedLoginsSinceLastSuccessful()))
                .startNewChain()
                .addSink(kafkaSink("TooManyFailedLoginsAlerts"))
                .name("kafka-sink-alert-on-too-many-failed-logins");
    }

    private static SingleOutputStreamOperator<EnrichedAuthenticationEvent> enrichWithFailedLoginsSinceLastSuccessfulLogin(DataStream<EnrichedAuthenticationEvent> enrichedAuthEvents) {
        return enrichedAuthEvents
                .keyBy(EnrichedAuthenticationEvent::getUsername)   // keyBy = ensure that the same physical task processes all records for a particular key
                .process(new FailedLoginsSinceLastSuccessfulCounter())
                .name("enrich-with-failed-logins-since-last-successful")
                .startNewChain();
    }

    private static SingleOutputStreamOperator<EnrichedAuthenticationEvent> transformAuthEventToEnrichedAuthEvent(DataStream<AuthenticationEvent> authEvents) {
        return authEvents
                .map(a -> new EnrichedAuthenticationEvent(a))
                .name("cast-AuthenticationEvent-to-EnrichedAuthenticationEvent")
                .startNewChain(); // only for debugging purpose
    }

    private static SingleOutputStreamOperator<AuthenticationEvent> randomlyGeneratedAuthenticationEvents(StreamExecutionEnvironment env) {
        return env
                .addSource(new AuthenticationEventSource())
                .name("AuthenticationEvents")
                .startNewChain();
    }

    private static DataStreamSource<AuthenticationEvent> readAuthenticationEventsFromKafka(StreamExecutionEnvironment env){

        KafkaSource<AuthenticationEvent> source = KafkaSource.<AuthenticationEvent>builder()
                .setBootstrapServers(KAFKA_BROKER_URL)
                .setTopics("AuthenticationEvents")
                .setGroupId("flink-autheventsanalyse-consumer-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(new ObjectDeserializationSchema(AuthenticationEvent.class)))
                .build();

        return env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
    }

    private static <T>  FlinkKafkaProducer<T> kafkaSink(String topic){
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", KAFKA_BROKER_URL);

        return new FlinkKafkaProducer<T>(
                topic,
                new ObjectSerializationSchema<T>(),
                properties);
    }

}
