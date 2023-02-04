package net.artux.pda.map.engine.ecs.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.InteractiveComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.Position;
import net.artux.pda.map.engine.ecs.systems.BaseSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

@PerGameMap
public class InteractionSystem extends BaseSystem {

    @Inject
    public InteractionSystem() {
        super(Family.all(InteractiveComponent.class, Position.class).exclude(PassivityComponent.class).get());
    }

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<InteractiveComponent> im = ComponentMapper.getFor(InteractiveComponent.class);
    private final Set<InteractiveComponent> interactiveComponents = new HashSet<>();

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        interactiveComponents.clear();

        for (int i = 0; i < getEntities().size(); i++) {
            Position position = pm.get(getEntities().get(i));
            final InteractiveComponent interactiveComponent = im.get(getEntities().get(i));

            Position playerPosition = pm.get(getPlayer());

            if (playerPosition.getPosition().dst(position.getPosition()) < 35f) {
                if (interactiveComponent.type != InteractiveComponent.Type.ACTION) {
                    interactiveComponents.add(interactiveComponent);
                } else {
                    setProcessing(false);
                    interactiveComponent.interact();
                }
            }
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }

    public Collection<InteractiveComponent> getInteractiveComponents() {
        return interactiveComponents;
    }

}
