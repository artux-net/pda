package net.artux.pda.map.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.artux.engine.resource.types.NetFile;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.components.CoreComponent;
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
    private final Label label;
    private final Label progressLabel;

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
        table.setSize(w, 200);
        stage.addActor(table);

        label = new Label(gameMap.getTitle(), labelStyle);
        label.setAlignment(Align.center);
        label.setSize(w, h);
        stage.addActor(label);

        progressLabel = new Label("0%", labelStyle);
        progressLabel.setAlignment(Align.left);
        table.add(progressLabel)
                .left()
                .bottom()
                .fill()
                .pad(50f);
    }

    private void loadMap() {
        assetManager.load(gameMap.getTexture(), NetFile.class);
        assetManager.load(gameMap.getBoundsTexture(), NetFile.class);
        assetManager.load(gameMap.getTilesTexture(), NetFile.class);
    }

    public void resume() {
    }

    @Override
    protected void handleInput() {
        gsm.addInputProcessor(stage);
    }

    @Override
    protected void stop() {
        gsm.removeInputProcessor(stage);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    boolean failed = false;

    @Override
    public void render() {
        try {
            if (!failed && assetManager.isFinished()) {
                gsm.set(coreComponent.getPlayState());
            } else {
                stage.draw();
                if (!failed) {
                    float progress = assetManager.getProgress();
                    progressLabel.setText(df.format(progress));
                }
            }
        } catch (Exception e) {
            label.setText(e.getMessage());

            progressLabel.setText("Click here to exit.");
            progressLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    dataRepository.getPlatformInterface().openPDA();
                }
            });
            failed = true;
        }
    }

    @Override
    public void resize(int w, int h) {
    }

    @Override
    public void dispose() {
    }

}
