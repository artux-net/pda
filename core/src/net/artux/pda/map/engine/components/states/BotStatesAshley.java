package net.artux.pda.map.engine.components.states;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.StatesComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pdalib.profile.items.Weapon;

import java.util.Timer;
import java.util.TimerTask;

public enum BotStatesAshley implements State<Entity>, Disposable {

    FIND_TARGET() {
        @Override
        public void update(Entity entity) {
            StatesComponent<Entity, BotStatesAshley> statesComponent = sm.get(entity);
            tmm.get(entity).setNextTarget();
            statesComponent.stateMachine.changeState(MOVING);
        }
    },

    STANDING() {
        @Override
        public void enter(final Entity entity) {
            super.enter(entity);
            final StatesComponent<Entity, BotStatesAshley> statesComponent = sm.get(entity);
            tmm.get(entity).setMovementTarget(null);
            try {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (statesComponent.stateMachine.getCurrentState() == STANDING)
                            statesComponent.stateMachine.changeState(FIND_TARGET);
                    }
                }, 1000 * (Math.abs(random.nextLong() % 30)));

            } catch (OutOfMemoryError error) {
                error.printStackTrace();
            }
        }

        @Override
        public void update(Entity entity) {
            //entity.stand();
        }
    },

    MOVING() {
        @Override
        public void update(Entity entity) {
            TargetMovingComponent targetMovingComponent = tmm.get(entity);
            PositionComponent positionComponent = pm.get(entity);
            if (targetMovingComponent.movementTarget != null) {
                if (positionComponent.getPosition().dst(targetMovingComponent.movementTarget) < 3f) {
                    sm.get(entity).stateMachine.changeState(STANDING);
                }
            } else sm.get(entity).stateMachine.changeState(STANDING);
        }
    },

    MOVING_FOR_SHOOT() {
        @Override
        public void update(Entity entity) {
            StatesComponent<Entity, BotStatesAshley> statesComponent = sm.get(entity);
            if (mm.get(entity).getEnemy() != null) {
                Entity enemy = mm.get(entity).getEnemy();
                if (wm.get(entity).getSelected() != null) {
                    Weapon weapon = wm.get(entity).getSelected();
                    if (pm.get(enemy).getPosition().dst(pm.get(entity).getPosition()) < distanceToAttack(weapon.precision)) {
                        tmm.get(entity).setMovementTarget(null);
                        statesComponent.stateMachine.changeState(SHOOT);
                    } else {

                        tmm.get(entity).setMovementTarget(pm.get(enemy).getPosition());
                    }
                }
            }
        }
    },

    SHOOT() {
        @Override
        public void update(Entity entity) {
            StatesComponent<Entity, BotStatesAshley> statesComponent = sm.get(entity);
            if (mm.get(entity).getEnemy() != null) {
                Entity enemy = mm.get(entity).getEnemy();
                if (wm.get(entity).getSelected() != null) {
                    Weapon weapon = wm.get(entity).getSelected();
                    if (pm.get(enemy).getPosition().dst(pm.get(entity).getPosition()) < distanceToAttack(weapon.precision)) {
                        //shoot
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

    private static ComponentMapper<StatesComponent<Entity, BotStatesAshley>> sm;
    private static ComponentMapper<TargetMovingComponent> tmm = tmm = ComponentMapper.getFor(TargetMovingComponent.class);
    private static ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private static ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private static ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    protected Timer timer = new Timer();

    @Override
    public void enter(Entity entity) {
        sm = (ComponentMapper<StatesComponent<Entity, BotStatesAshley>>) ComponentMapper.getFor(entity.getComponent(StatesComponent.class).getClass());
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
