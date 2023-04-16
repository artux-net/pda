package net.artux.pda.map.utils.di.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.engine.graphics.postprocessing.PostProcessing;
import net.artux.engine.scenes.SceneController;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.content.EntityBuilder;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.engine.ecs.systems.EntityProcessorSystem;
import net.artux.pda.map.managers.ConditionEntityManager;
import net.artux.pda.map.utils.di.modules.EngineModule;
import net.artux.pda.map.utils.di.modules.ui.UserInterfaceModule;
import net.artux.pda.map.utils.di.scope.PerGameMap;
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

    SceneController getGSC();

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
