package net.artux.pda.map.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.gson.Gson;

import net.artux.pda.map.GdxAdapter;
import net.artux.pda.map.model.Entity;
import net.artux.pda.map.model.Hit;
import net.artux.pda.map.model.Player;
import net.artux.pda.map.model.ServerPlayer;
import net.artux.pda.map.ui.HealthBar;
import net.artux.pda.map.ui.Logger;
import net.artux.pdalib.arena.Action;
import net.artux.pdalib.arena.Position;
import net.artux.pdalib.arena.ServerEntity;
import net.artux.pdalib.arena.ServerState;
import net.artux.pdalib.profile.items.GsonProvider;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.artux.pda.map.GdxAdapter.RUSSIAN_CHARACTERS;
import static net.artux.pda.map.GdxAdapter.RUSSIAN_FONT_NAME;

public class ArenaState extends State {
    public static List<Entity> entities = new ArrayList<>();

    private Player player;
    private Touchpad touchpad;
    public static Stage stage;
    public static Stage uistage;
    private final Texture background;
    private final OrthographicCamera cam;

    private final Button.ButtonStyle textButtonStyle;
    private final BitmapFont font;

    Logger logger;

    int wb;
    int hb;

    PauseState pauseState;
    AssetManager assetManager;

    long heapLoss;

    Gson gson = GsonProvider.getInstance();

    final WebSocketClient socketClient = new WebSocketClient(new URI("ws://192.168.0.200/arena/"+"c532f7c211bce687f13fe1db744bff20a83e0be98c915b2f4f6291f522a2be94")) {

        ServerState serverState;

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("OnOpen - arena");
        }

        @Override
        public void onMessage(String message) {
            serverState = gson.fromJson(message, ServerState.class);
            for (Map.Entry<String, ServerEntity> s : serverState.entities.entrySet()) {
                if (s.getKey().equals(String.valueOf(getMember().getPdaId())))
                    player.setPosition(s.getValue().getPosition().x, s.getValue().getPosition().y);
                else {
                    Actor a = getActor(s.getKey(), stage);
                    if (a == null && assetManager.isFinished()) {
                        ServerPlayer serverPlayer = new ServerPlayer(new Vector2(s.getValue().getPosition().x, s.getValue().getPosition().y), assetManager);
                        serverPlayer.setName(s.getKey());
                        stage.addActor(serverPlayer);
                    } else if (a instanceof ServerPlayer) {
                        ServerPlayer serverPlayer = (ServerPlayer) a;
                        serverPlayer.setPosition(s.getValue().getPosition().x, s.getValue().getPosition().y);
                    }
                }
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("OnClose - arena " + reason + " " + code);
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
        }

    };

    Actor getActor(String name, Stage stage){
        Array<Actor> stageActors = stage.getActors();
        for(int i=0; i<stage.getActors().size; i++){
            Actor a = stageActors.get(i);
            if(a.getName()!=null && a.getName().equals(name))
                return a;
        }
        return null;
    }

    boolean contains(String name, Stage stage){
        for (Actor actor : stage.getActors()) {
            if (actor.getName()!=null && actor.getName().equals(name))
                return true;
        }
        return false;
    }

    public ArenaState(final GameStateManager gsm, Batch batch) throws URISyntaxException {
        super(gsm);

        background = new Texture("servermap.png");

        socketClient.connect();

        font = GdxAdapter.generateFont(RUSSIAN_FONT_NAME, RUSSIAN_CHARACTERS);

        assetManager = new AssetManager();
        assetManager.load("dialog.png", Texture.class);
        assetManager.load("beg2.png", Texture.class);
        assetManager.load("beg1.png", Texture.class);
        assetManager.load("pause.png", Texture.class);
        assetManager.load("quest.png", Texture.class);
        assetManager.load("seller.png", Texture.class);
        assetManager.load("quest1.png", Texture.class);
        assetManager.load("cache.png", Texture.class);
        assetManager.load("direction.png", Texture.class);
        assetManager.load("transfer.png", Texture.class);
        assetManager.load("gg1.png", Texture.class);
        assetManager.load("red.png", Texture.class);
        assetManager.load("green.png", Texture.class);
        assetManager.load("yellow.png", Texture.class);
        assetManager.load("touchpad/knob.png", Texture.class);
        assetManager.load("touchpad/back.png", Texture.class);
        assetManager.finishLoading();

        long before = Gdx.app.getNativeHeap();

        Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        stage = new Stage(viewport, batch);
        uistage = new Stage();

        textButtonStyle = new Button.ButtonStyle();
        textButtonStyle.up  =  new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));

        initPlayer();
        Button.ButtonStyle runButtonStyle = new Button.ButtonStyle();
        runButtonStyle.up  = new TextureRegionDrawable(assetManager.get("beg2.png", Texture.class));
        runButtonStyle.down  = new TextureRegionDrawable(assetManager.get("beg1.png", Texture.class));
        runButton = new Button(runButtonStyle);
        runButton.setPosition(w - w/12,h/12);
        runButton.setSize(h/10,h/10);
        runButton.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                player.run = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                player.run = false;
                super.touchUp(event, x, y, pointer, button);
            }
        });
        uistage.addActor(runButton);

        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle();
        pauseButtonStyle.up = new TextureRegionDrawable(assetManager.get("pause.png", Texture.class));
        pauseButton = new Button(pauseButtonStyle);
        pauseButton.setPosition(w - w/11, h - h/11);
        pauseButton.setSize(h/12,h/12);
        pauseButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                gsm.push(pauseState);
            }
        });
        uistage.addActor(pauseButton);

        cam = new OrthographicCamera(w, h);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.zoom -=0.5;
        cam.update();


        initTouchPad(w,h);

        ((OrthographicCamera)stage.getCamera()).zoom -= 0.5f;

        long after = Gdx.app.getNativeHeap();
        heapLoss = after-before;
        System.out.println("Play init " + (after-before));
    }

    HealthBar healthBar;

    private void initPlayer(){
        player = new Player(new Vector2(), getMember(), assetManager);
        entities.add(player);
        stage.addActor(player);
        healthBar = new HealthBar(player);
        healthBar.setHeight(h/12);
        healthBar.setWidth(w/4);
        healthBar.setX(w/12);
        healthBar.setY(h-h/8-20);
        healthBar.setScale(1);
        uistage.addActor(healthBar);

        logger = new Logger(player,3, (int) (h/10));
    }


    private void initTouchPad(float w, float h) {
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();

        style.knob = new TextureRegionDrawable(assetManager.get("touchpad/knob.png", Texture.class));
        style.knob.setMinHeight(170);
        style.knob.setMinWidth(170);
        style.background = new TextureRegionDrawable(assetManager.get("touchpad/back.png", Texture.class));
        touchpad = new Touchpad(10, style);
        touchpad.setBounds(50, 50, h/2.5f, h/2.5f );

        Thread thread = new Thread(){

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    float deltaX = touchpad.getKnobPercentX();
                    float deltaY = touchpad.getKnobPercentY();
                    Action action = new Action("move", gson.toJson(new Position(deltaX, deltaY)));
                    if (socketClient.isOpen() && (deltaX!=0 || deltaY!=0)) {
                        System.out.println(gson.toJson(action));
                        socketClient.send(gson.toJson(action));
                    }
                }
            }
        };
        thread.start();

        uistage.addActor(touchpad);
    }

    public static void registerHit(Hit hit){
        stage.addActor(hit);
    }

    boolean checkEnemy(int id1, int id2){
        //TODO
        return false;
    }

    @Override
    protected void handleInput() {
        gsm.addInputProcessor(uistage);
    }

    @Override
    protected void stop() {
        gsm.removeInputProcessor(uistage);
    }

    @Override
    public void update(float dt) {
        uistage.act(dt);
        stage.act(dt);

        logger.update();

    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(130, 169, 130, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();

        stage.getViewport().getCamera().position.set(player.getPosition(), 0);
        stage.getBatch().draw(background, 0, 0);

        stage.getBatch().end();
        stage.draw();

        uistage.draw();
        logger.render();
    }
    Button button = null;
    Button runButton;
    Button pauseButton;

    @Override
    public void resize(int w, int h) {
        cam.viewportWidth = w;
        cam.viewportHeight = h;
        System.out.println("Resized: " + w + " : " + h);
        stage.getViewport().update(w, h, true);
        touchpad.setPosition(w/12, h/10);
    }

    @Override
    public void dispose() {
        System.out.println("Dispose PlayState");
        long before = Gdx.app.getNativeHeap();
        System.out.println("Before: " );
        logger.dispose();
        healthBar.dispose();
        stage.dispose();
        uistage.dispose();
        background.dispose();
        font.dispose();
        assetManager.dispose();
        player.dispose();
        pauseState.dispose();

        long after = Gdx.app.getNativeHeap();
        System.out.println("Play dispose " + (after-before));
        System.out.println("Total heap loss " + heapLoss);
    }
}
