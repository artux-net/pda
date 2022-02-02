package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.ui.UserInterface;

public class InteractionSystem extends EntitySystem {

    private Stage stage;
    private UserInterface userInterface;
    private SoundsSystem soundsSystem;

    public InteractionSystem(Stage stage, UserInterface userInterface, SoundsSystem soundsSystem) {
        this.stage = stage;
        this.userInterface = userInterface;
        this.soundsSystem = soundsSystem;
    }

    private ImmutableArray<Entity> points;
    private ImmutableArray<Entity> mobs;
    private ImmutableArray<Entity> players;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<InteractiveComponent> im = ComponentMapper.getFor(InteractiveComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        points = engine.getEntitiesFor(Family.all(InteractiveComponent.class, PositionComponent.class).get());
        mobs = engine.getEntitiesFor(Family.all(MoodComponent.class, PositionComponent.class).get());
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, PositionComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < points.size(); i++) {
            PositionComponent positionComponent = pm.get(points.get(i));
            InteractiveComponent interactiveComponent = im.get(points.get(i));

            for (int j = 0; j < players.size(); j++) {
                PositionComponent playerPosition = pm.get(players.get(j));

                String name = "q-" + interactiveComponent.title;
                if (playerPosition.getPosition().dst(positionComponent.getPosition()) < 35f) {
                    if (interactiveComponent.type != 1 && interactiveComponent.type != 3) {
                        if (!userInterface.contains(name)) {
                            Label text = new Label(interactiveComponent.title, userInterface.getLabelStyle());
                            text.setPosition(positionComponent.getX(), positionComponent.getY());
                            text.setName(name);
                            stage.addActor(text);
                            userInterface.addInteractButton(name, interactiveComponent.listener);
                        }
                    } else {
                        interactiveComponent.listener.interact();
                    }
                } else {
                    removeActor(stage.getActors(), name);
                    removeActor(userInterface.getChildren(), name);
                }
            }
        }

        for (int i = 0; i < mobs.size(); i++) {
            PositionComponent positionComponent = pm.get(mobs.get(i));
            for (int j = 0; j < players.size(); j++) {
                PlayerComponent playerComponent = pcm.get(players.get(j));

                if (playerComponent.camera.frustum.pointInFrustum(positionComponent.getX(), positionComponent.getY(), 0)) {
                    if (!positionComponent.isCameraVisible()) {
                        soundsSystem.playStalkerDetection();
                        positionComponent.setCameraVisible(true);
                    }
                }else
                    positionComponent.setCameraVisible(false);
            }
        }
    }

    private void removeActor(Array<Actor> actors, String actorName) {
        for (Actor actor : actors) {
            if (actor.getName() != null && actor.getName().equals(actorName)) {
                actor.remove();
            }
        }
    }

}
