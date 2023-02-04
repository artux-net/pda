package net.artux.pda.map.engine.ecs.systems;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.TimeComponent;

import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

@PerGameMap
public class TimerSystem extends IteratingSystem implements Disposable {

    private final Timer timer;
    private final ComponentMapper<TimeComponent> cm = ComponentMapper.getFor(TimeComponent.class);

    @Inject
    public TimerSystem() {
        super(Family.all(TimeComponent.class).get());
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

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TimeComponent timeComponent = cm.get(entity);
        if (timeComponent.isExpired(Instant.now())) {
            timeComponent.getListener().onExpire();
            getEngine().removeEntity(entity);
        }
    }

    public interface TimerListener {
        void action();
    }

}
