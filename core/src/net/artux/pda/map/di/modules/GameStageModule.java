package net.artux.pda.map.di.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.player.PlayerSystem;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.Logger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;


@Module
public class GameStageModule {

    @Provides
    @PerGameMap
    @Named("gameStage")
    public Stage getStage() {
        return new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    @Provides
    @PerGameMap
    @Named("uiStage")
    public Stage uiStage() {
        ScreenViewport screenViewport = new ScreenViewport();
        screenViewport.setUnitsPerPixel(Math.min(1920.0f / Gdx.graphics.getWidth(), 1080.0f / Gdx.graphics.getHeight()));
        return new Stage(screenViewport);
    }

    @Provides
    @PerGameMap
    public Camera getCamera(@Named("gameStage") Stage stage) {
        return stage.getCamera();
    }

    @Provides
    @PerGameMap
    @Named("uiCamera")
    public Camera getUICamera(@Named("uiStage") Stage stage) {
        return stage.getCamera();
    }


    @Provides
    public Skin getSkin(AssetManager assetManager) {
        Skin skin = assetManager.get("skins/cloud/cloud-form-ui.json");
        skin.add("primary", Colors.primaryColor);
        skin.add("background", Colors.backgroundColor);

        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(assetManager.get("textures/ui/exit.png", Texture.class));
        ImageButton.ImageButtonStyle pauseButtonStyle = new ImageButton.ImageButtonStyle(null,
                null, null, pauseDrawable, pauseDrawable, pauseDrawable);
        skin.add("close", pauseButtonStyle);


        return skin;
    }


    @Provides
    @PerGameMap
    public Logger getLogger(Skin skin, PlayerSystem playerSystem) {
        Logger logger = new Logger(skin);
        logger.putListiner("FPS", Gdx.app.getGraphics(), "getFramesPerSecond");
        logger.putListiner("Native Heap", Gdx.app, "getNativeHeap");
        logger.putListiner("Java Heap", Gdx.app, "getJavaHeap");
        logger.putListiner("Player position", playerSystem, "getPosition");
        logger.putListiner("Здоровье", playerSystem, "getHealthComponent");
        logger.putListiner("Опыт", playerSystem.getDataRepository().getCurrentStoryDataModel(), "getXp");
        logger.putListiner("Params", playerSystem.getDataRepository().getCurrentStoryDataModel(), "getParameters");
        logger.putListiner("Temp", playerSystem.getDataRepository().getCurrentStoryDataModel().getCurrentState(), "toString");

        logger.putListiner("Screen width", Gdx.app.getGraphics(), "getWidth");
        logger.putListiner("Height", Gdx.app.getGraphics(), "getHeight");
        logger.putListiner("Density", Gdx.app.getGraphics(), "getDensity");
        return logger;
    }

}
