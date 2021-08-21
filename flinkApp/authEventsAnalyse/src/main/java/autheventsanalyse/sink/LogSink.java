package autheventsanalyse.sink;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PublicEvolving
public class LogSink<IN> implements SinkFunction<IN> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(LogSink.class);

    public LogSink() {
    }

    public void invoke(IN value, Context context) {
        LOG.info(value.toString());
    }
}