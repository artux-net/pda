package net.artux.pda.map.engine.entities.model;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.effects.Effect;
import net.artux.pda.map.ecs.effects.EffectsComponent;
import net.artux.pda.map.ecs.effects.EffectsSystem;
import net.artux.pda.map.ecs.ai.MapOrientationSystem;
import net.artux.pda.map.ecs.render.RenderSystem;
import net.artux.pda.map.ecs.player.PlayerSystem;

public enum Anomaly {

    SPRINGBOARD("Трамплин") {
        @Override
        public void interact(Engine engine, Entity entity) {
            pm.get(entity).body.setLinearVelocity(random(-600000, 600000), random(-600000, 600000));
            hcm.get(entity).damage(random(10, 30));
            hcm.get(entity).stamina(-random(20, 40));
            engine.getSystem(EffectsSystem.class).addEffect(entity, Effect.FLY, 1);
        }
    },
    ELECTRA("Электра") {
        @Override
        public void interact(Engine engine, Entity entity) {
            hcm.get(entity).electric(random(10, 30));
            engine.getSystem(EffectsSystem.class).addEffect(entity, Effect.STUCK, 5);
        }
    },
    TELEPORT("Пузырь") {
        @Override
        public void interact(Engine engine, Entity entity) {
            Vector2 nextPosition = engine.getSystem(MapOrientationSystem.class).getRandomFreePoint();
            hcm.get(entity).psy(random(10, 30));
            pm.get(entity).body.setTransform(nextPosition, 0);
            pm.get(entity).body.setLinearVelocity(random(-600000, 600000), random(-600000, 600000));
            if (engine.getSystem(PlayerSystem.class).getPlayer() == entity)
                engine.getSystem(RenderSystem.class).setBlurEffect(10);
        }
    },
    GRAVITY("Гравити") {
        @Override
        public void interact(Engine engine, Entity entity) {
            engine.getSystem(EffectsSystem.class).addEffect(entity, Effect.BROKE_GRAVITY, 5);
        }
    };

    private final String name;

    Anomaly(String title) {
        this.name = title;
    }

    public String getName() {
        return name;
    }

    public abstract void interact(Engine engine, Entity entity);

    private static final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private static final ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private static final ComponentMapper<EffectsComponent> ecm = ComponentMapper.getFor(EffectsComponent.class);
}
