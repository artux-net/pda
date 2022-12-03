package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.components.BulletComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.VisionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.entities.EntityProcessorSystem;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BattleSystem extends BaseSystem {

    private ImmutableArray<Entity> bullets;

    private final EntityProcessorSystem entityProcessorSystem;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);

    private boolean playerShoot = false;

    @Inject
    public BattleSystem(EntityProcessorSystem entityProcessorSystem) {
        super(Family.all(HealthComponent.class, VisionComponent.class, MoodComponent.class, PositionComponent.class, WeaponComponent.class).get());
        this.entityProcessorSystem = entityProcessorSystem;
    }

    public void setPlayerShoot(boolean playerShoot) {
        this.playerShoot = playerShoot;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        bullets = engine.getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class, BulletComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        {
            WeaponComponent entityWeapon = wm.get(getPlayer());
            MoodComponent moodComponent = mm.get(getPlayer());
            VisionComponent visionComponent = vm.get(getPlayer());

            if (moodComponent.hasEnemy()) {
                if (!mm.get(moodComponent.enemy).untarget)
                    if (visionComponent.isSeeing(moodComponent.getEnemy()))
                        if (playerShoot && entityWeapon.shoot())
                            entityProcessorSystem.addBulletToEngine(getPlayer(), moodComponent.getEnemy(), entityWeapon.getSelected());
            }
        }

        for (Entity bullet : bullets) {
            BulletComponent bulletComponent = bcm.get(bullet);
            PositionComponent bulletPosition = pm.get(bullet);
            Vector2 targetPosition = bulletComponent.getTargetPosition();

            PositionComponent targetEntityPosition = pm.get(bulletComponent.getTarget());
            MoodComponent targetEntityMood = mm.get(bulletComponent.getTarget());
            HealthComponent targetEntityHealth = hm.get(bulletComponent.getTarget());

            float dstToTarget = bulletPosition.dst(targetPosition);
            if (targetEntityPosition.epsilonEquals(bulletPosition, 4f)) {
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
        entityWeapon.update(deltaTime);

        MoodComponent moodComponent = mm.get(getPlayer());
        VisionComponent visionComponent = vm.get(getPlayer());

        if (moodComponent.hasEnemy())
            if (visionComponent.isSeeing(moodComponent.getEnemy()))
                if (entityWeapon.shoot())
                    entityProcessorSystem.addBulletToEngine(entity, moodComponent.getEnemy(), entityWeapon.getSelected());
        //todo count dst
    }

}
