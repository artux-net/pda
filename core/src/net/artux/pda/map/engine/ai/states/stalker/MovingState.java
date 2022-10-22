package net.artux.pda.map.engine.ai.states.stalker;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.VelocityComponent;

public class MovingState extends StalkerState {

    public MovingState(StateMachine<Entity, BotState> stateMachine) {
        super(stateMachine);
    }

    @Override
    public void enter(Entity entity) {
        TargetMovingComponent targetMovingComponent = tmm.get(entity);
        PositionComponent positionComponent = pm.get(entity);

        targetMovingComponent.setPathToNextTarget(positionComponent.getPosition());
    }

    @Override
    public void update(Entity entity) {
        TargetMovingComponent targetMovingComponent = tmm.get(entity);
        PositionComponent positionComponent = pm.get(entity);
        VelocityComponent velocityComponent = vcm.get(entity);
        if (targetMovingComponent.movementTarget != null) {
            if (positionComponent.getPosition().dst(targetMovingComponent.movementTarget) < 3f) {
                changeState(new StandingState(stateMachine));
            } else {
                Vector2 target;

                if (targetMovingComponent.tempTarget == null
                        || positionComponent.getPosition().dst(
                        new Vector2(targetMovingComponent.tempTarget.realX, targetMovingComponent.tempTarget.realY)) < 5) {
                    if (targetMovingComponent.iterator.hasNext()) {
                        targetMovingComponent.tempTarget = targetMovingComponent.iterator.next();
                        target = new Vector2(targetMovingComponent.tempTarget.realX, targetMovingComponent.tempTarget.realY);
                    } else {
                        targetMovingComponent.getPath().clear();
                        targetMovingComponent.movementTarget = null;
                        targetMovingComponent.tempTarget = null;
                        targetMovingComponent.iterator = null;
                        target = Vector2.Zero;
                    }
                } else
                    target = new Vector2(targetMovingComponent.tempTarget.realX, targetMovingComponent.tempTarget.realY); // движемся к узлу

                Vector2 unit = new Vector2(target.x - positionComponent.getX(),
                        target.y - positionComponent.getY());

                unit.scl(1 / unit.len());
                velocityComponent.setVelocity(unit);
            }
        } else changeState(new StandingState(stateMachine));
    }

    @Override
    public void exit(Entity entity) {
        TargetMovingComponent targetMovingComponent = tmm.get(entity);
        VelocityComponent velocityComponent = vcm.get(entity);

        targetMovingComponent.getPath().clear();
        targetMovingComponent.movementTarget = null;
        targetMovingComponent.tempTarget = null;
        targetMovingComponent.iterator = null;
        velocityComponent.setVelocity(Vector2.Zero);
    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}
