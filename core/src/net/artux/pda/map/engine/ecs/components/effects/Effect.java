package net.artux.pda.map.engine.ecs.components.effects;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;

public enum Effect {
    BROKE_GRAVITY("Встряска", "icon") {
        @Override
        public void begin(Entity entity) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    pm.get(entity).body.setLinearVelocity(random(-10000, 10000), random(-10000, 10000));
                    hm.get(entity).damage(random(5,15));
                }
            }, 0, 0.1f, random(4, 10));
        }

        @Override
        public void affect(float dt, Entity entity) {

        }

        @Override
        public void end(Entity entity) {
        }
    },
    STUCK("Оцепенение", "icon") {
        @Override
        public void begin(Entity entity) {
            pm.get(entity).body.setLinearDamping(1000);
        }

        @Override
        public void affect(float dt, Entity entity) {

        }

        @Override
        public void end(Entity entity) {
            pm.get(entity).body.setLinearDamping(10);
        }
    },

    FLY("Полет", "icon") {
        @Override
        public void begin(Entity entity) {
            pm.get(entity).body.setLinearDamping(0);
        }

        @Override
        public void affect(float dt, Entity entity) {

        }

        @Override
        public void end(Entity entity) {
            pm.get(entity).body.setLinearDamping(10);
        }
    },
    BLOOD_LESS("Кровотечение", "icon") {
        @Override
        public void begin(Entity entity) {

        }

        @Override
        public void affect(float dt, Entity entity) {

        }

        @Override
        public void end(Entity entity) {

        }
    };

    private static final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private static final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);

    private final String name;
    private final String icon;

    Effect(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public abstract void begin(Entity entity);

    public abstract void affect(float dt, Entity entity);

    public abstract void end(Entity entity);

}
