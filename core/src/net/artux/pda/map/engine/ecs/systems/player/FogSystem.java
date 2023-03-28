package net.artux.pda.map.engine.ecs.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Frustum;

import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.FogOfWarComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.systems.BaseSystem;
import net.artux.pda.map.engine.ecs.systems.SoundsSystem;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class FogSystem extends BaseSystem {

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<FogOfWarComponent> fwm = ComponentMapper.getFor(FogOfWarComponent.class);

    private final SoundsSystem soundsSystem;
    private final CameraSystem cameraSystem;
    private final Frustum frustum;

    @Inject
    public FogSystem(SoundsSystem soundsSystem, CameraSystem cameraSystem) {
        super(Family.all(BodyComponent.class, FogOfWarComponent.class).exclude(PassivityComponent.class).get());
        this.soundsSystem = soundsSystem;
        this.cameraSystem = cameraSystem;

        frustum = cameraSystem.getCamera().frustum;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        ImmutableArray<Entity> entities = getEntities();
        Entity player = getPlayer();
        BodyComponent playerBodyComponent = pm.get(player);

        int entitiesVisibleByPlayer = 0;
        for (int i = 0; i < entities.size(); i++) {
            Entity entity1 = entities.get(i);
            BodyComponent firstBodyComponent = pm.get(entity1);
            FogOfWarComponent fogOfWarComponent = fwm.get(entity1);

            float dst = firstBodyComponent.getPosition().dst(playerBodyComponent.getPosition());

            float visibleCoefficient;
            if (dst < 200) {
                visibleCoefficient = 1;
                entitiesVisibleByPlayer += 1;
            } else if (dst > 270)
                visibleCoefficient = 0;
            else
                visibleCoefficient = (300 - dst) / 150;
            fogOfWarComponent.setVisionCoefficient(visibleCoefficient);

            if (fogOfWarComponent.isVisible()
                    && frustum.pointInFrustum(firstBodyComponent.getX(), firstBodyComponent.getY(), 0)) {
                if (!fogOfWarComponent.isCameraVisible
                        && !cameraSystem.isDetached()) {
                    soundsSystem.playStalkerDetection();
                }
                fogOfWarComponent.setCameraVisible(true);
            } else
                fogOfWarComponent.setCameraVisible(false);
        }

        if (isPlayerActive())
            PlayerData.visibleEntities = entitiesVisibleByPlayer + 1;
        else
            PlayerData.visibleEntities = entitiesVisibleByPlayer;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}