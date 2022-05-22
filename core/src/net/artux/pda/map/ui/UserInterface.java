package net.artux.pda.map.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.ui.bars.HealthBar;
import net.artux.pdalib.Checker;
import net.artux.pdalib.Member;
import net.artux.pdalib.profile.Story;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class UserInterface extends Group implements Disposable {

    private final Button.ButtonStyle textButtonStyle;
    private GameStateManager gsm;

    private final Color primaryColor = Color.valueOf("#3c3d48");
    private final Color backgroundColor = Color.valueOf("#161719");

    private HealthBar healthBar;
    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();
    private Group menu;
    private Texture texture;
    private Pixmap bgPixmap;
    private boolean isMenuOpen = false;

    public static float joyDeltaX;
    public static float joyDeltaY;
    public static boolean running;

    private BitmapFont font;
    private float leftMargin = 5;
    private Table hudTable;

    private UIFrame uiFrame;
    private Logger logger;
    private DebugMenu debugMenu;

    public UserInterface(final GameStateManager gsm, AssetManager assetManager, Camera camera) {
        super();
        this.gsm = gsm;


        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        style.knob = new TextureRegionDrawable(assetManager.get("touchpad/knob.png", Texture.class));
        style.knob.setMinHeight(170);
        style.knob.setMinWidth(170);
        style.background = new TextureRegionDrawable(assetManager.get("touchpad/back.png", Texture.class));
        final Touchpad touchpad = new Touchpad(10, style);

        touchpad.setPosition(Gdx.graphics.getWidth() / 12, Gdx.graphics.getHeight() / 10);
        touchpad.setBounds(50, 50, Gdx.graphics.getHeight() / 2.5f, Gdx.graphics.getHeight() / 2.5f);
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Color color = touchpad.getColor();
                joyDeltaX = ((Touchpad) actor).getKnobPercentX();
                joyDeltaY = ((Touchpad) actor).getKnobPercentY();
                if (joyDeltaY == 0 && joyDeltaX == 0)
                    touchpad.setColor(color.r, color.g, color.b, 0.2f);
                else
                    touchpad.setColor(color.r, color.g, color.b, 0.9f);
            }
        });
        addActor(touchpad);

        Button.ButtonStyle runButtonStyle = new Button.ButtonStyle();
        runButtonStyle.up = new TextureRegionDrawable(assetManager.get("beg2.png", Texture.class));
        runButtonStyle.down = new TextureRegionDrawable(assetManager.get("beg1.png", Texture.class));
        final Button runButton = new Button(runButtonStyle);
        runButton.setPosition(11 * Gdx.graphics.getWidth() / 12, Gdx.graphics.getHeight() / 12);
        runButton.setSize(Gdx.graphics.getHeight() / 10, Gdx.graphics.getHeight() / 10);
        runButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                running = true;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                running = false;
                super.touchUp(event, x, y, pointer, button);
            }
        });
        font = Fonts.generateFont(Fonts.Language.RUSSIAN, 24);

        initMenu(font);

        uiFrame = new UIFrame(camera, font, primaryColor, backgroundColor);
        addActor(uiFrame);

        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle();
        pauseButtonStyle.up = new TextureRegionDrawable(assetManager.get("pause.png", Texture.class));
        pauseButtonStyle.down = new TextureRegionDrawable(assetManager.get("pause.png", Texture.class));
        pauseButtonStyle.over = new TextureRegionDrawable(assetManager.get("pause.png", Texture.class));
        Button pauseButton = new Button(pauseButtonStyle);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.clicked(event, x, y);
                Gdx.app.debug("UserInterface", "touched pause - user interface");
                HashMap<String, String> data = new HashMap<>();
                data.put("openPda", "");
                gsm.getPlatformInterface().send(data);
            }
        });

        Button.ButtonStyle occupationsButtonStyle = new Button.ButtonStyle();
        occupationsButtonStyle.up = new TextureRegionDrawable(assetManager.get("occupations.png", Texture.class));
        Button menuButton = new Button(occupationsButtonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                isMenuOpen = !isMenuOpen;
            }
        });

        textButtonStyle = new Button.ButtonStyle();
        textButtonStyle.up = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));
        textButtonStyle.down = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));
        textButtonStyle.checked = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));
        textButtonStyle.over = new TextureRegionDrawable(assetManager.get("dialog.png", Texture.class));

        addActor(runButton);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

        uiFrame.getLeftHeaderTable().add(new Label(simpleDateFormat.format(new Date()), getLabelStyle()));
        uiFrame.getLeftHeaderTable().add(new Label(((Map)gsm.get("map")).getTitle(), getLabelStyle()));


        uiFrame.getRightHeaderTable().add(menuButton);
        uiFrame.getRightHeaderTable().add(pauseButton);

        hudTable = new Table();
        hudTable.setPosition(leftMargin * getDensity(), h - uiFrame.topFrameHeight);
        hudTable.align(Align.left | Align.top);
        addActor(hudTable);

        /*healthBar = new HealthBar(this, assetManager);
        WeaponBar weaponBar = new WeaponBar(this);

        hudTable.pad(leftMargin * getDensity())
                .row()
                .height(120);
        hudTable.add().setActor(healthBar);

        hudTable.row()
                .padTop(20)
                .height(70);
        hudTable.add().setActor(weaponBar);*/
    }

    public void enableDebug(AssetManager assetManager, Engine engine){
        Button.ButtonStyle occupationsButtonStyle = new Button.ButtonStyle();
        occupationsButtonStyle.up = new TextureRegionDrawable(assetManager.get("occupations.png", Texture.class));

        final Button debugButton = new Button(occupationsButtonStyle);
        debugButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (getChildren().indexOf(debugMenu, false) == -1)
                    addActor(debugMenu);
                else
                    removeActor(debugMenu);
            }
        });
        uiFrame.getLeftHeaderTable().add(debugButton);

        debugMenu = new DebugMenu(this, engine);
        debugMenu.setPosition(w/4, h/3);
        debugMenu.setSize(w/2, h/2);
        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        bgPixmap.setColor(backgroundColor);
        bgPixmap.fill();
        debugMenu.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap))));
    }

    public Table getHudTable() {
        return hudTable;
    }

    public Member getMember() {
        return gsm.getMember();
    }

    public void addInteractButton(String id, final InteractiveComponent.InteractListener listener) {
        Button button = new Button(textButtonStyle);
        button.setPosition(w - w / 12, 2.5f * h / 12);
        button.setSize(h / 10, h / 10);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.interact();
            }
        });

        button.setName(id);
        this.addActor(button);
    }

    void initMenu(BitmapFont font) {
        menu = new Group();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        bgPixmap.setColor(Color.rgb888(26 / 255, 27 / 255, 29 / 255));
        bgPixmap.fill();
        texture = new Texture(bgPixmap);
        Image image = new Image(texture);
        image.setSize(w / 4 + 20, h);
        menu.addActor(image);
        Label text = new Label("Метки", new Label.LabelStyle(font, Color.WHITE));
        text.setX(w / 8 - text.getWidth() / 2);
        text.setY(h - 100);
        menu.addActor(text);

        VerticalGroup menuTable = new VerticalGroup();
        menuTable.setFillParent(true);
        menuTable.align(Align.top);
        if (gsm.get("map") != null)
                for (final Point point : ((Map) gsm.get("map")).getPoints()) {
                    if (point.type < 2 || point.type > 3)
                        if (gsm.getMember() != null && Checker.check(point.getCondition(), gsm.getMember())) {
                            if (point.getData().containsKey("chapter") && gsm.getMember() != null) {
                                int storyId = Integer.parseInt(gsm.getMember().getData().getTemp().get("currentStory"));
                                for (Story story : gsm.getMember().getData().getStories()) {
                                    if (story.getStoryId() == storyId
                                            && (Integer.parseInt(point.getData().get("chapter")) == story.getLastChapter()
                                            || Integer.parseInt(point.getData().get("chapter")) == 0)) {
                                        menuTable.addActor(getLabel(point.getTitle(), labelStyle));
                                    }
                                }
                            } else {
                                menuTable.addActor(getLabel(point.getTitle(), labelStyle));
                            }
                        }
                }

        Skin skin = new Skin(Gdx.files.internal("data/skin/uiskin.json"));

        ScrollPane scrollPane = new ScrollPane(menuTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setSize(w / 4, h - 100);
        scrollPane.setScrollbarsVisible(true);
        menu.addActor(scrollPane);

        this.menu.setX(w);
        addActor(menu);
    }

    Label getLabel(String title, Label.LabelStyle labelStyle) {
        Label label = new Label(title, labelStyle);
        label.setWrap(true);
        label.setAlignment(Align.center);
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                //player.setDirection(point.getPosition());
            }
        });

        Pixmap labelColor = new Pixmap((int) label.getWidth(), (int) label.getHeight(), Pixmap.Format.RGB888);
        labelColor.setColor(Color.DARK_GRAY);
        labelColor.fill();
        label.getStyle().background = new Image(new Texture(labelColor)).getDrawable();
        return label;
    }

    public float getDensity() {
        return Gdx.graphics.getDensity();
    }

    public boolean contains(String name) {
        for (Actor actor : getChildren()) {
            if (actor.getName() != null && actor.getName().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (menu != null)
            if (isMenuOpen && menu.getX() > w * 0.75)
                menu.setX(menu.getX() - 15);
            else if (!isMenuOpen && menu.getX() <= w + 100) menu.setX(menu.getX() + 15);
    }

    public Label.LabelStyle getLabelStyle() {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    @Override
    public void dispose() {
        if (healthBar != null)
            healthBar.dispose();
        if (texture != null)
            texture.dispose();
        if (bgPixmap != null)
            bgPixmap.dispose();
        if (font != null)
            font.dispose();
        uiFrame.dispose();
        if (logger != null)
            logger.dispose();
    }

}
