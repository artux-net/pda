package net.artux.pda.map.ecs.interactive;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.di.scope.PerGameMap;

import java.time.Instant;

import javax.inject.Inject;

@PerGameMap
public class TimerSystem extends IteratingSystem {

    private final ComponentMapper<TimeComponent> cm = ComponentMapper.getFor(TimeComponent.class);

    @Inject
    public TimerSystem() {
        super(Family.all(TimeComponent.class).get());
    }

    public void addTimerAction(float frequencyPerMinute, TimerListener listener) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (frequencyPerMinute >= random.nextFloat())
                    listener.action();
            }
        }, random.nextInt(60), 60);
    }

    public void delayAction(int seconds, TimerListener listener) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                listener.action();
            }
        }, seconds);
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
