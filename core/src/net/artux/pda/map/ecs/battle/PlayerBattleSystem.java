package net.artux.pda.map.ecs.battle;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;

import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.interactive.PassivityComponent;
import net.artux.pda.map.ecs.sound.AudioSystem;
import net.artux.pda.map.ecs.vision.VisionComponent;
import net.artux.pda.map.ecs.characteristics.PlayerComponent;
import net.artux.pda.map.ecs.systems.BaseSystem;
import net.artux.pda.map.ecs.creation.EntityProcessorSystem;
import net.artux.pda.map.di.scope.PerGameMap;

import java.util.List;

import javax.inject.Inject;

@PerGameMap
public class PlayerBattleSystem extends BaseSystem {

    private final EntityProcessorSystem entityProcessorSystem;
    private final AudioSystem audioSystem;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private final ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    private boolean playerShoot = false;

    @Inject
    public PlayerBattleSystem(EntityProcessorSystem entityProcessorSystem, AudioSystem audioSystem) {
        super(Family.all(PlayerComponent.class, HealthComponent.class, VisionComponent.class,
                MoodComponent.class, BodyComponent.class, WeaponComponent.class).exclude(PassivityComponent.class).get());
        this.entityProcessorSystem = entityProcessorSystem;
        this.audioSystem = audioSystem;
    }

    public void setPlayerShoot(boolean playerShoot) {
        Gdx.app.getApplicationLogger().log("Player Battle", "player shoot: " + playerShoot);
        this.playerShoot = playerShoot;
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {
        WeaponComponent playerWeapon = wm.get(player);
        MoodComponent playerMood = mm.get(player);
        VisionComponent playerVision = vm.get(player);

        if (playerWeapon.getReloading() && !soundStarted) {
            audioSystem.playSound(playerWeapon.getReloadSound());
            soundStarted = true;
        }

        playerWeapon.update(deltaTime);

        if (playerMood.hasEnemy()) {
            if (!mm.get(playerMood.enemy).untarget)
                if (playerVision.isSeeing(playerMood.getEnemy()))
                    if (playerShoot && playerWeapon.shoot()) {
                        soundStarted = false;
                        entityProcessorSystem.startBullet(player, playerMood.getEnemy(), playerWeapon.getSelected());
                        audioSystem.playSound(playerWeapon.getShotSound());
                    }
        }else{
            if (playerShoot && playerWeapon.shoot()) {
                soundStarted = false;
                entityProcessorSystem.startBullet(player, playerWeapon.getSelected());
                audioSystem.playSound(playerWeapon.getShotSound());
            }
        }


        List<Entity> playerEnemies = playerVision.getVisibleEntities();
        if (playerMood.hasEnemy() && !playerEnemies.contains(playerMood.getEnemy())) {
            boolean nextEnemyFound = false;
            for (int i = 0; i < playerEnemies.size(); i++) {
                Entity enemy = playerEnemies.get(i);
                MoodComponent enemyMood = mm.get(enemy);
                if (playerMood.isEnemy(enemyMood) || enemyMood.isEnemy(playerMood)) {
                    playerMood.setEnemy(enemy);
                    nextEnemyFound = true;
                    break;
                }
            }
            if (!nextEnemyFound)
                playerMood.setEnemy(null);
        }

    }

    boolean soundStarted = false;

    public void reload() {
        WeaponComponent entityWeapon = wm.get(getPlayer());
        entityWeapon.reload();
    }
}
