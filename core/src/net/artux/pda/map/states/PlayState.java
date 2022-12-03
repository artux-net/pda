package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.core.CoreComponent;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.engine.LevelBackground;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.model.map.GameMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayState extends State {

    private final static String TAG = "PlayState";
    private GameMap gameMap;
    private Image mapTexture;
    public Stage stage;
    public Stage uistage;

    private LevelBackground levelBackground;
    private final EngineManager engineManager;
    private final AssetsFinder assetsFinder;
    private final UserInterface userInterface;

    @Inject
    public PlayState(final GameStateManager gsm, DataRepository dataRepository, CoreComponent coreComponent) {
        super(gsm, dataRepository);
        engineManager = coreComponent.getManager();
        assetsFinder = coreComponent.getAssetsFinder();
        stage = coreComponent.gameStage();
        uistage = coreComponent.uiStage();
        userInterface = coreComponent.getUserInterface();
        coreComponent.initInterface();
    }

    @Override
    public void resume() {
        Gdx.app.log(TAG, "OnResume");
        GameMap map = dataRepository.getGameMap();
        Gdx.input.getInputProcessor().touchUp(0,0,0,0);
        //resets all inputs after pause
        if (gameMap == null || gameMap.getId() != map.getId()) {
            //todo update map
            if (gameMap != null) {
                Gdx.app.log(TAG, "Update map, old: " + gameMap.getId() + " new map: " + map.getId());

            } else {
                Gdx.app.log(TAG, "Set map: " + map.getId());
            }
            gameMap = map;
            long loadTime = TimeUtils.millis();

            Texture background = assetsFinder.getLocal(map.getTexture());
            GlobalData.mapWidth = background.getWidth();
            GlobalData.mapHeight = background.getHeight();
            mapTexture = new Image(background);
            if (!stage.getActors().contains(mapTexture, false))
                stage.addActor(mapTexture);

            Texture levelTexture = assetsFinder.getLocal(map.getBlurTexture());
            if (levelTexture != null) {
                levelBackground = new LevelBackground(levelTexture, stage.getCamera());
            }
            Gdx.app.log("Main textures", "Loading took " + (TimeUtils.millis() - loadTime) + " ms.");

            //update entities
            //update spawns
            //update points
            engineManager.updateEverything();
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
        stage.dispose();
        uistage.dispose();
        engineManager.dispose();
        assetsFinder.dispose();
        if (userInterface != null)
            userInterface.dispose();
    }

}
