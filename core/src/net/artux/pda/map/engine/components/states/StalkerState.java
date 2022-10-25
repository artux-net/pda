package net.artux.pda.map.engine.components.states;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.engine.MessagingCodes.ATTACKED;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import net.artux.pda.map.engine.components.GraphMotionComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.StatesComponent;
import net.artux.pda.map.engine.components.TargetMovingComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.VisionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.model.items.WeaponModel;

import java.util.TimerTask;

public enum StalkerState implements State<Entity> {

    FIND_TARGET() {
        @Override
        public void update(Entity entity) {
            StatesComponent statesComponent = sm.get(entity);
            TargetMovingComponent targetMovingComponent = tmm.get(entity);
            gmm.get(entity).setMovementTarget(targetMovingComponent.targeting.getTarget());
            statesComponent.changeState(MOVING);
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }
    },

    STANDING() {
        @Override
        public void enter(final Entity entity) {
            super.enter(entity);
            final StatesComponent statesComponent = sm.get(entity);
            vmm.get(entity).set(0, 0);
            gmm.get(entity).disable();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (statesComponent.isInState(STANDING))
                        statesComponent.changeState(FIND_TARGET);
                }
            }, 1000 * (Math.abs(random.nextLong() % 30)));

        }

        @Override
        public void update(Entity entity) {

        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }
    },

    MOVING() {
        @Override
        public void update(Entity entity) {
            GraphMotionComponent targetMovingComponent = gmm.get(entity);
            PositionComponent positionComponent = pm.get(entity);
            if (targetMovingComponent.isActive()) {
                if (positionComponent.getPosition().dst(targetMovingComponent.movementTarget) < 3f) {
                    sm.get(entity).changeState(STANDING);
                }
            } else sm.get(entity).changeState(STANDING);
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }
    },

    MOVING_FOR_SHOOT() {
        @Override
        public void update(Entity entity) {
            StatesComponent statesComponent = sm.get(entity);
            GraphMotionComponent graphMotionComponent = gmm.get(entity);
            VisionComponent visionComponent = visionMapper.get(entity);
            if (mm.get(entity).getEnemy() != null) {
                Entity enemy = mm.get(entity).getEnemy();
                if (wm.get(entity).getSelected() != null) {
                    WeaponModel weaponModel = wm.get(entity).getSelected();
                    float dst = pm.get(enemy).dst(pm.get(entity));
                    if (dst < distanceToAttack(weaponModel.getPrecision())
                            && visionComponent.isSeeing(enemy)) {
                        graphMotionComponent.setMovementTarget(null);
                        statesComponent.changeState(SHOOT);
                    } else {
                        graphMotionComponent.setMovementTarget(pm.get(enemy));
                    }
                }
            }
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
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
                        //statesComponent.
                    } else {
                        statesComponent.changeState(MOVING_FOR_SHOOT);
                    }
                }
            }
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }
    },

    ATTACKING() {
        //global

        @Override
        public void enter(Entity entity) {
            super.enter(entity);
            sm.get(entity).changeState(MOVING_FOR_SHOOT);
        }

        @Override
        public void update(Entity entity) {
            MoodComponent moodComponent = mm.get(entity);
            if (!mm.get(entity).hasEnemy() || pm.get(entity).dst(pm.get(moodComponent.getEnemy())) > 150) {
                mm.get(entity).setEnemy(null);
                sm.get(entity).changeGlobalState(GUARDING, true);
            }
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }
    },

    GUARDING() {
        //global

        @Override
        public void enter(Entity entity) {
            super.enter(entity);
            sm.get(entity).changeState(STANDING);
        }

        @Override
        public void update(Entity entity) {
            MoodComponent moodComponent = mm.get(entity);
            if (moodComponent.hasEnemy()) {
                sm.get(entity).getDispatcher().dispatchMessage(ATTACKED, moodComponent.enemy);
                sm.get(entity).changeGlobalState(ATTACKING, true);
            } else {
                VisionComponent visionComponent = visionMapper.get(entity);
                for (Entity visibleEntity : visionComponent.getVisibleEntities()) {
                    MoodComponent enemyMood = mm.get(visibleEntity);
                    if (moodComponent.isEnemy(enemyMood)) {
                        moodComponent.setEnemy(visibleEntity);
                        sm.get(entity).getDispatcher().dispatchMessage(ATTACKED, moodComponent.enemy);
                        sm.get(entity).changeGlobalState(ATTACKING, true);
                    }
                }
            }
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            MoodComponent moodComponent = mm.get(entity);
            if (telegram.message == ATTACKED && !moodComponent.hasEnemy()) {
                moodComponent.setEnemy((Entity) telegram.extraInfo);
                sm.get(entity).changeGlobalState(ATTACKING, true);
            }
            return true;
        }
    };

    public float distanceToAttack(float precision) {
        return precision * 3;
    }

    protected ComponentMapper<StatesComponent> sm = ComponentMapper.getFor(StatesComponent.class);
    protected ComponentMapper<VelocityComponent> vmm = ComponentMapper.getFor(VelocityComponent.class);
    protected ComponentMapper<VisionComponent> visionMapper = ComponentMapper.getFor(VisionComponent.class);
    protected ComponentMapper<TargetMovingComponent> tmm = ComponentMapper.getFor(TargetMovingComponent.class);
    protected ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);
    protected ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    protected ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    protected ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    protected java.util.Timer timer = new java.util.Timer();

    @Override
    public void enter(Entity entity) {
    }


    @Override
    public void exit(Entity entity) {

    }

}
