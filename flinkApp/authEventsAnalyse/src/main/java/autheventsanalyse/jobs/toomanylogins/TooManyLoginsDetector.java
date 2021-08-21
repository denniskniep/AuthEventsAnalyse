package autheventsanalyse.jobs.toomanylogins;

import autheventsanalyse.entity.AuthenticationEvent;
import autheventsanalyse.sink.LogSink;
import com.esotericsoftware.minlog.Log;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
*  Flink processes the transactions of multiple accounts with the same object instance of FraudDetector
*  a simple member variable would not be fault-tolerant and all its information be lost in case of a failure.
*  A keyed state of an operator is automatically scoped to the key of the record that is currently processed.
*  Flink maintains an independent state for each user (defined by .keyBy(AuthenticationEvent::getUsername)).
 * */
public class TooManyLoginsDetector extends KeyedProcessFunction<String, AuthenticationEvent, TooManyLoginsAlert> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TooManyLoginsDetector.class);

    private transient ValueState<Long> countOfFailedLoginsState;

    @Override
    public void open(Configuration parameters) {
        ValueStateDescriptor<Long> countOfFailedLoginsDescriptor = new ValueStateDescriptor<>(
                "countOfFailedLogins",
                Types.LONG);
        countOfFailedLoginsState = getRuntimeContext().getState(countOfFailedLoginsDescriptor);
    }

    @Override
    public void processElement(
            AuthenticationEvent authEvent,
            Context context,
            Collector<TooManyLoginsAlert> collector) throws Exception {

        if(authEvent.isSuccessful()){
            countOfFailedLoginsState.clear();
        }else{
            Long value = countOfFailedLoginsState.value();
            if(value == null){
                value = 0L;
            }
            value = value+1;
            countOfFailedLoginsState.update(value);

            if(value > 7){
                TooManyLoginsAlert alert = new TooManyLoginsAlert(authEvent.getUsername(), value);
                collector.collect(alert);
            }
        }

        LOG.debug("AuthEvent processed; User=" + authEvent.getUsername() + " AuthSuccess=" + authEvent.isSuccessful());
    }
}
