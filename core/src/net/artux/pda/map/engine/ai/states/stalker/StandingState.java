package net.artux.pda.map.engine.ai.states.stalker;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;

import net.artux.pda.map.engine.components.TargetMovingComponent;

import java.util.Timer;
import java.util.TimerTask;

public class StandingState extends StalkerState {

    private final Timer timer;

    public StandingState(StateMachine<Entity, BotState> stateMachine) {
        super(stateMachine);
        timer = new Timer();
    }

    @Override
    public void enter(Entity entity) {
        TargetMovingComponent targetMovingComponent = tmm.get(entity);
        targetMovingComponent.setMovementTarget(null);
        try {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (stateMachine.getCurrentState() instanceof StandingState)
                        stateMachine.changeState(new MovingState(stateMachine));
                }
            }, 1000 * (Math.abs(random.nextLong() % 30)));

        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
    }

    @Override
    public void update(Entity entity) {

    }

    @Override
    public void exit(Entity entity) {

    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}
