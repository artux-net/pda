package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.artux.pda.map.engine.components.BodyComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.RelationalSpriteComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.data.PlayerData;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.ui.blocks.ControlBlock;

import java.util.LinkedList;
import java.util.List;

public class InteractionSystem extends BaseSystem {

    private Stage stage;
    private final UserInterface userInterface;
    private SoundsSystem soundsSystem;
    private CameraSystem cameraSystem;

    public InteractionSystem(Stage stage, UserInterface userInterface) {
        super(Family.all(InteractiveComponent.class, BodyComponent.class).get());
        this.stage = stage;
        this.userInterface = userInterface;
    }

    private ImmutableArray<Entity> mobs;

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<RelationalSpriteComponent> rsm = ComponentMapper.getFor(RelationalSpriteComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<InteractiveComponent> im = ComponentMapper.getFor(InteractiveComponent.class);
    private List<String> activeActions = new LinkedList<>();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        mobs = engine.getEntitiesFor(Family.all(SpriteComponent.class, MoodComponent.class, BodyComponent.class).get());
        soundsSystem = engine.getSystem(SoundsSystem.class);
        cameraSystem = engine.getSystem(CameraSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        activeActions.clear();
        for (int i = 0; i < getEntities().size(); i++) {
            Vector2 positionComponent = pm.get(getEntities().get(i)).getBody().getPosition();
            final InteractiveComponent interactiveComponent = im.get(getEntities().get(i));

            Vector2 playerPosition = pm.get(player).getBody().getPosition();

            String name = "q-" + interactiveComponent.type;
            if (playerPosition.dst(positionComponent) < 35f) {
                if (interactiveComponent.type != InteractiveComponent.Type.ACTION) {
                    activeActions.add(name);
                    if (userInterface.getControlBlock().findActor(name) == null) {
                        final Label text = new Label(interactiveComponent.title, userInterface.getLabelStyle());
                        text.setPosition(positionComponent.x, positionComponent.y);
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

                        userInterface.addInteractButton(name, icon, new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                interactiveComponent.listener.interact(userInterface);
                                text.remove();
                                ControlBlock controlBlock = userInterface.getControlBlock();
                                Cell<Actor> cell = controlBlock.getCell(actor);
                                actor.remove();
                                controlBlock.getCells().removeValue(cell, true);
                                controlBlock.invalidate();
                            }
                        });
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
            Vector2 positionComponent = pm.get(mob).getBody().getPosition();


            Vector2 playerPosition = pm.get(player).getBody().getPosition();

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

            /*if (near && cameraSystem.getCamera().frustum.pointInFrustum(positionComponent.getX(), positionComponent.getY(), 0)) {
                if (!positionComponent.isCameraVisible() && !cameraSystem.detached) {
                    soundsSystem.playStalkerDetection();
                }
                positionComponent.setCameraVisible(true);
            } else
                positionComponent.setCameraVisible(false);*/
            //todo

        }
        PlayerData.visibleEntities = counter;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

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
