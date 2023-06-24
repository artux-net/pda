package net.artux.pda.map.engine.ecs.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;

import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;
import net.artux.pda.map.engine.ecs.components.WeaponComponent;
import net.artux.pda.map.engine.ecs.components.player.PlayerComponent;
import net.artux.pda.map.engine.ecs.systems.BaseSystem;
import net.artux.pda.map.engine.ecs.systems.EntityProcessorSystem;
import net.artux.pda.map.engine.ecs.systems.SoundsSystem;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

@PerGameMap
public class PlayerBattleSystem extends BaseSystem {

    private final EntityProcessorSystem entityProcessorSystem;
    private final SoundsSystem soundsSystem;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private final ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    private boolean playerShoot = false;

    @Inject
    public PlayerBattleSystem(EntityProcessorSystem entityProcessorSystem, SoundsSystem soundsSystem) {
        super(Family.all(PlayerComponent.class, HealthComponent.class, VisionComponent.class,
                MoodComponent.class, BodyComponent.class, WeaponComponent.class).exclude(PassivityComponent.class).get());
        this.entityProcessorSystem = entityProcessorSystem;
        this.soundsSystem = soundsSystem;
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
            soundsSystem.playSound(playerWeapon.getReloadSound());
            soundStarted = true;
        }

        playerWeapon.update(deltaTime);

        if (playerMood.hasEnemy()) {
            if (!mm.get(playerMood.enemy).untarget)
                if (playerVision.isSeeing(playerMood.getEnemy()))
                    if (playerShoot && playerWeapon.shoot()) {
                        soundStarted = false;
                        entityProcessorSystem.addBulletToEngine(player, playerMood.getEnemy(), playerWeapon.getSelected());
                        soundsSystem.playSoundAtDistance(playerWeapon.getShotSound(), pm.get(playerMood.getEnemy()).getPosition());
                    }
        }/*else{
            if (playerShoot && playerWeapon.shoot()) {
                soundStarted = false;
                entityProcessorSystem.addBulletToEngine(player, moodComponent.getEnemy(), playerWeapon.getSelected());
                soundsSystem.playSoundAtDistance(playerWeapon.getShotSound(), pm.get(moodComponent.getEnemy()));
            }
        }*/


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
