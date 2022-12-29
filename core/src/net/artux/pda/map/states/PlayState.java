package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.CoreComponent;
import net.artux.pda.map.di.core.DaggerMapComponent;
import net.artux.pda.map.di.core.MapComponent;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.engine.LevelBackground;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.map.utils.NetFile;
import net.artux.pda.model.map.GameMap;

import java.util.Timer;

import javax.inject.Inject;

public class PlayState extends State {

    private final static String TAG = "PlayState";
    private GameMap gameMap;
    private Image mapTexture;
    public Stage stage;
    public Stage uistage;

    private LevelBackground levelBackground;
    private final EngineManager engineManager;
    private final AssetsFinder assetsFinder;
    private final AssetManager assetManager;
    private final UserInterface userInterface;
    private final CoreComponent coreComponent;
    private final MapComponent mapComponent;
    private final Timer timer;
    Texture background;

    @Inject
    public PlayState(final GameStateController gsm, DataRepository dataRepository, GameMap gameMap, CoreComponent coreComponent) {
        super(gsm, dataRepository);
        this.gameMap = gameMap;
        this.coreComponent = coreComponent;

        assetsFinder = coreComponent.getAssetsFinder();
        assetManager = assetsFinder.getManager();
        assetManager.finishLoading();//make sure everything loaded

        mapComponent = DaggerMapComponent.builder()
                .coreComponent(coreComponent)
                .build();

        timer = mapComponent.getTimer();
        engineManager = mapComponent.getManager();///n
        stage = mapComponent.gameStage();//n
        uistage = mapComponent.uiStage();//n
        userInterface = mapComponent.getUserInterface();
        mapComponent.initInterface();

        loadMap();
    }

    private void loadMap() {
        background = (Texture) assetManager.get(gameMap.getTexture(), NetFile.class).file;
        GlobalData.mapWidth = background.getWidth();
        GlobalData.mapHeight = background.getHeight();
        mapTexture = new Image(background);
        if (!stage.getActors().contains(mapTexture, false))
            stage.addActor(mapTexture);

        Texture levelTexture = (Texture) assetManager.get(gameMap.getBlurTexture(), NetFile.class).file;
        if (levelBackground == null) {
            levelBackground = new LevelBackground(levelTexture, stage.getCamera());
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
            //todo update map
            gsm.set(coreComponent.getPreloadState());

            //update entities
            //update spawns
            //update points
            //engineManager.updateEverything();
        }
        engineManager.updateOnlyPlayer();//update player
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

    @Override
    public void update(float dt) {
        uistage.act(dt);
        stage.act(dt);
        engineManager.update(dt);
    }

    @Override
    public void render() {
        SpriteBatch batch = (SpriteBatch) stage.getBatch();
        batch.begin();
        if (levelBackground != null)
            levelBackground.render(batch);
        batch.draw(background, 0, 0);
        batch.end();

        stage.draw();
        stage.getBatch().begin();
        engineManager.draw(stage.getBatch(), 1);
        stage.getBatch().end();
        uistage.draw(); // ui always last
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, false);
    }

    @Override
    public void dispose() {
        assetManager.unload(gameMap.getTexture());
        assetManager.unload(gameMap.getTilesTexture());
        assetManager.unload(gameMap.getBoundsTexture());
        assetManager.unload(gameMap.getBlurTexture());

        stage.dispose();
        uistage.dispose();
        engineManager.dispose();
        timer.purge();
        timer.cancel();
        if (userInterface != null)
            userInterface.dispose();
    }

}
