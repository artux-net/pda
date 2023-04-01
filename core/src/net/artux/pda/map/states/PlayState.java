package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.engine.graphics.postprocessing.PostProcessing;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.utils.di.components.CoreComponent;
import net.artux.pda.map.utils.di.components.DaggerMapComponent;
import net.artux.pda.map.utils.di.components.MapComponent;
import net.artux.pda.model.map.GameMap;

import javax.inject.Inject;

public class PlayState extends State {

    private final static String TAG = "PlayState";
    private final GameMap gameMap;
    public Stage stage;
    public Stage uistage;

    private final EngineManager engineManager;
    private final CoreComponent coreComponent;

    private final World world;
    private final OrthogonalTiledMapRenderer renderer;
    private final PostProcessing postProcessing;

    @Inject
    public PlayState(final GameStateController gsm, DataRepository dataRepository, GameMap gameMap, CoreComponent coreComponent) {
        super(gsm, dataRepository);
        this.gameMap = gameMap;
        this.coreComponent = coreComponent;

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

        mapComponent.getUserInterface();
        mapComponent.initInterface();
    }

    @Override
    public void resume() {
        Gdx.input.getInputProcessor()
                .touchUp(0, 0, 0, 0); //resets all inputs after pause
        Gdx.app.log(TAG, "OnResume");
        GameMap map = dataRepository.getGameMap();

        if (gameMap.getId() != map.getId()) {
            Gdx.app.log(TAG, "Update map, old: " + gameMap.getId() + " new map: " + map.getId());
            gsm.set(coreComponent.getPreloadState());
        }
        engineManager.updateOnlyPlayer();
    }

    @Override
    protected void handleInput() {
        gsm.addInputProcessor(uistage);
        gsm.addInputProcessor(stage);
        gsm.addInputProcessor(new GestureDetector(engineManager.getGestureListener()));
    }

    @Override
    protected void stop() {
        gsm.removeInputProcessor(stage);
        gsm.removeInputProcessor(uistage);
    }

    @Override
    public void update(float dt) {
        uistage.act(dt);
        stage.act(dt);
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
        postProcessing.dispose();
        stage.dispose();
        renderer.dispose();
        uistage.dispose();
        engineManager.dispose();
    }

}
