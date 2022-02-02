package net.artux.pda.map.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.artux.pda.map.engine.Assets;
import net.artux.pda.map.engine.EntityManager;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.model.LevelBackground;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.ui.UserInterface;


public class PlayState extends State {

    public Stage stage;
    public Stage uistage;

    private Texture background;
    private LevelBackground levelBackground;
    private Texture bounds;

    public AssetManager assetManager;

    private final UserInterface userInterface;

    private static final String tag = "PlayState";

    String cachePath = "cache/";
    EntityManager entityManager;

    public PlayState(final GameStateManager gsm, Batch batch) {
        super(gsm);
        Engine engine = new Engine();

        Gdx.app.debug(tag, "Start play state init");

        Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        assetManager = Assets.get();
        stage = new Stage(viewport, batch);
        uistage = new Stage();

        camera = ((OrthographicCamera) stage.getCamera());
        camera.zoom = 0.5f;

        userInterface = new UserInterface(gsm, assetManager);
        uistage.addActor(userInterface);

        Map map = (Map) gsm.get("map");
        if (map != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(map));

            background = new Texture(Gdx.files.local(cachePath + map.getTextureUri()));
            GlobalData.mapWidth = background.getWidth();
            GlobalData.mapHeight = background.getHeight();

            if (map.getBlurTextureUri() != null) {
                FileHandle fileHandle = Gdx.files.local(cachePath + map.getBlurTextureUri());
                if (fileHandle.exists()) {
                    levelBackground = new LevelBackground(new Texture(fileHandle), camera);
                }
            }

            entityManager = new EntityManager(engine, assetManager, stage, map, getMember(), userInterface, gsm);
        }

        Gdx.app.debug(tag, "State loaded, heap: " + Gdx.app.getNativeHeap());
    }

    @Override
    protected void handleInput() {
        gsm.addInputProcessor(uistage);
        gsm.addInputProcessor(stage);
        gsm.addInputProcessor(new GestureDetector(entityManager));
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
    }

    @Override
    public void render(SpriteBatch batch) {
        float dt = Gdx.app.getGraphics().getDeltaTime();

        stage.getBatch().begin();
        if (levelBackground != null)
            levelBackground.render(batch);

        if (background != null)
            stage.getBatch().draw(background, 0, 0);

        entityManager.update(dt); // TODO make it draw
        stage.getBatch().end();

        stage.draw();
        entityManager.draw(dt);
        uistage.draw();
    }

    @Override
    public void resize(int w, int h) {
        System.out.println("Resized: " + w + " : " + h);
        stage.getViewport().update(w, h, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        Gdx.app.debug(tag, "after dispose stages, heap " + Gdx.app.getNativeHeap());
        if (background != null)
            background.dispose();
        if (bounds != null)
            bounds.dispose();
        if (levelBackground != null)
            levelBackground.dispose();
        Gdx.app.debug(tag, "after dispose textures, heap " + Gdx.app.getNativeHeap());
        Gdx.app.debug(tag, "after dispose font, heap " + Gdx.app.getNativeHeap());
        assetManager.dispose();
        Gdx.app.debug(tag, "after dispose asset manager and font, heap " + Gdx.app.getNativeHeap());
        if (userInterface != null)
            userInterface.dispose();
        Gdx.app.debug(tag, "after dispose ui, heap " + Gdx.app.getNativeHeap());
    }

}
