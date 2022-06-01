package net.artux.pda.map.engine.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.ui.UserInterface;

public class DeadCheckerSystem extends BaseSystem {

    private UserInterface ui;
    private Label.LabelStyle labelStyle;
    private boolean deadMessage;
    private GameStateManager gameStateManager;

    public DeadCheckerSystem(UserInterface userInterface, GameStateManager gameStateManager) {
        super(null);
        this.ui = userInterface;
        this.gameStateManager = gameStateManager;
        labelStyle = userInterface.getLabelStyle();
        labelStyle.fontColor = Color.RED;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

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
