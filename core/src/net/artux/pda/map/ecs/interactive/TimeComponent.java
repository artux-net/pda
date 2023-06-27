package net.artux.pda.map.ecs.interactive;

import com.badlogic.ashley.core.Component;

import java.time.Instant;

public class TimeComponent implements Component {

    private final Instant expiration;
    private final ExpirationListener listener;

    public TimeComponent(Instant expiration, ExpirationListener listener) {
        this.expiration = expiration;
        this.listener = listener;
    }

    public ExpirationListener getListener() {
        return listener;
    }

    public boolean isExpired(Instant instant) {
        return instant.isAfter(expiration);
    }

    public interface ExpirationListener {
        void onExpire();
    }


}
