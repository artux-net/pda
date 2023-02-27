package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.BulletComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.VelocityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;
import net.artux.pda.map.engine.ecs.components.WeaponComponent;
import net.artux.pda.map.engine.ecs.components.player.PlayerComponent;

import javax.inject.Inject;

@PerGameMap
public class BattleSystem extends BaseSystem {

    private ImmutableArray<Entity> bullets;

    private final EntityProcessorSystem entityProcessorSystem;
    private final SoundsSystem soundsSystem;

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);

    @Inject
    public BattleSystem(SoundsSystem soundsSystem, EntityProcessorSystem entityProcessorSystem) {
        super(Family.all(HealthComponent.class, VisionComponent.class,
                MoodComponent.class, BodyComponent.class, WeaponComponent.class).exclude(PlayerComponent.class, PassivityComponent.class).get());
        this.entityProcessorSystem = entityProcessorSystem;
        this.soundsSystem = soundsSystem;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        bullets = engine.getEntitiesFor(Family.all(BodyComponent.class, VelocityComponent.class, BulletComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity bullet : bullets) {
            BulletComponent bulletComponent = bcm.get(bullet);
            Vector2 bulletBodyComponent = pm.get(bullet).getPosition();
            Vector2 targetPosition = bulletComponent.getTargetPosition();

            Vector2 targetEntityBodyComponent = pm.get(bulletComponent.getTarget()).getPosition();
            MoodComponent targetEntityMood = mm.get(bulletComponent.getTarget());
            HealthComponent targetEntityHealth = hm.get(bulletComponent.getTarget());

            float dstToTarget = bulletBodyComponent.dst(targetPosition);
            if (targetEntityBodyComponent.epsilonEquals(bulletBodyComponent, 4f)) {
                targetEntityHealth.damage(bulletComponent.getDamage());

                if (!targetEntityMood.hasEnemy()) {
                    targetEntityMood.setEnemy(bulletComponent.getAuthor());
                    MoodComponent playerMood = mm.get(getPlayer());
                    if (bulletComponent.getAuthor() == getPlayer() && !targetEntityMood.isEnemy(playerMood)) {
                        playerMood.setRelation(targetEntityMood, -10);
                    }
                }
            } else if (dstToTarget > bulletComponent.getLastDstToTarget())
                getEngine().removeEntity(bullet);
            else
                bulletComponent.setLastDstToTarget(dstToTarget);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        WeaponComponent entityWeapon = wm.get(entity);
        MoodComponent moodComponent = mm.get(entity);
        VisionComponent visionComponent = vm.get(entity);
        entityWeapon.update(deltaTime);

       /* if (moodComponent.hasEnemy())
            if (visionComponent.isSeeing(moodComponent.getEnemy()))
                if (entityWeapon.shoot()) {
                    entityProcessorSystem.addBulletToEngine(entity, moodComponent.getEnemy(), entityWeapon.getSelected());
                    soundsSystem.playSoundAtDistance(entityWeapon.getShotSound(), pm.get(moodComponent.getEnemy()).getPosition());
                }*/
        //todo count dst
    }

}
