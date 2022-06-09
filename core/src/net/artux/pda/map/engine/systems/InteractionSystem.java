package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StatesComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.bars.Slot;

import java.util.LinkedList;
import java.util.List;

public class InteractionSystem extends BaseSystem {

    private Stage stage;
    private final UserInterface userInterface;
    private SoundsSystem soundsSystem;
    private CameraSystem cameraSystem;
    private AssetManager assetManager;

    public InteractionSystem(Stage stage, UserInterface userInterface, AssetManager assetManager) {
        super(Family.all(InteractiveComponent.class, PositionComponent.class).get());
        this.assetManager = assetManager;
        this.stage = stage;
        this.userInterface = userInterface;
    }

    private ImmutableArray<Entity> mobs;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SpriteComponent> scm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<InteractiveComponent> im = ComponentMapper.getFor(InteractiveComponent.class);
    private List<String> activeActions = new LinkedList<>();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        mobs = engine.getEntitiesFor(Family.all(MoodComponent.class, PositionComponent.class, StatesComponent.class).get());
        soundsSystem = engine.getSystem(SoundsSystem.class);
        cameraSystem = engine.getSystem(CameraSystem.class);

        //userInterface.getAssistantBlock().row();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        activeActions.clear();
        for (int i = 0; i < entities.size; i++) {
            PositionComponent positionComponent = pm.get(entities.get(i));
            InteractiveComponent interactiveComponent = im.get(entities.get(i));

            PositionComponent playerPosition = pm.get(player);

            String name = "q-" + interactiveComponent.title;
            if (playerPosition.getPosition().dst(positionComponent.getPosition()) < 35f) {
                if (interactiveComponent.type != InteractiveComponent.Type.ACTION) {
                    activeActions.add(name);
                    if (userInterface.getControlBlock().findActor(name) == null) {
                        Label text = new Label(interactiveComponent.title, userInterface.getLabelStyle());
                        text.setPosition(positionComponent.getX(), positionComponent.getY());
                        text.setName(name);
                        stage.addActor(text);
                        userInterface.getControlBlock().row();

                        String icon;
                        switch (interactiveComponent.type) {

                            case FINDING:
                                icon = "ui/icons/icon_search.png";
                                break;
                            case TRANSFER:
                                icon = "ui/icons/ic_transfer.png";
                                break;
                            default:
                                icon = "ui/icons/icon_dialog.png";
                                break;
                        }

                        userInterface.addInteractButton(name, icon, interactiveComponent.listener);
                    }
                } else {
                    interactiveComponent.listener.interact(userInterface);
                }
            }
        }
        removeActorsFromStage(stage);
        removeActor(userInterface.getControlBlock());
        int counter = 0;
        for (int i = 0; i < mobs.size(); i++) {
            Entity mob = mobs.get(i);
            PositionComponent positionComponent = pm.get(mob);
            SpriteComponent spriteComponent = scm.get(mob);

            PlayerComponent playerComponent = pcm.get(player);
            PositionComponent playerPosition = pm.get(player);

            float dst = positionComponent.getPosition().dst(playerPosition.getPosition());

            boolean near = true;

            if (dst < 200) {
                spriteComponent.setAlpha(1);
                counter++;
            } else if (dst > 270) {
                spriteComponent.setAlpha(0);
                near = false;
            } else {
                spriteComponent.setAlpha((300 - dst) / 100);
                counter++;
            }

            if (near && playerComponent.camera.frustum.pointInFrustum(positionComponent.getX(), positionComponent.getY(), 0)) {
                if (!positionComponent.isCameraVisible() && !cameraSystem.detached) {
                    soundsSystem.playStalkerDetection();
                }
                positionComponent.setCameraVisible(true);
            } else
                positionComponent.setCameraVisible(false);

        }
        PlayerData.visibleEntities = counter;
    }

    public void addButton(String icon, InteractiveComponent.InteractListener listener) {
        userInterface.getControlBlock().row();
        userInterface.addInteractButton("", icon, listener);
    }

    public void addButton(String icon, EventListener listener) {
        userInterface.getControlBlock().row();
        userInterface.addInteractButton("", icon, listener);
    }

    private void removeActor(Table container) {
        for (Actor actor : container.getChildren()) {
            if (actor.getName() != null && !activeActions.contains(actor.getName()) && actor.getName().contains("q-")) {
                Cell<Actor> cell = container.getCell(actor);
                actor.remove();
                // remove cell from table
                container.getCells().removeValue(cell, true);
                container.invalidate();
            }
        }
    }

    private void removeActorsFromStage(Stage stage) {
        for (Actor actor : stage.getActors()) {
            if (actor.getName() != null && actor.getName().contains("q-") && !activeActions.contains(actor.getName())) {
                actor.remove();
            }
        }
    }

    public UserInterface getUserInterface() {
        return userInterface;
    }
}
