package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.StalkerComponent;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.ui.UserInterface;

import java.util.Random;

public class DeadCheckerSystem extends BaseSystem {

    private UserInterface ui;
    private Label.LabelStyle labelStyle;
    private boolean deadMessage;
    private GameStateManager gameStateManager;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    private AssetManager assetManager;
    private Random random = new Random();

    public DeadCheckerSystem(UserInterface userInterface, GameStateManager gameStateManager, AssetManager assetManager) {
        super(Family.all(HealthComponent.class, PositionComponent.class).get());
        this.ui = userInterface;
        this.gameStateManager = gameStateManager;
        this.assetManager = assetManager;
        labelStyle = userInterface.getLabelStyle();
        labelStyle.fontColor = Color.RED;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (int i = 0; i<entities.size; i++){
            Entity entity = entities.get(i);

            HealthComponent healthComponent = hm.get(entity);
            PositionComponent positionComponent = pm.get(entity);

            if (healthComponent.isDead()) {
                final Entity deadEntity = new Entity();
                deadEntity.add(new PositionComponent(positionComponent.getPosition()))
                        .add(new SpriteComponent(assetManager.get("gray.png", Texture.class), 4, 4));

                if (entity != player) {
                    StalkerComponent stalkerComponent = entity.getComponent(StalkerComponent.class);
                    deadEntity.add(new InteractiveComponent("Обыскать: " + stalkerComponent.getName(), 0, new InteractiveComponent.InteractListener() {
                        @Override
                        public void interact(UserInterface userInterface) {
                            PlayerSystem playerSystem = getEngine().getSystem(PlayerSystem.class);
                            playerSystem.addMedicine(random.nextInt(3));
                            playerSystem.addRadiation(random.nextInt(2));
                            getEngine().removeEntity(deadEntity);//TODO
                        }
                    })).add(stalkerComponent);
                }

                getEngine().removeEntity(entity);
                getEngine().addEntity(deadEntity);
            }
        }
        if (!getEngine().getEntities().contains(player, false)) {
            if (!deadMessage) {
                Group deadMessageGroup = new Group();

                TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
                style.font = labelStyle.font;
                style.fontColor = Color.RED;

                TextButton textButton = new TextButton("Игра провалена!", style);
                TextButton textButton1 = new TextButton("Для продолжения нажмите здесь..", style);
                textButton1.moveBy(0, -50);

                deadMessageGroup.addActor(textButton);
                deadMessageGroup.addActor(textButton1);
                deadMessageGroup.setPosition(ui.getStage().getWidth() / 2 - textButton1.getWidth() / 2, ui.getStage().getHeight() / 2 - textButton1.getHeight() / 2);
                deadMessageGroup.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        gameStateManager.getPlatformInterface().restart();
                        super.clicked(event, x, y);
                    }
                });
                ui.addActor(deadMessageGroup);

                deadMessage = true;
            }
        }
    }

}
