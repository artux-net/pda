package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.engine.resource.types.NetFile;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.components.CoreComponent;
import net.artux.pda.map.di.components.DaggerMapComponent;
import net.artux.pda.map.di.components.MapComponent;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.view.LevelBackgroundImage;
import net.artux.pda.model.map.GameMap;

import javax.inject.Inject;

public class PlayState extends State {

    private final static String TAG = "PlayState";
    private final GameMap gameMap;
    public Stage stage;
    public Stage uistage;

    private LevelBackgroundImage levelBackgroundImage;
    private final EngineManager engineManager;
    private final AssetManager assetManager;
    private final CoreComponent coreComponent;

    private final World world;
    private final OrthogonalTiledMapRenderer renderer;

    Texture background;

    @Inject
    public PlayState(final GameStateController gsm, DataRepository dataRepository, GameMap gameMap, CoreComponent coreComponent) {
        super(gsm, dataRepository);
        this.gameMap = gameMap;
        this.coreComponent = coreComponent;

        AssetsFinder assetsFinder = coreComponent.getAssetsFinder();
        assetManager = assetsFinder.getManager();
        assetManager.finishLoading();//make sure everything loaded

        MapComponent mapComponent = DaggerMapComponent.builder()
                .coreComponent(coreComponent)
                .build();


        world = mapComponent.getWorld();
        engineManager = mapComponent.getManager();
        stage = mapComponent.gameStage();
        uistage = mapComponent.uiStage();
        renderer = mapComponent.getRenderer();
        renderer.setView((OrthographicCamera) stage.getCamera());

        mapComponent.getUserInterface();
        mapComponent.initInterface();
        loadMap();
    }

    private void loadMap() {
        background = (Texture) assetManager.get(gameMap.getTexture(), NetFile.class).file;
        GlobalData.mapWidth = background.getWidth();
        GlobalData.mapHeight = background.getHeight();
        /*Image mapTexture = new Image(background);
        if (!stage.getActors().contains(mapTexture, false))
            stage.addActor(mapTexture);
        mapTexture.setZIndex(1);*/

        Texture levelTexture = assetManager.get("textures/defaults/blur.png", Texture.class);
        if (levelBackgroundImage == null) {
            levelBackgroundImage = new LevelBackgroundImage(levelTexture, stage.getCamera());
            //stage.addActor(levelBackgroundImage);
            levelBackgroundImage.setZIndex(0);
        }
    }


    public GameMap getGameMap() {
        return gameMap;
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
        GestureDetector.GestureListener gestureListener = engineManager.getGestureListener();
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return gestureListener.touchDown(x, y, pointer, button);
            }
        });
        gsm.addInputProcessor(new GestureDetector(gestureListener));
    }

    @Override
    protected void stop() {
        gsm.removeInputProcessor(stage);
        gsm.removeInputProcessor(uistage);
    }

    float minDt = Float.MAX_VALUE;

    @Override
    public void update(float dt) {
        uistage.act(dt);
        stage.act(dt);
        dt = Math.min(minDt, dt);
        world.step(dt, 3, 2);
        renderer.setView((OrthographicCamera) stage.getCamera());
        engineManager.update(dt);
    }

    @Override
    public void render() {
        renderer.render();
        stage.draw();

        stage.getBatch().begin();
        engineManager.draw(stage.getBatch(), 1);
        stage.getBatch().end();
        uistage.draw(); // ui always last
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, false);
        //uistage.getViewport().update(w, h, false);
    }

    @Override
    public void dispose() {
        assetManager.unload(gameMap.getTexture());

        stage.dispose();
        renderer.dispose();
        uistage.dispose();
        engineManager.dispose();
    }

}
