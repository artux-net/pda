package net.artux.pda.map.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.CoreComponent;
import net.artux.pda.map.utils.NetFile;
import net.artux.pda.model.map.GameMap;

import java.text.DecimalFormat;

import javax.inject.Inject;


public class PreloadState extends State {

    private final CoreComponent coreComponent;
    private final AssetManager assetManager;
    private final BitmapFont font;
    private final Stage stage;
    private final DecimalFormat df = new DecimalFormat("##.##%");

    @Inject
    public PreloadState(final GameStateController gsm, DataRepository dataRepository, CoreComponent coreComponent) {
        super(gsm, dataRepository);
        this.coreComponent = coreComponent;
        font = coreComponent.getAssetsFinder().getFontManager().getFont(22);
        stage = new Stage();
        assetManager = coreComponent.getAssetsManager();

        loadMap();
    }

    private void loadMap() {
        GameMap gameMap = dataRepository.getGameMap();
        assetManager.load(gameMap.getTexture(), NetFile.class);
        assetManager.load(gameMap.getBoundsTexture(), NetFile.class);
        assetManager.load(gameMap.getBlurTexture(), NetFile.class);
        assetManager.load(gameMap.getTilesTexture(), NetFile.class);
    }

    public void resume() {

    }

    @Override
    protected void handleInput() {

    }

    @Override
    protected void stop() {

    }


    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {
        Batch batch = stage.getBatch();
        batch.begin();
        if (assetManager.update()) {
            gsm.set(coreComponent.getPlayState());
        } else {
            float progress = assetManager.getProgress();
            font.draw(batch, df.format(progress), 50, 50);
        }
        batch.end();
    }

    @Override
    public void resize(int w, int h) {

    }

    @Override
    public void dispose() {

    }

}
