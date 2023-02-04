package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.ArtifactComponent;
import net.artux.pda.map.engine.ecs.components.Position;
import net.artux.pda.map.engine.ecs.components.player.PlayerComponent;
import net.artux.pda.map.view.UserInterface;

import javax.inject.Inject;

@PerGameMap
public class ArtifactSystem extends BaseSystem {

    private SoundsSystem soundsSystem;
    private UserInterface userInterface;
    private ComponentMapper<Position> pcm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);

    @Inject
    public ArtifactSystem(SoundsSystem soundsSystem, UserInterface userInterface) {
        super(Family.all(ArtifactComponent.class, Position.class).get());
        this.soundsSystem = soundsSystem;
        this.userInterface = userInterface;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
    }

    private final float distanceForDetector = 100;

    private float timeCount = 0;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < getEntities().size(); i++) {
            Position position = pcm.get(getEntities().get(i));
            Position playerComponent = pcm.get(getPlayer());
            float dst = position.getPosition().dst(playerComponent.getPosition());
            if (dst < distanceForDetector) {
                float timeLimit = dst / distanceForDetector;
                timeCount += deltaTime;
                if (timeCount > timeLimit * 5) {
                    soundsSystem.playSound();
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
