package net.artux.pda.map.engine.ecs.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.InteractiveComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.systems.BaseSystem;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

@PerGameMap
public class InteractionSystem extends BaseSystem {

    @Inject
    public InteractionSystem() {
        super(Family.all(InteractiveComponent.class, BodyComponent.class).exclude(PassivityComponent.class).get());
    }

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<InteractiveComponent> im = ComponentMapper.getFor(InteractiveComponent.class);
    private final Set<InteractiveComponent> interactiveComponents = new HashSet<>();

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        interactiveComponents.clear();

        for (int i = 0; i < getEntities().size(); i++) {
            BodyComponent bodyComponent = pm.get(getEntities().get(i));
            final InteractiveComponent interactiveComponent = im.get(getEntities().get(i));

            BodyComponent playerBodyComponent = pm.get(getPlayer());

            if (playerBodyComponent.getPosition().dst(bodyComponent.getPosition()) < 35f) {
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
