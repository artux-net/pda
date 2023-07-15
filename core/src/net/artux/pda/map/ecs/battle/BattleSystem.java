package net.artux.pda.map.ecs.battle;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.characteristics.PlayerComponent;
import net.artux.pda.map.ecs.creation.EntityProcessorSystem;
import net.artux.pda.map.ecs.interactive.PassivityComponent;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.sound.AudioSystem;
import net.artux.pda.map.ecs.systems.BaseSystem;
import net.artux.pda.map.ecs.vision.VisionComponent;

import javax.inject.Inject;

@PerGameMap
public class BattleSystem extends BaseSystem {

    private ImmutableArray<Entity> bullets;

    private final EntityProcessorSystem entityProcessorSystem;
    private final BulletPool bulletPool;
    private final AudioSystem audioSystem;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private final ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    private final ComponentMapper<BulletComponent> bcm = ComponentMapper.getFor(BulletComponent.class);

    @Inject
    public BattleSystem(AudioSystem audioSystem, EntityProcessorSystem entityProcessorSystem, BulletPool bulletPool) {
        super(Family.all(HealthComponent.class, VisionComponent.class, MoodComponent.class, BodyComponent.class, WeaponComponent.class).exclude(PlayerComponent.class, PassivityComponent.class).get());
        this.entityProcessorSystem = entityProcessorSystem;
        this.audioSystem = audioSystem;
        this.bulletPool = bulletPool;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        bullets = engine.getEntitiesFor(Family.all(BodyComponent.class, BulletComponent.class).exclude(PassivityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity bullet : bullets) {
            BulletComponent bulletComponent = bcm.get(bullet);

            if (bulletComponent.isFree()) {
                for (int i = 0; i < getEntities().size(); i++) {
                    Entity entity = getEntities().get(i);
                    if (checkHit(bullet, entity))
                        break;
                }
            } else {
                checkHit(bullet, bulletComponent.getTarget());
            }
        }
    }

    private boolean checkHit(Entity bullet, Entity npc) {
        BulletComponent bulletComponent = bcm.get(bullet);
        BodyComponent bulletBody = pm.get(bullet);
        Vector2 entityPosition = pm.get(npc).getPosition();
        MoodComponent targetEntityMood = mm.get(npc);
        HealthComponent targetEntityHealth = hm.get(npc);

        boolean hit = false;

        if (entityPosition.epsilonEquals(bulletBody.getPosition(), 4f)) {
            targetEntityHealth.damage(bulletComponent.getDamage());
            hit = true;
            if (!targetEntityMood.hasEnemy()) {
                targetEntityMood.setEnemy(bulletComponent.getAuthor());
                MoodComponent playerMood = mm.get(getPlayer());
                if (bulletComponent.getAuthor() == getPlayer() && !targetEntityMood.isEnemy(playerMood)) {
                    playerMood.setRelation(targetEntityMood, -10);
                }
            }
        }
        if (!hit
                && bulletComponent.getTargetPosition().epsilonEquals(bulletBody.getPosition(), 1))
            hit = true;


        if (hit)
            bulletPool.free(bullet);

        return hit;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        WeaponComponent entityWeapon = wm.get(entity);
        MoodComponent moodComponent = mm.get(entity);
        VisionComponent visionComponent = vm.get(entity);
        entityWeapon.update(deltaTime);

        if (moodComponent.hasEnemy())
            if (visionComponent.isSeeing(moodComponent.getEnemy()))
                if (entityWeapon.shoot()) {
                    entityProcessorSystem.startBullet(entity, moodComponent.getEnemy(), entityWeapon.getSelected());
                    audioSystem.playSoundAtDistance(entityWeapon.getShotSound(), pm.get(moodComponent.getEnemy()).getPosition());
                }
        //todo count dst
    }

}