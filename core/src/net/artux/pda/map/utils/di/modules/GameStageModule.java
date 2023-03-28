package net.artux.pda.map.utils.di.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.engine.ecs.systems.MapOrientationSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.map.view.Logger;
import net.artux.pda.map.view.UserInterface;

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
    public UserInterface userInterface(Skin skin, AssetsFinder finder, MapOrientationSystem mapOrientationSystem, @Named("uiStage") Stage uiStage, @Named("gameStage") Stage stage) {
        UserInterface userInterface = new UserInterface(skin, finder, mapOrientationSystem.getMapBorder(), uiStage.getCamera(), stage.getCamera());
        uiStage.addActor(userInterface);
        return userInterface;
    }

    @Provides
    public Skin getSkin(AssetManager assetManager) {
        return assetManager.get("skins/cloud/cloud-form-ui.json");
    }

    @Provides
    @PerGameMap
    public Logger getLogger(Skin skin, PlayerSystem playerSystem) {
        Logger logger = new Logger(skin);
        logger.put("FPS", logger, "getFrameRate");
        logger.put("Native Heap", Gdx.app, "getNativeHeap");
        logger.put("Java Heap", Gdx.app, "getJavaHeap");
        logger.put("Player position", playerSystem, "getPosition");
        logger.put("Здоровье", playerSystem, "getHealth");
        logger.put("Опыт", playerSystem.getDataRepository().getCurrentStoryDataModel(), "getXp");
        logger.put("Params", playerSystem.getDataRepository().getCurrentStoryDataModel(), "getParameters");
        logger.put("Temp", playerSystem.getDataRepository().getCurrentStoryDataModel().getCurrentState(), "toString");
        //logger.put("Stories stat", Arrays.toString(playerSystem.getPlayerMember().getData().getStories().toArray()), "toString");

        logger.put("Screen width", Gdx.app.getGraphics(), "getWidth");
        logger.put("Height", Gdx.app.getGraphics(), "getHeight");
        logger.put("Density", Gdx.app.getGraphics(), "getDensity");
        return logger;
    }

}
