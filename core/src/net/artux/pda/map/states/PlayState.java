package net.artux.pda.map.states;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.artux.pda.map.model.Entity;
import net.artux.pda.map.model.Hit;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Player;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.model.Quest;
import net.artux.pda.map.model.Spawn;
import net.artux.pda.map.model.Text;
import net.artux.pda.map.model.Transfer;
import net.artux.pda.map.model.TransferPoint;
import net.artux.pda.map.ui.FrameRate;
import net.artux.pda.map.ui.HealthBar;
import net.artux.pdalib.Checker;
import net.artux.pdalib.profile.Story;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.graphics.Texture.TextureWrap.Repeat;


public class PlayState extends State {

    public static List<Entity> entities = new ArrayList<>();

    private Player player;
    private Touchpad touchpad;
    public static Stage stage;
    public static Stage uistage;
    private Texture background;
    private Texture blur;
    private OrthographicCamera cam;

    private Touchpad.TouchpadStyle style;
    private Button.ButtonStyle textButtonStyle;

    FrameRate frameRate;

    int wb;
    int hb;

    PauseState pauseState;
    Skin skin;

    public PlayState(final GameStateManager gsm, Batch batch) {
        super(gsm);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Map map = gsm.getMap();
        pauseState = new PauseState(gsm, map);
        Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport, batch);
        uistage = new Stage();


        background = new Texture(Gdx.files.absolute(map.getTextureUri()));
        pauseState.setBackground(background);
        if (map.getBlurTextureUri()!=null)
        if (Gdx.files.absolute(map.getBlurTextureUri()).exists()) {
            blur = new Texture(Gdx.files.absolute(map.getBlurTextureUri()));
            blur.setWrap(Repeat, Repeat);
            wb = background.getWidth()/2 - blur.getWidth()/2;
            hb = background.getHeight()/2 - blur.getHeight()/2;
        }
        background.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        TextureRegion imgTextureRegion = new TextureRegion(background);
        imgTextureRegion.setRegion(0,0,background.getWidth()*3,background.getHeight()*3);

        initPlayer(map);
        frameRate = new FrameRate(player);

        skin = new Skin();
        skin.add("up-d", new Texture("dialog.png"));
        skin.add("up", new Texture("beg2.png"));
        skin.add("down", new Texture("beg1.png"));
        skin.add("pause", new Texture("pause.png"));
        skin.add("quest", new Sprite(new Texture("quest.png")));
        skin.add("transfer", new Sprite(new Texture("transfer.png")));

        textButtonStyle = new Button.ButtonStyle();
        textButtonStyle.up  = skin.getDrawable("up-d");


        Button.ButtonStyle runButtonStyle = new Button.ButtonStyle();
        runButtonStyle.up  = skin.getDrawable("up");
        runButtonStyle.down  = skin.getDrawable("down");
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
        pauseButtonStyle.up = skin.getDrawable("pause");
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
        System.out.println("spawns: " + map.getSpawns().size());
        for (Spawn spawn : map.getSpawns()){
            System.out.println("Create spawn");
            spawn.create();
        }

        ((OrthographicCamera)stage.getCamera()).zoom -= 0.5f;



        for(final Point point: map.getPoints()) {
            if (Checker.check(point.getCondition(), getMember()))
            if (point.getData().containsKey("chapter")){
                int storyId = Integer.parseInt(getMember().getData().getTemp().get("currentStory"));
                for (Story story : getMember().getData().getStories()) {
                    if (story.getStoryId() == storyId
                            && (Integer.parseInt(point.getData().get("chapter")) == story.getLastChapter()
                            || Integer.parseInt(point.getData().get("chapter")) == 0))
                            addPoint(point);
                }
            }else
                addPoint(point);

        }
        for (Transfer transfer : map.getTransfers()){
            stage.addActor(new TransferPoint(transfer, skin));
        }
    }

    private void addPoint(Point point){
        if (point.type < 4 && point.type >= 0) {
            stage.addActor(new Quest(point, skin));
        }
    }

    private void initPlayer(Map map){
        player = new Player(map.getPlayerPosition(), getMember());
        if (Gdx.files.absolute(map.getBoundsTextureUri()).exists())
            player.setBoundsTexture(new Texture(Gdx.files.absolute(map.getBoundsTextureUri())));
        entities.add(player);
        stage.addActor(player);
        HealthBar healthBar = new HealthBar(player);
        healthBar.setHeight(h/8);
        healthBar.setWidth(w/4);
        healthBar.setX(w/12);
        healthBar.setY(h-h/8-20);
        healthBar.setScale(1);
        uistage.addActor(healthBar);
    }


    private void initTouchPad(float w, float h) {
        Skin skin = new Skin();
        skin.add("knob", new Texture("touchpad/knob.png"));
        skin.add("background", new Texture("touchpad/back.png"));
        style = new Touchpad.TouchpadStyle();

        style.knob = skin.getDrawable("knob");
        style.knob.setMinHeight(170);
        style.knob.setMinWidth(170);
        style.background = skin.getDrawable("background");
        touchpad = new Touchpad(10, style);
        touchpad.setBounds(50, 50, h/2.5f, h/2.5f );
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float deltaX = ((Touchpad) actor).getKnobPercentX();
                float deltaY = ((Touchpad) actor).getKnobPercentY();

                player.setVelocity(deltaX, deltaY);
            }
        });

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

        frameRate.update();
        for (int i = 0; i<stage.getActors().size; i++){
            Actor actor = stage.getActors().get(i);

            if (actor instanceof Hit){
                Hit hit = (Hit) actor;
                for (Actor actor1 : stage.getActors()){
                    if (actor1 instanceof Entity){
                        Entity entity = (Entity) actor1;
                        if (entity.getSprite().getBoundingRectangle().overlaps(hit.getSprite().getBoundingRectangle())
                                && !hit.author.equals(entity)){
                            entity.damage(hit.getDamage());
                            stage.getActors().removeIndex(i);
                            break;
                        }
                    }
                }
            }else if (actor instanceof Quest){
                final Quest point  = (Quest) actor;
                String name = "q-"+point.hashCode();
                if (distance(point.getPosition(), player.getPosition()) < 35f) {
                    if (point.type!=1 && point.type!=3) {
                        if (!containsUi(name)) {
                            button = new Button(textButtonStyle);
                            button.setPosition(w - w / 12, 2.5f * h / 12);
                            button.setSize(h / 10, h / 10);
                            button.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    gsm.getPlatformInterface().send(point.getData());
                                }
                            });
                            button.setName(name);
                            Text text =new Text(point.title, point.getPosition());
                            text.setName(name);
                            stage.addActor(text);
                            uistage.addActor(button);
                        }
                    }else {
                        System.out.println("send");
                        gsm.getPlatformInterface().send(point.getData());
                    }
                } else {
                    for(Actor buttons : uistage.getActors()){
                        if(buttons.getName()!=null && buttons.getName().equals(name)) {
                            buttons.remove();
                        }
                    }
                    for(Actor buttons : stage.getActors()){
                        if(buttons.getName()!=null && buttons.getName().equals(name)) {
                            buttons.remove();
                        }
                    }
                }

            }else if (actor instanceof TransferPoint){
                final TransferPoint point  = (TransferPoint) actor;
                String name = "t-"+point.hashCode();
                if (distance(point.getPosition(), player.getPosition()) < 35f) {
                    if (!containsUi(name)) {
                        button = new Button(textButtonStyle);
                        button.setPosition(w - w/12, 2.5f * h / 12);
                        button.setSize(h/10,h/10);
                        button.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                gsm.getPlatformInterface().send(point.getData());
                            }
                        });
                        button.setName(name);
                        Text text =new Text(point.getTitle(), point.getPosition());
                        text.setName(name);
                        stage.addActor(text);
                        uistage.addActor(button);
                    }
                } else {
                    for(Actor buttons : uistage.getActors()){
                        if(buttons.getName()!=null && buttons.getName().equals(name)) {
                            buttons.remove();
                        }
                    }
                    for(Actor buttons : stage.getActors()){
                        if(buttons.getName()!=null && buttons.getName().equals(name)) {
                            buttons.remove();
                        }
                    }
                }
            }
        }

        for (int i = 0; i<entities.size(); i++)
            for (int j=0; j<entities.size(); j++)
                if (distance(entities.get(i).getPosition(), entities.get(j).getPosition())<50f)
                    if (checkEnemy(entities.get(i).id, entities.get(j).id)){
                        Entity entity = entities.get(i);
                        entity.setEnemy(entities.get(j));
                        entities.set(i, entity);

                        entity = entities.get(j);
                        entity.setEnemy(entities.get(i));
                        entities.set(j, entity);
                    }

    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(130, 169, 130, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //drawQuests(batch);
        /*for (Actor actor : stage.getActors())
            actor.draw(batch,0);*/

        stage.getBatch().begin();

        stage.getViewport().getCamera().position.set(player.getPosition(), 0);
        if (blur!=null)
            stage.getBatch().draw(blur,wb, hb);
        stage.getBatch().draw(background, 0, 0);

        stage.getBatch().end();
        stage.draw();

        uistage.draw();
        frameRate.render();
    }
    Button button = null;
    Button runButton;
    Button pauseButton;

    boolean contains(String name){
        for (Actor actor : stage.getActors()) {
            if (actor.getName()!=null && actor.getName().equals(name))
                return true;
        }
        return false;
    }

    boolean containsUi(String name){
        for (Actor actor : uistage.getActors()) {
            if (actor.getName()!=null && actor.getName().equals(name))
                return true;
        }
        return false;
    }

    public static double sq(double a){
        return a*a;
    }

    public static double distance(Vector2 p1, Vector2 p2){
        return Math.sqrt(sq(p1.x - p2.x)
                + sq(p1.y - p2.y));
    }

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
        frameRate.dispose();
    }

}
