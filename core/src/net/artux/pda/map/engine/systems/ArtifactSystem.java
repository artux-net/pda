package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.artux.pda.map.engine.components.ArtifactComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;

import java.util.Random;

public class ArtifactSystem extends BaseSystem {

    private Random random = new Random();
    private SoundsSystem soundsSystem;
    private MessagesSystem messagesSystem;
    private ComponentMapper<PositionComponent> pcm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);

    public ArtifactSystem() {
        super(Family.all(ArtifactComponent.class, PositionComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        soundsSystem = engine.getSystem(SoundsSystem.class);
        messagesSystem = engine.getSystem(MessagesSystem.class);
    }

    private final float distanceForDetector = 100;

    private float timeCount = 0;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size; i++) {
            PositionComponent positionComponent = pcm.get(entities.get(i));
            PositionComponent playerComponent = pcm.get(player);
            float dst = positionComponent.getPosition().dst(playerComponent.getPosition());
            if (dst < distanceForDetector) {
                float timeLimit = dst / distanceForDetector;
                timeCount += deltaTime;
                if (timeCount > timeLimit * 5){
                    soundsSystem.playSound();
                    timeCount = 0;
                }

                if (dst < 1) {
                    getEngine().removeEntity(entities.get(i));
                    messagesSystem.addMessage("Найден артефакт", "Уведомление");
                }
            }

        }
    }
}
