package net.artux.pda.map.di.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.ui.Logger;
import net.artux.pda.map.ui.UserInterface;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class GameStageModule {

    @Provides
    @Singleton
    @Named("gameStage")
    public Stage getStage() {
        return new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    @Provides
    @Singleton
    public Camera getCamera(@Named("gameStage") Stage stage) {
        return stage.getCamera();
    }

    @Provides
    @Named("uiStage")
    public Stage uiStage(UserInterface userInterface) {
        Stage stage = new Stage();
        stage.addActor(userInterface);
        return stage;
    }

    @Provides
    public Skin getSkin(AssetManager assetManager) {
        return assetManager.get("skins/cloud/cloud-form-ui.json");
    }

    @Provides
    public Logger getLogger(Skin skin, PlayerSystem playerSystem) {
        Logger logger = new Logger(skin);
        logger.put("FPS", this, "getFrameRate");
        logger.put("Native Heap", Gdx.app, "getNativeHeap");
        logger.put("Java Heap", Gdx.app, "getJavaHeap");
        logger.put("Player position", playerSystem, "getPosition");
        logger.put("Здоровье", playerSystem, "getHealth");
        logger.put("Опыт", playerSystem.getPlayerComponent().gdxData, "getXp");
        logger.put("Params", playerSystem.getPlayerComponent().gdxData, "getParameters");
        logger.put("Temp", playerSystem.getPlayerComponent().gdxData.getCurrentState(), "toString");
        //logger.put("Stories stat", Arrays.toString(playerSystem.getPlayerMember().getData().getStories().toArray()), "toString");

        logger.put("Screen width", Gdx.app.getGraphics(), "getWidth");
        logger.put("Height", Gdx.app.getGraphics(), "getHeight");
        logger.put("Density", Gdx.app.getGraphics(), "getDensity");
        return logger;
    }


}
