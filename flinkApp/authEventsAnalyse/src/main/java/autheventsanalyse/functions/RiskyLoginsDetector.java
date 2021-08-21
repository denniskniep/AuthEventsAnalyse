package autheventsanalyse.functions;

import autheventsanalyse.entity.EnrichedAuthenticationEvent;
import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.api.common.functions.MapFunction;

public class RiskyLoginsDetector implements MapFunction<EnrichedAuthenticationEvent, EnrichedAuthenticationEvent> {

        private static final long serialVersionUID = 1L;

    @Override
    public EnrichedAuthenticationEvent map(EnrichedAuthenticationEvent authEvent) throws Exception {

        authEvent.setRisk(0);
        if(RandomUtils.nextInt(0, 10) == 0){
            authEvent.setRisk(RandomUtils.nextInt(0, 100));
        }

       return authEvent;
    }
}
