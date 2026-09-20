package net.artux.pda.map.ecs.interactive;

import com.badlogic.ashley.core.Component;

public class TimeComponent implements Component {

    private final long expirationMillis;
    private final ExpirationListener listener;

    public TimeComponent(long expirationMillis, ExpirationListener listener) {
        this.expirationMillis = expirationMillis;
        this.listener = listener;
    }

    public ExpirationListener getListener() {
        return listener;
    }

    public boolean isExpired(long nowMillis) {
        return nowMillis > expirationMillis;
    }

    public interface ExpirationListener {
        void onExpire();
    }


}
