package net.artux.pda.map.engine.ecs.components.states;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.engine.ecs.systems.statemachine.MessagingCodes.ATTACKED;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.ecs.components.GraphMotionComponent;
import net.artux.pda.map.engine.ecs.components.GroupTargetMovingComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.StatesComponent;
import net.artux.pda.map.engine.ecs.components.VelocityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;
import net.artux.pda.map.engine.ecs.components.WeaponComponent;
import net.artux.pda.model.items.WeaponModel;

import java.util.TimerTask;

public enum StalkerState implements State<Entity> {

    INITIAL() {
        @Override
        public void update(Entity entity) {
            StatesComponent statesComponent = sm.get(entity);
            statesComponent.changeState(STANDING);
        }
    },

    FIND_TARGET() {
        @Override
        public void update(Entity entity) {
            StatesComponent statesComponent = sm.get(entity);
            GroupTargetMovingComponent groupTargetMovingComponent = tmm.get(entity);
            gmm.get(entity).setMovementTarget(groupTargetMovingComponent.nextTarget());
            statesComponent.changeState(MOVING);
        }
    },

    //can not be initial
    STANDING() {
        @Override
        public void enter(final Entity entity) {
            final StatesComponent statesComponent = sm.get(entity);
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
    },

    MOVING() {
        @Override
        public void update(Entity entity) {
            GraphMotionComponent targetMovingComponent = gmm.get(entity);
            BodyComponent bodyComponent = pm.get(entity);
            if (targetMovingComponent.isActive()) {
                if (bodyComponent.getPosition().dst(targetMovingComponent.movementTarget) < 3f) {
                    sm.get(entity).changeState(STANDING);
                }
            } else sm.get(entity).changeState(STANDING);
        }
    },

    ATTACKING() {
        //global

        @Override
        public void update(Entity entity) {
            MoodComponent moodComponent = mm.get(entity);
            StatesComponent statesComponent = sm.get(entity);
            VisionComponent visionComponent = visionMapper.get(entity);

            if (moodComponent.hasEnemy()) {
                Entity enemy = moodComponent.getEnemy();
                HealthComponent healthComponent = hm.get(enemy);
                if (healthComponent.isDead()) {
                    moodComponent.setEnemy(null);
                    return;
                }

                WeaponComponent weaponComponent = wm.get(entity);

                if (weaponComponent.getSelected() != null) {
                    WeaponModel weaponModel = weaponComponent.getSelected();
                    Vector2 enemyBodyComponent = pm.get(enemy).getPosition();
                    Vector2 entityBodyComponent = pm.get(entity).getPosition();

                    float dst = entityBodyComponent.dst(enemyBodyComponent);
                    if (dst > 200) {
                        moodComponent.setEnemy(null);
                        return;
                    }

                    if (!visionComponent.isSeeing(enemy)
                            || dst > distanceToAttack(weaponModel.getPrecision())) {
                        gmm.get(entity).setMovementTarget(enemyBodyComponent);
                    } else if (dst > 20 && dst < distanceToAttack(weaponModel.getPrecision())) {
                        gmm.get(entity).setMovementTarget(null);
                    } else {
                        Vector2 tempTarget = enemyBodyComponent.cpy().sub(entityBodyComponent);
                        gmm.get(entity).setMovementTarget(entityBodyComponent.cpy().sub(tempTarget));
                    }
                }
            } else {
                moodComponent.setEnemy(null);
                statesComponent.setGlobalState(GUARDING);
            }
        }
    },

    GUARDING() {
        //global
        @Override
        public void update(Entity entity) {
            MoodComponent moodComponent = mm.get(entity);
            if (moodComponent.hasEnemy()) {
                sm.get(entity).getDispatcher().dispatchMessage(ATTACKED, moodComponent.enemy);
                sm.get(entity).setGlobalState(ATTACKING);
            } else {
                VisionComponent visionComponent = visionMapper.get(entity);
                for (Entity visibleEntity : visionComponent.getVisibleEntities()) {
                    if (mm.has(visibleEntity)) {
                        MoodComponent enemyMood = mm.get(visibleEntity);
                        if (moodComponent.isEnemy(enemyMood)) {
                            moodComponent.setEnemy(visibleEntity);
                            sm.get(entity).getDispatcher().dispatchMessage(ATTACKED, moodComponent.enemy);
                            sm.get(entity).setGlobalState(ATTACKING);
                        }
                    }
                }
            }
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            MoodComponent moodComponent = mm.get(entity);
            if (telegram.message == ATTACKED && !moodComponent.hasEnemy()) {
                moodComponent.setEnemy((Entity) telegram.extraInfo);
                sm.get(entity).setGlobalState(ATTACKING);
            }
            return true;
        }
    };

    public float distanceToAttack(float precision) {
        return precision * 3;
    }

    protected ComponentMapper<StatesComponent> sm = ComponentMapper.getFor(StatesComponent.class);
    protected ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    protected ComponentMapper<VelocityComponent> vmm = ComponentMapper.getFor(VelocityComponent.class);
    protected ComponentMapper<VisionComponent> visionMapper = ComponentMapper.getFor(VisionComponent.class);
    protected ComponentMapper<GroupTargetMovingComponent> tmm = ComponentMapper.getFor(GroupTargetMovingComponent.class);
    protected ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);
    protected ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    protected ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    protected ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    protected java.util.Timer timer = new java.util.Timer();

    @Override
    public void enter(Entity entity) {
    }


    @Override
    public void exit(Entity entity) {

    }

    @Override
    public boolean onMessage(Entity entity, Telegram telegram) {
        return false;
    }
}
