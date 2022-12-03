package net.artux.pda.map.di.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.AppModule;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.entities.EntityProcessorSystem;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.states.PlayState;
import net.artux.pda.map.states.PreloadState;
import net.artux.pda.map.ui.UserInterface;

import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {EngineModule.class, AppModule.class, UserInterfaceModule.class})
public interface CoreComponent {

    PlayState getPlayState();

    PreloadState getPreloadState();

    Engine getEngine();

    EngineManager getManager();

    @Named("gameStage")
    Stage gameStage();

    @Named("uiStage")
    Stage uiStage();

    Camera getCamera();

    UserInterface getUserInterface();

    GameStateManager getGSM();

    AssetsFinder getAssetsFinder();

    AssetManager getAssetsManager();

    DataRepository getDataRepository();

    EntityProcessorSystem getEntityProcessor();

    EntityBuilder getEntityBuilder();

    Set<Actor> initInterface();

}
