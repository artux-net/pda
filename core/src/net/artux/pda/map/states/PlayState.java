package net.artux.pda.map.states;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Player;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.model.Spawn;

import java.util.HashMap;


public class PlayState extends State {

    private Map map;

    private Player player;
    private Touchpad touchpad;
    private Stage stage;
    private Texture background;
    private Texture blur;
    private OrthographicCamera cam;

    private Touchpad.TouchpadStyle style;
    private Button.ButtonStyle textButtonStyle;
    private HashMap<String, Texture> textures =  new HashMap<>();

    public PlayState(GameStateManager gsm, Batch batch) {
        super(gsm);
        map = gsm.getMap();
        Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport, batch);
        background = map.getTexture();
        blur = new Texture("maps/map_escape_bg.jpg");

        background.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        TextureRegion imgTextureRegion = new TextureRegion(background);
        imgTextureRegion.setRegion(0,0,background.getWidth()*3,background.getHeight()*3);

        initializePlayer(map);
        textures.put("0", new Texture("quest.png"));

        Skin buttonSkin = new Skin();
        buttonSkin.add("up-d", new Texture("dialog.png"));
        textButtonStyle = new Button.ButtonStyle();
        textButtonStyle.up  = buttonSkin.getDrawable("up-d");

        buttonSkin.add("up", new Texture("beg2.png"));
        buttonSkin.add("down", new Texture("beg1.png"));
        buttonSkin.add("pause", new Texture("pause.png"));
        Button.ButtonStyle runButtonStyle = new Button.ButtonStyle();
        runButtonStyle.up  = buttonSkin.getDrawable("up");
        runButtonStyle.down  = buttonSkin.getDrawable("down");


        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

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
        stage.addActor(runButton);

        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle();
        pauseButtonStyle.up = buttonSkin.getDrawable("pause");
        pauseButton = new Button(pauseButtonStyle);
        pauseButton.setPosition(w - w/11, h - h/11);
        pauseButton.setSize(h/12,h/12);
        stage.addActor(pauseButton);

        cam = new OrthographicCamera(w, h);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.zoom -=0.5;
        cam.update();

        initTouchpad(w,h);
        for (Spawn spawn : map.getSpawns()){
            spawn.create(batch);
        }
    }

    private void initializePlayer(Map map){
        player = new Player(map.getPlayerPosition());
        player.setBoundsTexture(map.getBoundsTexture());
    }


    private void initTouchpad(float w, float h) {
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

        stage.addActor(touchpad);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        player.update(dt);
        for (Spawn spawn:map.getSpawns())
            spawn.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(169, 169, 169, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
        cam.position.set(player.getPosition(),0);

        batch.setProjectionMatrix(cam.combined);

        batch.begin();
        
        batch.draw(blur,-2000,0);
        batch.draw(background, 0, 0);

        player.draw(batch);
        drawQuests(batch);
        for (Spawn spawn:map.getSpawns())
            spawn.draw(batch);
        batch.end();
        stage.act();
        stage.draw();

    }
    Button button = null;
    Button runButton = null;
    Button pauseButton = null;

    void drawQuests(Batch batch){
        for(final Point point: map.getPoints()) {
            if (point.type==0){
                batch.draw(textures.get("0"), point.getPosition().x, point.getPosition().y);
            }
            if (Math.sqrt(sq(point.getPosition().x - player.getPosition().x)
                    + sq(point.getPosition().y - player.getPosition().y)) < 35f) {
                float w = Gdx.graphics.getWidth();
                float h = Gdx.graphics.getHeight();
                if (!contains("b")) {
                    button = new Button(textButtonStyle);
                    button.setPosition(w - w/12, 2.5f * h / 12);
                    button.setSize(h/10,h/10);
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                             gsm.getPlatformInterface().send(point.getData());
                        }
                    });
                    button.setName("b");
                    stage.addActor(button);
                }
            } else {
                for(Actor actor : stage.getActors()){
                    if(actor.getName()!=null && actor.getName().equals("b"))
                        actor.remove();
                }
            }
        }
    }

    boolean contains(String name){
        for (Actor actor : stage.getActors()) {
            if (actor.getName()!=null && actor.getName().equals(name))
                return true;
        }
        return false;
    }

    double sq(double a){
        return a*a;
    }

    @Override
    public void resize(int w, int h) {
        cam.viewportWidth = w;
        cam.viewportHeight = h;
        System.out.println(w + " : " + h);
        stage.getViewport().update(w, h, true);
        touchpad.setPosition(w/12, h/10);
    }

    @Override
    public void dispose() {

    }
}
