package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.artux.pda.map.engine.ecs.components.ArtifactComponent;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.player.PlayerComponent;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class HealthSystem extends BaseSystem {

    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    @Inject
    public HealthSystem() {
        super(Family.all(HealthComponent.class, BodyComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < getEntities().size(); i++) {
            HealthComponent healthComponent = hm.get(getEntities().get(i));
            if (healthComponent.getRadiation() > 0){
                healthComponent.damage(0.001f * healthComponent.getRadiation());
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
