package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VisionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.entities.EntityProcessorSystem;
import net.artux.pda.map.engine.systems.BaseSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;

import javax.inject.Inject;

@PerGameMap
public class PlayerBattleSystem extends BaseSystem {

    private final EntityProcessorSystem entityProcessorSystem;
    private final SoundsSystem soundsSystem;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    private boolean playerShoot = false;

    @Inject
    public PlayerBattleSystem(EntityProcessorSystem entityProcessorSystem, SoundsSystem soundsSystem) {
        super(Family.all(PlayerComponent.class, HealthComponent.class, VisionComponent.class,
                MoodComponent.class, PositionComponent.class, WeaponComponent.class).exclude(PassivityComponent.class).get());
        this.entityProcessorSystem = entityProcessorSystem;
        this.soundsSystem = soundsSystem;
    }

    public void setPlayerShoot(boolean playerShoot) {
        Gdx.app.log("Player Battle", "player shoot: " + playerShoot);
        this.playerShoot = playerShoot;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        WeaponComponent entityWeapon = wm.get(entity);
        MoodComponent moodComponent = mm.get(entity);
        VisionComponent visionComponent = vm.get(entity);
        entityWeapon.update(deltaTime);
        
        if (moodComponent.hasEnemy()) {
            if (!mm.get(moodComponent.enemy).untarget)
                if (visionComponent.isSeeing(moodComponent.getEnemy()))
                    if (playerShoot && entityWeapon.shoot()) {
                        soundStarted = false;
                        entityProcessorSystem.addBulletToEngine(entity, moodComponent.getEnemy(), entityWeapon.getSelected());
                        soundsSystem.playSoundAtDistance(entityWeapon.getShotSound(), pm.get(moodComponent.getEnemy()));
                    }
        }

        if (entityWeapon.reloading && !soundStarted){
            soundsSystem.playSound(entityWeapon.getReloadSound());
            soundStarted = true;
        }
    }

    boolean soundStarted = false;

    public void reload() {
        WeaponComponent entityWeapon = wm.get(getPlayer());
        entityWeapon.reload();
    }
}
