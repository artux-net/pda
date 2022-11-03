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
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.LevelBackground;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.map.ui.UserInterface;


public class PlayState extends State {

    public Stage stage;
    public Stage uistage;

    private LevelBackground levelBackground;
    private final EngineManager engineManager;
    private final AssetsFinder assetsFinder;
    private final UserInterface userInterface;

    public PlayState(final GameStateManager gsm, DataRepository dataRepository) {
        super(gsm, dataRepository);

        assetsFinder = new AssetsFinder();
        AssetManager assetManager = assetsFinder.getManager();
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        uistage = new Stage();

        userInterface = new UserInterface(gsm, dataRepository, assetsFinder, stage.getCamera());
        uistage.addActor(userInterface);

        GameMap map = dataRepository.getGameMap();
        if (map != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Gdx.app.debug("PlayState", gson.toJson(map));

            long loadTime = TimeUtils.millis();

            Texture background = assetsFinder.getLocal(map.getTexture());
            GlobalData.mapWidth = background.getWidth();
            GlobalData.mapHeight = background.getHeight();
            stage.addActor(new Image(background));

            Texture levelTexture = assetsFinder.getLocal(map.getBlurTexture());
            if (levelTexture != null) {
                levelBackground = new LevelBackground(levelTexture, stage.getCamera());
            }
            Gdx.app.log("Main textures", "Loading took " + (TimeUtils.millis() - loadTime) + " ms.");
        }
        engineManager = new EngineManager(assetsFinder, stage, this);
        if (userInterface != null)
            userInterface.enableDebug(assetManager, engineManager.getEngine());
    }

    public UserInterface getUserInterface() {
        return userInterface;
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
