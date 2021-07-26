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
import net.artux.pda.map.model.ServerPlayer;
import net.artux.pda.map.model.server.ServerEntity;
import net.artux.pda.map.ui.HealthBar;
import net.artux.pda.map.ui.Logger;
import net.artux.pdalib.arena.Action;
import net.artux.pdalib.arena.Connection;
import net.artux.pdalib.arena.Position;
import net.artux.pdalib.arena.ServerState;
import net.artux.pdalib.profile.items.GsonProvider;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.artux.pda.map.GdxAdapter.RUSSIAN_CHARACTERS;
import static net.artux.pda.map.GdxAdapter.RUSSIAN_FONT_NAME;

public class ArenaState extends State {
    public static List<Entity> entities = new ArrayList<>();

    private ServerPlayer player;
    private Touchpad moveTouchpad;
    private Touchpad shootTouchpad;
    public static Stage stage;
    public static Stage uistage;
    private final Texture background;
    private final OrthographicCamera cam;

    private final BitmapFont font;

    Logger logger;

    AssetManager assetManager;

    long heapLoss;

    Gson gson = GsonProvider.getInstance();

    ServerState lastServerState;

    final WebSocketClient socketClient;
    float lastJoyX;
    float lastJoyY;
    long lastUpdate;
    static long ping;
    long serverRenderTime;
    int lastFrameId;
    public static long getPing() {
        return ping;
    }

    public long getServerRenderTime() {
        return serverRenderTime;
    }

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

    public ArenaState(final GameStateManager gsm, Batch batch, Connection connection) throws URISyntaxException {
        super(gsm);

        background = new Texture("servermap.png");



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

        socketClient = new WebSocketClient(new URI("ws://"+connection.ip+"/pda/arena/"+connection.token + "/" +connection.session)) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("OnOpen - arena");
            }

            @Override
            public void onMessage(String message) {
                ping = System.currentTimeMillis() - lastUpdate;

                lastUpdate = System.currentTimeMillis();
                lastServerState = gson.fromJson(message, ServerState.class);

                for (net.artux.pdalib.arena.ServerEntity s : lastServerState.entities) {
                    if (s.pdaId == getMember().getPdaId()) {
                        player.setVelocity(s.getVelocity().x, s.getVelocity().y);
                        player.setNextPosition(s.getPosition().x, s.getPosition().y);
                    } else {
                        Actor a = getActor("a" + s.pdaId, stage);
                        if (a == null && assetManager.isFinished()) {
                            ServerEntity serverEntity = new ServerEntity(new Vector2(s.getPosition().x, s.getPosition().y), assetManager);
                            serverEntity.setName("a" + s.pdaId);
                            stage.addActor(serverEntity);
                            System.out.println("add new actor with name: " + "a" + s.pdaId);
                        } else if (a instanceof ServerEntity) {
                            ServerEntity serverEntity = (ServerEntity) a;
                            serverEntity.setVelocity(s.getVelocity());
                            if (lastServerState.frame - lastFrameId > 10)
                                serverEntity.setPosition(s.getPosition().x, s.getPosition().y);
                        }
                    }
                }
                if (lastServerState.frame - lastFrameId > 10){
                    lastFrameId = lastServerState.frame;
                }
                serverRenderTime = System.currentTimeMillis() - lastUpdate;
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
        System.out.println("Arena connecting to " + "ws://"+connection.ip+"/pda/arena/"+connection.token + "/" +connection.session);


        long before = Gdx.app.getNativeHeap();

        Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        stage = new Stage(viewport, batch);
        uistage = new Stage();

        Button.ButtonStyle textButtonStyle = new Button.ButtonStyle();
        textButtonStyle.up  =  new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));

        initPlayer();
        socketClient.connect();
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
                HashMap<String, String> data = new HashMap<>();
                data.put("openPda", "");
                gsm.getPlatformInterface().send(data);
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
        player = new ServerPlayer(this, new Vector2(), getMember(), assetManager);
        player.setServer(true);
        entities.add(player);
        stage.addActor(player);
        healthBar = new HealthBar(player);
        healthBar.setHeight(h/12);
        healthBar.setWidth(w/4);
        healthBar.setX(w/12);
        healthBar.setY(h-h/8-20);
        healthBar.setScale(1);
        uistage.addActor(healthBar);

        logger = new Logger(this, player,3, (int) (h-h/10));
    }


    private void initTouchPad(float w, float h) {
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();

        style.knob = new TextureRegionDrawable(assetManager.get("touchpad/knob.png", Texture.class));
        style.knob.setMinHeight(170);
        style.knob.setMinWidth(170);
        style.background = new TextureRegionDrawable(assetManager.get("touchpad/back.png", Texture.class));
        moveTouchpad = new Touchpad(10, style);
        moveTouchpad.setBounds(50, 50, h/2.5f, h/2.5f );

        shootTouchpad = new Touchpad(10, style);
        shootTouchpad.setBounds(w -50, 50, h/2.5f, h/2.5f );

        Thread thread = new Thread(){

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    float deltaX = moveTouchpad.getKnobPercentX();
                    float deltaY = moveTouchpad.getKnobPercentY();
                    Action action = new Action("move", gson.toJson(new Position(deltaX, deltaY)));
                    if (socketClient.isOpen() && (deltaX!= lastJoyX || deltaY!= lastJoyY)) {
                        lastJoyX = deltaX;
                        lastJoyY = deltaY;
                        socketClient.send(gson.toJson(action));
                    }
                }
            }
        };
        thread.start();

        Thread thread1 = new Thread(){

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    float deltaX = shootTouchpad.getKnobPercentX();
                    float deltaY = shootTouchpad.getKnobPercentY();
                    Action action = new Action("shoot", gson.toJson(new Position(deltaX, deltaY)));
                    if (socketClient.isOpen() && (deltaX!=0 || deltaY!=0)) {
                        socketClient.send(gson.toJson(action));
                    }
                }
            }
        };
        thread1.start();

        uistage.addActor(moveTouchpad);
        uistage.addActor(shootTouchpad);
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
        shootTouchpad.setPosition(w/12, h/10);
    }

    @Override
    public void dispose() {
        socketClient.close();
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

        long after = Gdx.app.getNativeHeap();
        System.out.println("Play dispose " + (after-before));
        System.out.println("Total heap loss " + heapLoss);
    }
}
