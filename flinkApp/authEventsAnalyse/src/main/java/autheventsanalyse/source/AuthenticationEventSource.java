package autheventsanalyse.source;

import autheventsanalyse.entity.AuthenticationEvent;
import org.apache.flink.annotation.Public;
import org.apache.flink.streaming.api.functions.source.FromIteratorFunction;

import java.io.Serializable;
import java.util.Iterator;

@Public
public class AuthenticationEventSource extends FromIteratorFunction<AuthenticationEvent> {
    private static final long serialVersionUID = 1L;

    public AuthenticationEventSource() {
        super(new AuthenticationEventSource.RateLimitedIterator(AuthenticationEventIterator.unbounded()));
    }

    private static class RateLimitedIterator<T> implements Iterator<T>, Serializable {
        private static final long serialVersionUID = 1L;
        private final Iterator<T> inner;

        private RateLimitedIterator(Iterator<T> inner) {
            this.inner = inner;
        }

        public boolean hasNext() {
            return this.inner.hasNext();
        }

        public T next() {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException var2) {
                throw new RuntimeException(var2);
            }

            return this.inner.next();
        }
    }
}