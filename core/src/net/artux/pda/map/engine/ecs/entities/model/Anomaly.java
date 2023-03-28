package net.artux.pda.map.engine.ecs.entities.model;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.effects.Effect;
import net.artux.pda.map.engine.ecs.components.effects.Effects;
import net.artux.pda.map.engine.ecs.systems.EffectsSystem;
import net.artux.pda.map.engine.ecs.systems.MapOrientationSystem;
import net.artux.pda.map.engine.ecs.systems.RenderSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem;

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
            hcm.get(entity).damage(random(10, 30));
            engine.getSystem(EffectsSystem.class).addEffect(entity, Effect.STUCK, 5);
        }
    },
    TELEPORT("Пузырь") {
        @Override
        public void interact(Engine engine, Entity entity) {
            Vector2 nextPosition = engine.getSystem(MapOrientationSystem.class).getRandomFreePoint();
            pm.get(entity).body.setTransform(nextPosition, 0);
            pm.get(entity).body.setLinearVelocity(random(-600000, 600000), random(-600000, 600000));
            if (engine.getSystem(PlayerSystem.class).getPlayer() == entity)
                engine.getSystem(RenderSystem.class).setEffect(10);
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
    private static final ComponentMapper<Effects> ecm = ComponentMapper.getFor(Effects.class);
}
