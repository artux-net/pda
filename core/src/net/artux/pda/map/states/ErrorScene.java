package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;

import net.artux.engine.scenes.Scene;
import net.artux.engine.scenes.SceneController;
import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.utils.di.components.CoreComponent;
import net.artux.pda.model.map.GameMap;

import java.time.Instant;

import javax.inject.Inject;


public class ErrorScene extends Scene {

    private final Stage stage;
    private final Label label;

    @Inject
    public ErrorScene(final SceneController gsm, DataRepository dataRepository,
                      CoreComponent coreComponent, LocaleBundle localeBundle) {
        super(gsm);
        BitmapFont font = coreComponent.getAssetsFinder().getFontManager().getFont(22);
        GameMap gameMap = dataRepository.getGameMap();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        stage = new Stage();
        Table table = new Table();
        table.setPosition(0, 0);
        table.setSize(w, 200);

        label = new Label(gameMap.getTitle(), labelStyle);
        label.setAlignment(Align.center);
        label.setSize(w, h);
        stage.addActor(label);
        stage.addActor(table);

        Label exitLabel = new Label(localeBundle.get("main.click.exit"), labelStyle);
        exitLabel.setAlignment(Align.left);
        table.add(exitLabel)
                .left()
                .bottom()
                .fill()
                .pad(50f);

        exitLabel.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                dataRepository.getPlatformInterface().openPDA();
            }
        });

        Label restartLabel = new Label(localeBundle.get("main.click.restart"), labelStyle);
        restartLabel.setAlignment(Align.left);
        table.add(restartLabel)
                .left()
                .bottom()
                .fill()
                .pad(50f);

        restartLabel.addListener(new ActorGestureListener() {
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                gsm.set(coreComponent.getPlayState());
            }
        });
    }

    public void setThrowable(Throwable throwable) {
        throwable.printStackTrace();
        label.setText(Instant.now().toString() + " error: " + throwable.getMessage());
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            label.setText(label.getText() + "\n" + stackTraceElement);
        }
        Gdx.app.getApplicationLogger().error("Core", "Map Core error", throwable);
    }

    public void resume() {
    }

    @Override
    protected void handleInput() {
        sceneController.addInputProcessor(stage);
    }

    @Override
    protected void stop() {
        sceneController.removeInputProcessor(stage);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render() {
        stage.draw();
    }

    @Override
    public void resize(int w, int h) {
    }

    @Override
    public void dispose() {
    }

}
