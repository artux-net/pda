package net.artux.pda.map.ecs.ai.states;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.ecs.ai.statemachine.MessagingCodes.ATTACKED;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.ai.GraphMotionComponent;
import net.artux.pda.map.ecs.ai.TargetMovingComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.battle.MoodComponent;
import net.artux.pda.map.ecs.ai.StatesComponent;
import net.artux.pda.map.ecs.vision.VisionComponent;
import net.artux.pda.map.ecs.battle.WeaponComponent;
import net.artux.pda.model.items.WeaponModel;

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
            TargetMovingComponent targetMovingComponent = tmm.get(entity);
            gmm.get(entity).setMovementTarget(targetMovingComponent.nextTarget());
            statesComponent.changeState(MOVING);
        }
    },

    //can not be initial
    STANDING() {
        @Override
        public void enter(final Entity entity) {
            final StatesComponent statesComponent = sm.get(entity);
            gmm.get(entity).disable();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (statesComponent.isInState(STANDING))
                        statesComponent.changeState(FIND_TARGET);
                }
            }, Math.abs(random.nextLong() % 30));

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
                        sm.get(entity).changeState(MOVING);
                    } else if (dst > 75 && dst < distanceToAttack(weaponModel.getPrecision())) {
                        gmm.get(entity).setMovementTarget(null);
                        sm.get(entity).changeState(STANDING);
                    } else {
                        //движение в обратном направлении, слишком близко подошли
                        Vector2 diff = enemyBodyComponent.cpy().sub(entityBodyComponent);
                        diff.scl(-1);
                        gmm.get(entity).setMovementTarget(diff.add(entityBodyComponent));
                        sm.get(entity).changeState(MOVING);
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

    protected final ComponentMapper<StatesComponent> sm = ComponentMapper.getFor(StatesComponent.class);
    protected final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    protected final ComponentMapper<VisionComponent> visionMapper = ComponentMapper.getFor(VisionComponent.class);
    protected final ComponentMapper<TargetMovingComponent> tmm = ComponentMapper.getFor(TargetMovingComponent.class);
    protected final ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);
    protected final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    protected final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    protected final ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

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
