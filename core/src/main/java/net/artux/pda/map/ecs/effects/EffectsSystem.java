package net.artux.pda.map.ecs.effects;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import net.artux.engine.pathfinding.TiledNavigator;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.interactive.PassivityComponent;
import net.artux.pda.map.ecs.sound.AudioSystem;
import net.artux.pda.map.ecs.systems.BaseSystem;
import net.artux.pda.map.engine.entities.ai.TileType;
import net.artux.pda.map.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class EffectsSystem extends BaseSystem {

    private final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<BodyComponent> bcm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<EffectsComponent> ecm = ComponentMapper.getFor(EffectsComponent.class);

    private final TiledNavigator tiledNavigator;
    private final AudioSystem audioSystem;
    private final Sound geigerSound;

    private float geigerTime;

    @Inject
    public EffectsSystem(TiledNavigator tiledNavigator, AudioSystem audioSystem, AssetManager assetManager) {
        super(Family.all(EffectsComponent.class, BodyComponent.class, HealthComponent.class).exclude(PassivityComponent.class).get());
        this.tiledNavigator = tiledNavigator;
        this.audioSystem = audioSystem;

        geigerSound = assetManager.get("audio/sounds/pda/geiger.ogg");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent healthComponent = hcm.get(entity);
        ecm.get(entity).getEffects().forEach(effect -> effect.affect(getEngine(), deltaTime, entity));
        Vector2 position = bcm.get(entity).getPosition();
        TileType type = tiledNavigator.getTileType(position.x, position.y);

        Radiation radiation = Radiation.EMPTY;
        if (type == TileType.WEAK_RADIATION) {
            radiation = Radiation.WEAK;
        } else if (type == TileType.MIDDLE_RADIATION) {
            radiation = Radiation.MIDDLE;
        } else if (type == TileType.STRONG_RADIATION) {
            radiation = Radiation.STRONG;
        }


        if (radiation.damage > 0) {
            healthComponent.radiationValue(radiation.damage);
            if (entity == getPlayer()) {
                geigerTime -= deltaTime;
                if (geigerTime < 0) {
                    audioSystem.playSound(geigerSound);
                    geigerTime += radiation.geigerTime;
                }
            }
        }
    }

    public void addEffect(Entity entity, Effect effect, int seconds) {
        if (!ecm.has(entity))
            return;

        ecm.get(entity).add(effect);
        effect.begin(entity);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                effect.end(entity);
                ecm.get(entity).remove(effect);
            }
        }, seconds);
    }

    private enum Radiation {
        EMPTY(0, 0),
        WEAK(0.03f, 0.2f),
        MIDDLE(0.09f, 0.15f),
        STRONG(0.015f, 0.1f);

        private final float damage;
        private final float geigerTime;

        Radiation(float damage, float geigerTime) {
            this.damage = damage;
            this.geigerTime = geigerTime;
        }
    }

}