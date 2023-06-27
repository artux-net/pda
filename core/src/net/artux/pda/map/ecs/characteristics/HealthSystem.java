package net.artux.pda.map.ecs.characteristics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.interactive.PassivityComponent;
import net.artux.pda.map.ecs.render.RenderSystem;
import net.artux.pda.map.ecs.systems.BaseSystem;
import net.artux.pda.map.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class HealthSystem extends BaseSystem {

    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    private final RenderSystem renderSystem;

    @Inject
    public HealthSystem(RenderSystem renderSystem) {
        super(Family.all(HealthComponent.class, BodyComponent.class).exclude(PassivityComponent.class).get());
        this.renderSystem = renderSystem;
    }

    @Override
    public void update(float deltaTime) {
        if (isPlayerActive()) {
            HealthComponent healthComponent = hm.get(getPlayer());
            if (healthComponent.getDamaged() > 0.1f) {
                renderSystem.damageAccumulator += healthComponent.getDamaged();
            }
        }
        super.update(deltaTime);
    }

    public void setImmortalPlayer(boolean value) {
        hm.get(getPlayer()).setImmortal(value);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HealthComponent healthComponent = hm.get(entity);
        healthComponent.setDamaged(0);
        if (healthComponent.getRadiation() > 0) {
            healthComponent.damage(0.001f * healthComponent.getRadiation());
        }
    }
}
