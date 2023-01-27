package net.artux.pda.map.di.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.CoreComponent;
import net.artux.pda.map.di.core.ui.UserInterfaceModule;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.ConditionEntityManager;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.entities.EntityProcessorSystem;
import net.artux.pda.map.states.GameStateController;
import net.artux.pda.map.ui.UserInterface;

import java.util.Set;

import javax.inject.Named;

import dagger.Component;

@PerGameMap
@Component(modules = {EngineModule.class, UserInterfaceModule.class}, dependencies = CoreComponent.class)
public interface MapComponent extends CoreComponent{

    Engine getEngine();

    EngineManager getManager();

    @Named("gameStage")
    Stage gameStage();

    @Named("uiStage")
    Stage uiStage();

    Camera getCamera();

    UserInterface getUserInterface();

    GameStateController getGSC();

    DataRepository getDataRepository();

    EntityProcessorSystem getEntityProcessor();

    EntityBuilder getEntityBuilder();

    Set<Actor> initInterface();

    ConditionEntityManager getConditionManager();

}
