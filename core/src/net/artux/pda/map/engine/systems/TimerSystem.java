package net.artux.pda.map.engine.systems;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Disposable;

import java.util.Timer;
import java.util.TimerTask;

public class TimerSystem extends EntitySystem implements Disposable {

    private final Timer timer;

    public TimerSystem() {
        timer = new Timer();
    }

    public void addTimerAction(float frequencyPerMinute, TimerListener listener) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (frequencyPerMinute >= random.nextFloat())
                    listener.action();
            }
        }, 1000 * random.nextInt(60), 1000 * 60);
    }

    public void delayAction(int seconds, TimerListener listener) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listener.action();
            }
        }, 1000L * seconds);
    }

    @Override
    public void dispose() {
        timer.cancel();
        timer.purge();
    }

    public interface TimerListener {
        void action();
    }

}
