package autheventsanalyse.jobs.toomanylogins;

import autheventsanalyse.entity.AuthenticationEvent;
import autheventsanalyse.sink.LogSink;
import autheventsanalyse.source.AuthenticationEventSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TooManyLoginsJob {

    public static void execute() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<AuthenticationEvent> authEvents = env
                .addSource(new AuthenticationEventSource())
                .name("AuthenticationEvents"); // only for debugging purpose

        //ensure that the same physical task processes all records for a particular key
        DataStream<TooManyLoginsAlert> alerts = authEvents
                .keyBy(AuthenticationEvent::getUsername)
                .process(new TooManyLoginsDetector())
                .name("too-many-logins-detector");

        alerts
                .addSink(new LogSink<TooManyLoginsAlert>())
                .name("log-too-many-logins-alerts");

        env.execute("Too many logins detection");
    }

}
