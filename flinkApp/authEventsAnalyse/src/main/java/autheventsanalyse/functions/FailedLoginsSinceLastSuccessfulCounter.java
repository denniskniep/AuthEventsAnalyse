package autheventsanalyse.functions;

import autheventsanalyse.entity.EnrichedAuthenticationEvent;
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
*  https://ci.apache.org/projects/flink/flink-docs-release-1.13/docs/dev/datastream/fault-tolerance/state/
 * */
public class FailedLoginsSinceLastSuccessfulCounter extends KeyedProcessFunction<String, EnrichedAuthenticationEvent, EnrichedAuthenticationEvent> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(FailedLoginsSinceLastSuccessfulCounter.class);

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
            EnrichedAuthenticationEvent authEvent,
            Context context,
            Collector<EnrichedAuthenticationEvent> collector) throws Exception {

        if(authEvent.isSuccessful()){
            countOfFailedLoginsState.clear();
        }else{
            Long value = countOfFailedLoginsState.value();
            if(value == null){
                value = 0L;
            }
            value = value+1;
            countOfFailedLoginsState.update(value);
            authEvent.setFailedLoginsSinceLastSuccessful(value);
        }
        collector.collect(authEvent);
        LOG.debug("AuthEvent processed; User=" + authEvent.getUsername() + " AuthSuccess=" + authEvent.isSuccessful());
    }
}
