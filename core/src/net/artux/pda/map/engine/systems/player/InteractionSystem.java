package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.RelationalSpriteComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.engine.systems.BaseSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.ui.UserInterface;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

@PerGameMap
public class InteractionSystem extends BaseSystem {

    private final UserInterface userInterface;
    private final SoundsSystem soundsSystem;
    private final CameraSystem cameraSystem;

    @Inject
    public InteractionSystem(UserInterface userInterface, SoundsSystem soundsSystem, CameraSystem cameraSystem) {
        super(Family.all(InteractiveComponent.class, PositionComponent.class).exclude(PassivityComponent.class).get());
        this.userInterface = userInterface;

        this.soundsSystem = soundsSystem;
        this.cameraSystem = cameraSystem;
    }

    private ImmutableArray<Entity> mobs;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<RelationalSpriteComponent> rsm = ComponentMapper.getFor(RelationalSpriteComponent.class);
    private ComponentMapper<InteractiveComponent> im = ComponentMapper.getFor(InteractiveComponent.class);
    private final Set<InteractiveComponent> interactiveComponents = new HashSet<>();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        mobs = engine.getEntitiesFor(
                Family
                        .all(MoodComponent.class, PositionComponent.class)
                        .exclude(PassivityComponent.class)
                        .get());
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        interactiveComponents.clear();

        for (int i = 0; i < getEntities().size(); i++) {
            PositionComponent positionComponent = pm.get(getEntities().get(i));
            final InteractiveComponent interactiveComponent = im.get(getEntities().get(i));

            PositionComponent playerPosition = pm.get(getPlayer());

            if (playerPosition.getPosition().dst(positionComponent.getPosition()) < 35f) {
                if (interactiveComponent.type != InteractiveComponent.Type.ACTION) {
                    interactiveComponents.add(interactiveComponent);
                } else {
                    setProcessing(false);
                    interactiveComponent.interact();
                }
            }
        }

        int counter = 0;
        for (int i = 0; i < mobs.size(); i++) {
            Entity mob = mobs.get(i);
            PositionComponent positionComponent = pm.get(mob);
            PositionComponent playerPosition = pm.get(getPlayer());

            float dst = positionComponent.dst(playerPosition);

            boolean near = true;
            float alpha;
            if (dst < 200) {
                alpha = 1;
                counter++;
            } else if (dst > 270) {
                alpha = 0;
                near = false;
            } else {
                alpha = (300 - dst) / 100;
                counter++;
            }

            if (sm.has(mob)) {
                sm.get(mob).setAlpha(alpha);
            } else if (rsm.has(mob))
                rsm.get(mob).setAlpha(alpha);

            if (near && cameraSystem.getCamera().frustum.pointInFrustum(positionComponent.getX(), positionComponent.getY(), 0)) {
                if (!positionComponent.isCameraVisible() && !cameraSystem.detached) {
                    soundsSystem.playStalkerDetection();
                }
                positionComponent.setCameraVisible(true);
            } else
                positionComponent.setCameraVisible(false);

        }
        PlayerData.visibleEntities = counter;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }

    public Collection<InteractiveComponent> getInteractiveComponents() {
        return interactiveComponents;
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }
}
