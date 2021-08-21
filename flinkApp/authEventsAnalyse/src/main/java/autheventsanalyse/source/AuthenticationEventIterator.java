package autheventsanalyse.source;

import autheventsanalyse.entity.AuthenticationEvent;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

final class AuthenticationEventIterator implements Iterator<AuthenticationEvent>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final Timestamp INITIAL_TIMESTAMP = Timestamp.valueOf("2019-01-01 00:00:00");
    private static final int SIX_MINUTES = 360000;
    private final boolean bounded;
    private int index = 0;
    private long timestamp;
    private static List<AuthenticationEvent> data = Arrays.asList(
            new AuthenticationEvent("userA"),
            new AuthenticationEvent("userB"),
            new AuthenticationEvent("userC"),
            new AuthenticationEvent("userD"),
            new AuthenticationEvent("userE"),
            new AuthenticationEvent("userF")
    );

    static AuthenticationEventIterator bounded() {
        return new AuthenticationEventIterator(true);
    }

    static AuthenticationEventIterator unbounded() {
        return new AuthenticationEventIterator(false);
    }

    private AuthenticationEventIterator(boolean bounded) {
        this.bounded = bounded;
        this.timestamp = INITIAL_TIMESTAMP.getTime();
    }

    public boolean hasNext() {
        if(this.bounded){
            return this.index < data.size();
        }else {
            return true;
        }
    }

    public AuthenticationEvent next() {
        if(this.bounded){
            this.index++;
        } else {
            this.index = RandomUtils.nextInt(0,data.size());
        }

        AuthenticationEvent authEvent = data.get(this.index);

        this.timestamp += RandomUtils.nextInt(100, SIX_MINUTES);
        authEvent.setTimestamp(this.timestamp);

        boolean isSuccessful = RandomUtils.nextInt(0,2) == 1;
        authEvent.setSuccessful(isSuccessful);

        String ip = RandomUtils.nextInt(0,255) + "." + RandomUtils.nextInt(0,255) + "." + RandomUtils.nextInt(0,255) + "." +  RandomUtils.nextInt(0,255);
        authEvent.setIp(ip);

        return authEvent;
    }
}
