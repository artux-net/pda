package net.artux.pda.map.engine.components.states;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.GraphMotionComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.StatesComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.model.items.WeaponModel;

import java.util.Timer;
import java.util.TimerTask;

public enum BotStatesAshley implements State<Entity>, Disposable {

    FIND_TARGET() {
        @Override
        public void update(Entity entity) {
            StatesComponent statesComponent = sm.get(entity);
            TargetMovingComponent targetMovingComponent = tmm.get(entity);
            gmm.get(entity).setMovementTarget(targetMovingComponent.targeting.getTarget());
            statesComponent.stateMachine.changeState(MOVING);
        }
    },

    STANDING() {
        @Override
        public void enter(final Entity entity) {
            super.enter(entity);
            final StatesComponent statesComponent = sm.get(entity);
            vmm.get(entity).set(0,0);
            gmm.get(entity).setMovementTarget(null);
            try {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (statesComponent.stateMachine.isInState(STANDING))
                            statesComponent.stateMachine.changeState(FIND_TARGET);
                    }
                }, 1000 * (Math.abs(random.nextLong() % 30)));

            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            }
        }

        @Override
        public void update(Entity entity) {

        }
    },

    MOVING() {
        @Override
        public void update(Entity entity) {
            GraphMotionComponent targetMovingComponent = gmm.get(entity);
            PositionComponent positionComponent = pm.get(entity);
            if (targetMovingComponent.isActive()) {
                if (positionComponent.getPosition().dst(targetMovingComponent.movementTarget) < 3f) {
                    sm.get(entity).stateMachine.changeState(STANDING);
                }
            } else sm.get(entity).stateMachine.changeState(STANDING);
        }
    },

    MOVING_FOR_SHOOT() {
        @Override
        public void update(Entity entity) {
            StatesComponent statesComponent = sm.get(entity);
            GraphMotionComponent graphMotionComponent = gmm.get(entity);
            if (mm.get(entity).getEnemy() != null) {
                Entity enemy = mm.get(entity).getEnemy();
                if (wm.get(entity).getSelected() != null) {
                    WeaponModel weaponModel = wm.get(entity).getSelected();
                    if (pm.get(enemy).dst(pm.get(entity)) < distanceToAttack(weaponModel.getPrecision())) {
                        graphMotionComponent.setMovementTarget(null);
                        statesComponent.stateMachine.changeState(SHOOT);
                    } else {
                        graphMotionComponent.setMovementTarget(pm.get(enemy).getPosition());
                    }
                }
            }
        }
    },

    SHOOT() {
        @Override
        public void update(Entity entity) {
            StatesComponent statesComponent = sm.get(entity);
            if (mm.get(entity).getEnemy() != null) {
                Entity enemy = mm.get(entity).getEnemy();
                if (wm.get(entity).getSelected() != null) {
                    WeaponModel weaponModel = wm.get(entity).getSelected();
                    float dst = pm.get(enemy).getPosition().dst(pm.get(entity).getPosition());
                    if (dst < distanceToAttack(weaponModel.getPrecision())) {
                        //shoot
                    } else if (dst < 10) {
                        // отодвинутся от врага
                        //TODO
                        //statesComponent.stateMachine.
                    } else {
                        statesComponent.stateMachine.changeState(MOVING_FOR_SHOOT);
                    }
                }
            }
        }
    },

    ATTACKING() {
        //global

        @Override
        public void enter(Entity entity) {
            super.enter(entity);
            sm.get(entity).stateMachine.changeState(MOVING_FOR_SHOOT);
        }

        @Override
        public void update(Entity entity) {
            if (mm.get(entity).getEnemy() == null || pm.get(entity).getPosition().dst(tmm.get(entity).getTargeting().getTarget()) > 200) {
                mm.get(entity).setEnemy(null);
                sm.get(entity).stateMachine.changeGlobalState(GUARDING, true);
            }
        }
    },

    GUARDING() {
        //global


        @Override
        public void enter(Entity entity) {
            super.enter(entity);
            sm.get(entity).stateMachine.changeState(STANDING);
        }

        @Override
        public void update(Entity entity) {
            if (mm.get(entity).getEnemy() != null) {
                sm.get(entity).stateMachine.changeGlobalState(ATTACKING, true);
            }
        }
    };

    public float distanceToAttack(float precision) {
        return precision * 20;
    }

    protected ComponentMapper<StatesComponent> sm = ComponentMapper.getFor(StatesComponent.class);
    protected ComponentMapper<VelocityComponent> vmm = ComponentMapper.getFor(VelocityComponent.class);
    protected ComponentMapper<TargetMovingComponent> tmm = ComponentMapper.getFor(TargetMovingComponent.class);
    protected ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);
    protected ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    protected ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    protected ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    protected Timer timer = new Timer();

    @Override
    public void enter(Entity entity) {
    }


    @Override
    public void exit(Entity entity) {

    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        if (telegram.message == 1) {
            //entity.setEnemy((Entity) telegram.extraInfo);
        }
        return true;
    }

    @Override
    public void dispose() {
    }
}
