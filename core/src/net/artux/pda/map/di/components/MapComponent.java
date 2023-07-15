package net.artux.pda.map.di.components;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.engine.graphics.postprocessing.PostProcessing;
import net.artux.engine.scenes.SceneManager;
import net.artux.pda.map.di.modules.EngineModule;
import net.artux.pda.map.di.modules.ui.UserInterfaceModule;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.managers.notification.NotificationController;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.content.entities.EntityBuilder;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.ecs.creation.EntityProcessorSystem;
import net.artux.pda.map.managers.ConditionEntityManager;
import net.artux.pda.map.repository.EngineSaver;
import net.artux.pda.map.view.root.UserInterface;

import java.util.Set;

import javax.inject.Named;

import dagger.Component;

/**
 * Интерфейс позволяющий обращатся к карте, движку и другим основным компонентам
 */
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

    SceneManager getSceneManager();

    DataRepository getDataRepository();

    EntityProcessorSystem getEntityProcessor();

    EntityBuilder getEntityBuilder();

    Set<Actor> initInterface();

    World getWorld();

    TiledMap getTiledMap();

    OrthogonalTiledMapRenderer getRenderer();

    ConditionEntityManager getConditionManager();

    PostProcessing getPostProcessing();

    EngineSaver getGameSaver();

    NotificationController getNotificationController();
}
