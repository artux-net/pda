package net.artux.pda.map.ecs.effects;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.ecs.battle.WeaponComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.characteristics.PlayerComponent;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.player.PlayerSystem;
import net.artux.pda.map.ecs.render.RenderSystem;
import net.artux.pda.model.items.WeaponModel;

import java.util.HashMap;
import java.util.Map;

public enum Effect {
    BROKE_GRAVITY("Встряска", "icon") {
        @Override
        public void begin(Entity entity) {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    pm.get(entity).body.setLinearVelocity(random(-10000, 10000), random(-10000, 10000));
                    hm.get(entity).damage(random(5, 15));
                }
            }, 0, 0.1f, random(4, 10));
        }

        @Override
        public void affect(Engine engine, float dt, Entity entity) {

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
        public void affect(Engine engine, float dt, Entity entity) {

        }

        @Override
        public void end(Entity entity) {
            pm.get(entity).body.setLinearDamping(10);
        }
    },
    LOST_MIND("Помутнение сознания", "icon") {

        final HashMap<WeaponModel, Float> precisions = new HashMap<>();

        @Override
        public void begin(Entity entity) {
            pm.get(entity).body.setLinearVelocity(random(-20, 20), random(-20, 20));
        }

        @Override
        public void affect(Engine engine, float dt, Entity entity) {
            RenderSystem renderSystem = engine.getSystem(RenderSystem.class);
            if (engine.getSystem(PlayerSystem.class).getPlayer() == entity
                    && renderSystem.getBlurEffect() <= 0)
                engine.getSystem(RenderSystem.class).setBlurEffect(3);

            if (!wm.has(entity))
                return;
            WeaponModel weaponModel = wm.get(entity).getSelected();
            if (weaponModel == null)
                return;
            if (weaponModel.getPrecision() < 1)
                return;
            precisions.put(weaponModel, weaponModel.getPrecision());
            weaponModel.setPrecision(0.1f);
        }

        @Override
        public void end(Entity entity) {
            for (Map.Entry<WeaponModel, Float> entry : precisions.entrySet()) {
                entry.getKey().setPrecision(entry.getValue());
            }
            precisions.clear();
            pm.get(entity).body.setLinearDamping(10);
        }
    },
    FLY("Полет", "icon") {
        @Override
        public void begin(Entity entity) {
            pm.get(entity).body.setLinearDamping(0);
        }

        @Override
        public void affect(Engine engine, float dt, Entity entity) {

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
        public void affect(Engine engine, float dt, Entity entity) {

        }

        @Override
        public void end(Entity entity) {

        }
    };

    private static final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private static final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private static final ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    private static final ComponentMapper<PlayerComponent> plm = ComponentMapper.getFor(PlayerComponent.class);

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

    public abstract void affect(Engine engine, float dt, Entity entity);

    public abstract void end(Entity entity);

}
