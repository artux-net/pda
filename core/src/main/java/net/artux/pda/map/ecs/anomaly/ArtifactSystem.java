package net.artux.pda.map.ecs.anomaly;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.characteristics.PlayerComponent;
import net.artux.pda.map.ecs.systems.BaseSystem;
import net.artux.pda.map.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class ArtifactSystem extends BaseSystem {

    private final ComponentMapper<BodyComponent> pcm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);

    @Inject
    public ArtifactSystem() {
        super(Family.all(ArtifactComponent.class, BodyComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    private float timeCount = 0;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < getEntities().size(); i++) {
            BodyComponent bodyComponent = pcm.get(getEntities().get(i));
            BodyComponent playerComponent = pcm.get(getPlayer());
            float dst = bodyComponent.getPosition().dst(playerComponent.getPosition());
            float distanceForDetector = 100;
            if (dst < distanceForDetector) {
                float timeLimit = dst / distanceForDetector;
                timeCount += deltaTime;
                if (timeCount > timeLimit * 5) {
                    //soundsSystem.playSound();
                    timeCount = 0;
                }

                if (dst < 1) {
                    getEngine().removeEntity(getEntities().get(i));
                    //userInterface.addMessage("Найден артефакт", "Уведомление");
                }
            }

        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
