package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;

import net.artux.engine.graphics.postprocessing.PostProcessing;
import net.artux.engine.scenes.Scene;
import net.artux.engine.scenes.SceneController;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.utils.di.components.CoreComponent;
import net.artux.pda.map.utils.di.components.DaggerMapComponent;
import net.artux.pda.map.utils.di.components.MapComponent;
import net.artux.pda.model.map.GameMap;

import org.int4.dirk.api.Injector;
import org.int4.dirk.di.Injectors;
import org.int4.dirk.library.SingletonScopeResolver;

import javax.inject.Inject;

public class PlayScene extends Scene {

    private final static String TAG = "PlayState";
    private final GameMap gameMap;
    public Stage stage;
    public Stage uistage;

    private final EngineManager engineManager;
    private final CoreComponent coreComponent;

    private final World world;
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer renderer;
    private final PostProcessing postProcessing;
    private final DataRepository dataRepository;

    @Inject
    public PlayScene(final SceneController sceneController, DataRepository dataRepository, GameMap gameMap, CoreComponent coreComponent) {
        super(sceneController);
        this.gameMap = gameMap;
        this.coreComponent = coreComponent;
        this.dataRepository = dataRepository;

        MapComponent mapComponent = DaggerMapComponent.builder()
                .coreComponent(coreComponent)
                .build();

        world = mapComponent.getWorld();
        engineManager = mapComponent.getManager();
        stage = mapComponent.gameStage();
        uistage = mapComponent.uiStage();
        renderer = mapComponent.getRenderer();
        renderer.setView((OrthographicCamera) stage.getCamera());
        postProcessing = mapComponent.getPostProcessing();
        tiledMap = mapComponent.getTiledMap();

        mapComponent.getUserInterface();
        mapComponent.initInterface();
        addStage(stage);
        addStage(uistage);
    }

    @Override
    public void resume() {
        Gdx.app.getApplicationLogger().log(TAG, "OnResume");
        GameMap map = dataRepository.getGameMap();

        if (gameMap.getId() != map.getId()) {
            Gdx.app.getApplicationLogger().log(TAG, "Update map, old: " + gameMap.getId() + " new map: " + map.getId());
            sceneController.set(coreComponent.getPreloadState());
        }
        engineManager.updateOnlyPlayer();
    }

    @Override
    protected void handleInput() {
        super.handleInput();
        sceneController.addInputProcessor(new GestureDetector(engineManager.getGestureListener()));
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        world.step(1 / 90f, 1, 1);
        engineManager.update(dt);
    }

    @Override
    public void render() {
        Camera camera = stage.getCamera();
        Vector3 leftBottom = camera.frustum.planePoints[0];
        postProcessing.begin();
        renderer.setView(camera.combined, leftBottom.x, leftBottom.y, camera.viewportWidth, camera.viewportHeight);
        renderer.render();
        stage.draw();

        stage.getBatch().begin();
        engineManager.draw(stage.getBatch(), 1);
        stage.getBatch().end();

        postProcessing.end();
        postProcessing.process();

        uistage.draw(); // ui always last
    }

    @Override
    public void resize(int w, int h) {
        //stage.getViewport().update(w, h, false);
        //uistage.getViewport().update(w, h, false);
    }

    @Override
    public void dispose() {
        super.dispose();
        Timer.instance().clear();
        postProcessing.dispose();
        renderer.dispose();
        tiledMap.dispose();
        engineManager.dispose();
    }

}
