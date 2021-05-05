package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Player;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.model.Text;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pdalib.Checker;
import net.artux.pdalib.profile.Story;

import java.util.HashMap;

public class UserInterface extends Group implements Disposable {

    Player player;
    GameStateManager gsm;

    HealthBar healthBar;
    public Logger logger;
    float w = Gdx.graphics.getWidth();
    float h = Gdx.graphics.getHeight();
    Group menu;
    boolean isMenuOpen = false;

    public UserInterface(GameStateManager gsm, Player gamer, AssetManager assetManager, BitmapFont font){
        this.player = gamer;
        this.gsm = gsm;



        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();

        style.knob = new TextureRegionDrawable(assetManager.get("touchpad/knob.png", Texture.class));
        style.knob.setMinHeight(170);
        style.knob.setMinWidth(170);
        style.background = new TextureRegionDrawable(assetManager.get("touchpad/back.png", Texture.class));
        Touchpad touchpad = new Touchpad(10, style);
        touchpad.setPosition(Gdx.graphics.getWidth()/12, Gdx.graphics.getHeight()/10);
        touchpad.setBounds(50, 50, Gdx.graphics.getHeight()/2.5f, Gdx.graphics.getHeight()/2.5f );
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float deltaX = ((Touchpad) actor).getKnobPercentX();
                float deltaY = ((Touchpad) actor).getKnobPercentY();

                UserInterface.this.player.setVelocity(deltaX, deltaY);
            }
        });
        addActor(touchpad);

        Button.ButtonStyle runButtonStyle = new Button.ButtonStyle();
        runButtonStyle.up  = new TextureRegionDrawable(assetManager.get("beg2.png", Texture.class));
        runButtonStyle.down  = new TextureRegionDrawable(assetManager.get("beg1.png", Texture.class));
        Button runButton = new Button(runButtonStyle);
        runButton.setPosition(11*Gdx.graphics.getWidth()/12,Gdx.graphics.getHeight()/12);
        runButton.setSize(Gdx.graphics.getHeight()/10,Gdx.graphics.getHeight()/10);
        runButton.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                UserInterface.this.player.run = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                UserInterface.this.player.run = false;
                super.touchUp(event, x, y, pointer, button);
            }
        });


        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle();
        pauseButtonStyle.up = new TextureRegionDrawable(assetManager.get("pause.png", Texture.class));
        pauseButtonStyle.down = new TextureRegionDrawable(assetManager.get("pause.png", Texture.class));
        pauseButtonStyle.over = new TextureRegionDrawable(assetManager.get("pause.png", Texture.class));
        Button pauseButton = new Button(pauseButtonStyle);
        pauseButton.setPosition(10*Gdx.graphics.getWidth()/11, 10*Gdx.graphics.getHeight()/11);
        pauseButton.setSize(Gdx.graphics.getHeight()/12,Gdx.graphics.getHeight()/12);
        pauseButton.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.clicked(event, x, y);
                System.out.println("touched pause - user interface");
                HashMap<String, String> data = new HashMap<>();
                data.put("openPda", "");
                UserInterface.this.gsm.getPlatformInterface().send(data);
            }
        });

        Button.ButtonStyle occupationsButtonStyle = new Button.ButtonStyle();
        occupationsButtonStyle.up = new TextureRegionDrawable(assetManager.get("occupations.png", Texture.class));
        Button menuButton = new Button(occupationsButtonStyle);
        menuButton.setPosition(9*Gdx.graphics.getWidth()/11, 10*Gdx.graphics.getHeight()/11);
        menuButton.setSize(Gdx.graphics.getHeight()/12,Gdx.graphics.getHeight()/12);
        menuButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                isMenuOpen = !isMenuOpen;
            }
        });


        addActor(runButton);
        addActor(pauseButton);

        healthBar = new HealthBar(player);
        healthBar.setHeight(h/10);
        healthBar.setWidth(w/4);
        healthBar.setX(w/36);
        healthBar.setY(7*h/8);
        healthBar.setScale(1);
        addActor(healthBar);

        menu = new Group();
        Text text =new Text("Задания", font);
        text.setX(w/4);
        text.setY(h/1.5f);
        menu.addActor(text);

        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB888);
        bgPixmap.setColor(Color.rgb888(26/255,27/255,29/255));
        bgPixmap.fill();
        Image image = new Image(new Texture(bgPixmap));
        image.setSize(w/4, h);
        menu.addActor(image);

        Table menuTable = new Table();

        for (final Point point : ((Map)gsm.get("map")).getPoints()) {
            if (point.type < 2 || point.type > 3)
                if (gsm.getMember()!=null && Checker.check(point.getCondition(), gsm.getMember())){
                    menuTable.row();
                    if (point.getData().containsKey("chapter") && gsm.getMember()!=null) {
                        int storyId = Integer.parseInt(gsm.getMember().getData().getTemp().get("currentStory"));
                        for (Story story : gsm.getMember().getData().getStories()) {
                            if (story.getStoryId() == storyId
                                    && (Integer.parseInt(point.getData().get("chapter")) == story.getLastChapter()
                                    || Integer.parseInt(point.getData().get("chapter")) == 0)) {
                                Text label = new Text(point.getTitle(), font);
                                label.addListener(new ClickListener() {
                                    @Override
                                    public void clicked(InputEvent event, float x, float y) {
                                        super.clicked(event, x, y);
                                        player.setDirection(point.getPosition());
                                    }
                                });
                                menuTable.add(label);
                            }
                        }
                    } else {
                        Text label = new Text(point.getTitle(), font);
                        label.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                player.setDirection(point.getPosition());
                            }
                        });
                        menuTable.add(label);
                    }
                }
        }
        menuTable.setSize(w/4, h);
        menu.addActor(menuTable);

        this.menu.setX(w);
        addActor(menu);
        addActor(menuButton);

        logger = new Logger(player, 3, (int) (6*h/8));
    }

    public boolean contains(String name){
        for (Actor actor : getChildren()) {
            if (actor.getName()!=null && actor.getName().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        logger.update();
        if (isMenuOpen && menu.getX()>w*0.75)
            menu.setX(menu.getX()-15);
        else if (!isMenuOpen && menu.getX()<=w+100) menu.setX(menu.getX()+15);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        logger.render();
    }

    @Override
    public void dispose() {
        healthBar.dispose();
        logger.dispose();
    }

}
