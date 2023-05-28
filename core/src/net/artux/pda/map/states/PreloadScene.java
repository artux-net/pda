package net.artux.pda.map.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.artux.engine.scenes.SceneController;
import net.artux.engine.scenes.Scene;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.utils.di.components.CoreComponent;
import net.artux.pda.model.map.GameMap;

import java.text.DecimalFormat;

import javax.inject.Inject;


public class PreloadScene extends Scene {

    private final CoreComponent coreComponent;
    private final AssetManager assetManager;
    private final Stage stage;
    private final DecimalFormat df = new DecimalFormat("##.##%");
    private final Label progressLabel;

    @Inject
    public PreloadScene(final SceneController gsm, DataRepository dataRepository,
                        CoreComponent coreComponent) {
        super(gsm);
        this.coreComponent = coreComponent;
        BitmapFont font = coreComponent.getAssetsFinder().getFontManager().getFont(22);
        assetManager = coreComponent.getAssetsManager();
        GameMap gameMap = dataRepository.getGameMap();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        stage = new Stage();
        Table table = new Table();
        table.setPosition(0, 0);
        table.setSize(w, 200);

        Label label = new Label(gameMap.getTitle(), labelStyle);
        label.setAlignment(Align.center);
        label.setSize(w, h);
        stage.addActor(label);
        stage.addActor(table);

        progressLabel = new Label("0%", labelStyle);
        progressLabel.setAlignment(Align.left);
        table.add(progressLabel)
                .left()
                .bottom()
                .fill()
                .pad(50f);
    }


    public void resume() {
    }

    @Override
    public void hide() {

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
    public void render(float dt) {
        try {
            if (assetManager.isFinished()) {
                sceneController.set(coreComponent.getPlayState());
            } else {
                stage.draw();
                float progress = assetManager.getProgress();
                progressLabel.setText(df.format(progress));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorScene errorState = coreComponent.getErrorState();
            errorState.setThrowable(e);
            sceneController.set(errorState);
        }
    }

    @Override
    public void resize(int w, int h) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void dispose() {
    }

}
