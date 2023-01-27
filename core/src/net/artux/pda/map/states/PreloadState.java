package net.artux.pda.map.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.CoreComponent;
import net.artux.engine.resource.types.NetFile;
import net.artux.pda.model.map.GameMap;

import java.text.DecimalFormat;

import javax.inject.Inject;


public class PreloadState extends State {

    private final CoreComponent coreComponent;
    private final AssetManager assetManager;
    private final BitmapFont font;
    private final Stage stage;
    private final GameMap gameMap;
    private final DecimalFormat df = new DecimalFormat("##.##%");

    @Inject
    public PreloadState(final GameStateController gsm, DataRepository dataRepository, CoreComponent coreComponent) {
        super(gsm, dataRepository);
        this.coreComponent = coreComponent;
        font = coreComponent.getAssetsFinder().getFontManager().getFont(22);
        assetManager = coreComponent.getAssetsManager();
        gameMap = dataRepository.getGameMap();

        loadMap();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        stage = new Stage();
        Table table = new Table();
        table.setPosition(0, 0);
        table.setSize(200, 200);
        stage.addActor(table);

        Label label = new Label(gameMap.getTitle(), labelStyle);
        label.setAlignment(Align.center);
        label.setSize(w, h);
        stage.addActor(label);

        Label progressLabel = new Label("0%", labelStyle);
        progressLabel.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                float progress = assetManager.getProgress();
                progressLabel.setText(df.format(progress));
                return false;
            }
        });
        table.add(progressLabel)
                .left()
                .bottom()
                .pad(50f);
    }

    private void loadMap() {
        assetManager.load(gameMap.getTexture(), NetFile.class);
        assetManager.load(gameMap.getBoundsTexture(), NetFile.class);
        assetManager.load(gameMap.getTilesTexture(), NetFile.class);
    }

    public void resume() {}

    @Override
    protected void handleInput() {}

    @Override
    protected void stop() {}

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render() {
        if (assetManager.isFinished()) {
            gsm.set(coreComponent.getPlayState());
        } else {
            stage.draw();
        }
    }

    @Override
    public void resize(int w, int h) {}

    @Override
    public void dispose() {}

}
