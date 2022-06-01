package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StatesComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.ui.UserInterface;

public class InteractionSystem extends BaseSystem {

    private Stage stage;
    private final UserInterface userInterface;
    private SoundsSystem soundsSystem;
    private CameraSystem cameraSystem;

    public InteractionSystem(Stage stage, UserInterface userInterface) {
        super(Family.all(InteractiveComponent.class, PositionComponent.class).get());
        this.stage = stage;
        this.userInterface = userInterface;
    }

    private ImmutableArray<Entity> mobs;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SpriteComponent> scm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<InteractiveComponent> im = ComponentMapper.getFor(InteractiveComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        mobs = engine.getEntitiesFor(Family.all(MoodComponent.class, PositionComponent.class, StatesComponent.class).get());
        soundsSystem = engine.getSystem(SoundsSystem.class);
        cameraSystem = engine.getSystem(CameraSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size; i++) {
            PositionComponent positionComponent = pm.get(entities.get(i));
            InteractiveComponent interactiveComponent = im.get(entities.get(i));

            PositionComponent playerPosition = pm.get(player);

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
                    interactiveComponent.listener.interact(userInterface);
                }
            } else {
                removeActor(stage.getActors(), name);
                removeActor(userInterface.getChildren(), name);
            }

        }
        int counter = 0;
        for (int i = 0; i < mobs.size(); i++) {
            Entity mob = mobs.get(i);
            PositionComponent positionComponent = pm.get(mob);
            SpriteComponent spriteComponent = scm.get(mob);

            PlayerComponent playerComponent = pcm.get(player);
            PositionComponent playerPosition = pm.get(player);

            float dst = positionComponent.getPosition().dst(playerPosition.getPosition());

            boolean near = true;

            if (dst < 200)
                spriteComponent.setAlpha(1);
            else if (dst > 270) {
                spriteComponent.setAlpha(0);
                near = false;
            }
            else
                spriteComponent.setAlpha((300 - dst) / 100);

            if (near && playerComponent.camera.frustum.pointInFrustum(positionComponent.getX(), positionComponent.getY(), 0)) {
                if (!positionComponent.isCameraVisible() && !cameraSystem.detached) {
                    soundsSystem.playStalkerDetection();
                }
                positionComponent.setCameraVisible(true);
            } else
                positionComponent.setCameraVisible(false);
            if (positionComponent.isCameraVisible())
                counter++;
        }
        PlayerData.visibleEntities = counter;
    }

    private void removeActor(Array<Actor> actors, String actorName) {
        for (Actor actor : actors) {
            if (actor.getName() != null && actor.getName().equals(actorName)) {
                actor.remove();
            }
        }
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }
}
