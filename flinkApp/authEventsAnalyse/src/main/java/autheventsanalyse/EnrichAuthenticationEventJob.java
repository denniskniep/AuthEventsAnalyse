package autheventsanalyse;

import autheventsanalyse.entity.AuthenticationEvent;
import autheventsanalyse.entity.EnrichedAuthenticationEvent;
import autheventsanalyse.functions.*;
import autheventsanalyse.sink.LogSink;
import autheventsanalyse.source.AuthenticationEventSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;

public class EnrichAuthenticationEventJob {

    public static void execute() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        DataStream<AuthenticationEvent> authEvents = randomlyGeneratedAuthenticationEvents(env);

        DataStream<EnrichedAuthenticationEvent> enrichedAuthEvents = transformAuthEventToEnrichedAuthEvent(authEvents);

        enrichedAuthEvents = enrichWithFailedLoginsSinceLastSuccessfulLogin(enrichedAuthEvents);

        alertOnTooManyFailedLogins(enrichedAuthEvents);

        enrichedAuthEvents = detectUserdata(enrichedAuthEvents);

        enrichedAuthEvents = detectCountryByIp(enrichedAuthEvents);

        enrichedAuthEvents = detectRiskyAuthenticationEvent(enrichedAuthEvents);

        alertOnRiskyAuthenticationEvent(enrichedAuthEvents);

        enrichedAuthEvents
                .addSink(new DiscardingSink<>())
                .name("sink");

        env.execute("Enrich AuthenticationEvent");
    }

    private static void alertOnRiskyAuthenticationEvent(DataStream<EnrichedAuthenticationEvent> enrichedAuthEvents) {
        enrichedAuthEvents
                .filter(e -> e.getRisk() > 75)
                .startNewChain()
                .addSink(new LogSink<EnrichedAuthenticationEvent>())
                .name("alert-on-risky-authentication-event");

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
                .addSink(new LogSink<TooManyLoginsAlert>())
                .name("alert-on-too-many-failed-logins");
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
}
