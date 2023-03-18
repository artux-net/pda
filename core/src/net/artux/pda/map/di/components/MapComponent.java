package net.artux.pda.map.di.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.engine.graphics.postprocessing.PostProcessing;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.modules.EngineModule;
import net.artux.pda.map.di.modules.ui.UserInterfaceModule;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ConditionEntityManager;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.engine.ecs.systems.EntityProcessorSystem;
import net.artux.pda.map.engine.helpers.EntityBuilder;
import net.artux.pda.map.states.GameStateController;
import net.artux.pda.map.view.UserInterface;

import java.util.Set;

import javax.inject.Named;

import dagger.Component;

@PerGameMap
@Component(modules = {EngineModule.class, UserInterfaceModule.class}, dependencies = CoreComponent.class)
public interface MapComponent extends CoreComponent {

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

    World getWorld();

    TiledMap getTiledMap();

    OrthogonalTiledMapRenderer getRenderer();

    ConditionEntityManager getConditionManager();

    PostProcessing getPostProcessing();

}
