package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.VisionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.entities.EntityProcessorSystem;
import net.artux.pda.map.engine.systems.BaseSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;

import java.util.LinkedList;

import javax.inject.Inject;

@PerGameMap
public class PlayerBattleSystem extends BaseSystem {

    private final EntityProcessorSystem entityProcessorSystem;
    private final SoundsSystem soundsSystem;

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    private boolean playerShoot = false;

    @Inject
    public PlayerBattleSystem(EntityProcessorSystem entityProcessorSystem, SoundsSystem soundsSystem) {
        super(Family.all(PlayerComponent.class, HealthComponent.class, VisionComponent.class,
                MoodComponent.class, Position.class, WeaponComponent.class).exclude(PassivityComponent.class).get());
        this.entityProcessorSystem = entityProcessorSystem;
        this.soundsSystem = soundsSystem;
    }

    public void setPlayerShoot(boolean playerShoot) {
        Gdx.app.log("Player Battle", "player shoot: " + playerShoot);
        this.playerShoot = playerShoot;
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {
        WeaponComponent playerWeapon = wm.get(player);
        MoodComponent moodComponent = mm.get(player);
        VisionComponent playerVision = vm.get(player);

        if (playerWeapon.reloading && !soundStarted) {
            soundsSystem.playSound(playerWeapon.getReloadSound());
            soundStarted = true;
        }

        playerWeapon.update(deltaTime);

        if (moodComponent.hasEnemy()) {
            if (!mm.get(moodComponent.enemy).untarget)
                if (playerVision.isSeeing(moodComponent.getEnemy()))
                    if (playerShoot && playerWeapon.shoot()) {
                        soundStarted = false;
                        entityProcessorSystem.addBulletToEngine(player, moodComponent.getEnemy(), playerWeapon.getSelected());
                        soundsSystem.playSoundAtDistance(playerWeapon.getShotSound(), pm.get(moodComponent.getEnemy()));
                    }
        }

        LinkedList<Entity> playerEnemies = playerVision.getVisibleEntities();
        if (moodComponent.hasEnemy() && !playerEnemies.contains(moodComponent.getEnemy()))
            moodComponent.setEnemy(null);
    }

    boolean soundStarted = false;

    public void reload() {
        WeaponComponent entityWeapon = wm.get(getPlayer());
        entityWeapon.reload();
    }
}
