package autheventsanalyse.functions;

import autheventsanalyse.entity.EnrichedAuthenticationEvent;
import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.api.common.functions.MapFunction;

public class UserdataDetector implements MapFunction<EnrichedAuthenticationEvent, EnrichedAuthenticationEvent> {

    private static final long serialVersionUID = 1L;

    @Override
    public EnrichedAuthenticationEvent map(EnrichedAuthenticationEvent authEvent) throws Exception {
       authEvent.setOnVacation(RandomUtils.nextInt(0,300) == 0);
       return authEvent;
    }
}
