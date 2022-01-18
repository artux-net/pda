package net.artux.pda.map.states;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import net.artux.pda.map.model.Assets;
import net.artux.pda.map.model.Entity;
import net.artux.pda.map.model.EntityManager;
import net.artux.pda.map.model.Hit;
import net.artux.pda.map.model.LevelBackground;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Mob;
import net.artux.pda.map.model.Player;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.model.Quest;
import net.artux.pda.map.model.Spawn;
import net.artux.pda.map.model.Text;
import net.artux.pda.map.model.Transfer;
import net.artux.pda.map.model.TransferPoint;
import net.artux.pda.map.model.components.SpriteComponent;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pdalib.Checker;
import net.artux.pdalib.profile.Story;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;


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
        camera.zoom -= 0.5f;

        Map map = (Map) gsm.get("map");

        if (map != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(map));

            background = new Texture(Gdx.files.local(cachePath + map.getTextureUri()));

            if (map.getBlurTextureUri() != null) {
                FileHandle fileHandle = Gdx.files.local(cachePath + map.getBlurTextureUri());
                if (fileHandle.exists()) {
                    levelBackground = new LevelBackground(new Texture(fileHandle), camera);
                }
            }
        }

        userInterface = new UserInterface(gsm, assetManager);
        uistage.addActor(userInterface);

        entityManager = new EntityManager(engine, assetManager, stage, map, getMember(), userInterface);
        stage.addCaptureListener(new InputListener());
        stage.addListener(entityManager);

        Gdx.app.debug(tag, "State loaded, heap: " + Gdx.app.getNativeHeap());
    }

    private void addPoint(Point point) {
        stage.addActor(new Quest(point, assetManager));
        /*if (point.type < 2 || point.type > 3)//TODO
            player.setDirection(point.getPosition());*/
    }

    @Override
    protected void handleInput() {
        gsm.addInputProcessor(uistage);
        gsm.addInputProcessor(stage);
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

        entityManager.update(dt);

        stage.getBatch().end();
        stage.draw();

        uistage.draw();

        entityManager.draw(dt);
    }

    @Override
    public void resize(int w, int h) {
        System.out.println("Resized: " + w + " : " + h);
        stage.getViewport().update(w, h, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        uistage.dispose();
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
