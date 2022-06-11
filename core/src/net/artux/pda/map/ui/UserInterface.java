package net.artux.pda.map.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.model.Map;
import net.artux.pda.map.model.Point;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.ui.bars.Utils;
import net.artux.pda.map.ui.blocks.AssistantBlock;
import net.artux.pda.map.ui.blocks.ControlBlock;
import net.artux.pda.map.ui.blocks.MessagesBlock;
import net.artux.pdalib.Checker;
import net.artux.pdalib.Member;
import net.artux.pdalib.profile.Story;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class UserInterface extends Group implements Disposable {

    private GameStateManager gsm;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final Color primaryColor = Color.valueOf("#3c3d48");
    private final Color backgroundColor = Color.valueOf("#161719");

    private float w = Gdx.graphics.getWidth();
    private float h = Gdx.graphics.getHeight();
    private Group menu;
    private boolean isMenuOpen = false;

    public static float joyDeltaX;
    public static float joyDeltaY;

    private BitmapFont font;
    private float leftMargin = 5;
    private Table hudTable;

    private UIFrame uiFrame;
    private Label timeLabel;
    private DebugMenu debugMenu;
    private MessagesBlock messagesBlock;
    private AssistantBlock assistantBlock;
    private ControlBlock controlBlock;
    private Touchpad touchpad;

    private AssetManager assetManager;
    private Timer timer = new Timer();

    public UserInterface(final GameStateManager gsm, AssetsFinder assetsFinder, Camera camera) {
        super();
        this.gsm = gsm;
        this.assetManager = assetsFinder.getManager();
        long loadTime = TimeUtils.millis();

        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        style.knob = new TextureRegionDrawable(assetManager.get("ui/touchpad/knob.png", Texture.class));
        style.knob.setMinHeight(170);
        style.knob.setMinWidth(170);
        style.background = new TextureRegionDrawable(assetManager.get("ui/touchpad/back.png", Texture.class));
        touchpad = new Touchpad(10, style);

        touchpad.setPosition(Gdx.graphics.getWidth() / 12f, Gdx.graphics.getHeight() / 10f);
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

        font = assetsFinder.getFontManager().getFont(24);
        initMenu(font);

        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(assetManager.get("ui/exit.png", Texture.class));
        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle(pauseDrawable, pauseDrawable, pauseDrawable);
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
        occupationsButtonStyle.up = new TextureRegionDrawable(assetManager.get("ui/burger.png", Texture.class));
        Button menuButton = new Button(occupationsButtonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                isMenuOpen = !isMenuOpen;
            }
        });

        uiFrame = new UIFrame(camera, font, primaryColor, backgroundColor);
        addActor(uiFrame);
        timeLabel = new Label("00:00", getLabelStyle());
        uiFrame.getLeftHeaderTable().add(timeLabel);
        uiFrame.getLeftHeaderTable().add(new Label(((Map) gsm.get("map")).getTitle(), getLabelStyle()));

        uiFrame.getRightHeaderTable().add(menuButton);
        uiFrame.getRightHeaderTable().add(pauseButton);

        hudTable = new Table();
        hudTable.setPosition(uiFrame.getHeaderLeftX(), h - h / 14);
        hudTable.align(Align.left | Align.top);
        hudTable.defaults().align(Align.left);
        hudTable.setWidth(w / 3);
        addActor(hudTable);

        assistantBlock = new AssistantBlock();
        assistantBlock.setPosition(w - w / 3 - h / 28f, h - h / 2 - h / 14f);
        addActor(assistantBlock);

        messagesBlock = new MessagesBlock(assetsFinder);
        messagesBlock.setPosition(leftMargin * getDensity(), h / 10 + h / 2.5f - uiFrame.frame);
        addActor(messagesBlock);

        controlBlock = new ControlBlock();
        controlBlock.defaults()
                .pad(10)
                .height(h / 8)
                .width(h / 8)
                .right();
        controlBlock.setPosition(w - w / 3 - h / 28f, h / 28f);
        addActor(controlBlock);
        Color color = getColor();
        color.a = 0.7f;
        controlBlock.setColor(color);
        color.a = 1f;
        setColor(color);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeLabel.setText(simpleDateFormat.format(new Date())); // TODO eat memory every time
            }
        }, 0, 2000);
        Gdx.app.log("UI", "Loading took " + (TimeUtils.millis() - loadTime) + " ms.");
    }

    public MessagesBlock getMessagesBlock() {
        return messagesBlock;
    }

    public void enableDebug(AssetManager assetManager, Engine engine) {
        Button.ButtonStyle bugStyle = new Button.ButtonStyle();
        bugStyle.up = new TextureRegionDrawable(assetManager.get("ui/bug.png", Texture.class));

        final Button debugButton = new Button(bugStyle);
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

        debugMenu = new DebugMenu(this, engine, assetManager.<Skin>get("skins/cloud/cloud-form-ui.json"));
        debugMenu.setPosition(w / 4, h / 3);
        debugMenu.setSize(w / 2, h / 2);
    }

    public Table getHudTable() {
        return hudTable;
    }

    public AssistantBlock getAssistantBlock() {
        return assistantBlock;
    }

    public ControlBlock getControlBlock() {
        return controlBlock;
    }

    public Touchpad getTouchpad() {
        return touchpad;
    }

    public Member getMember() {
        return gsm.getMember();
    }

    public void addInteractButton(String id, String iconPath, final InteractiveComponent.InteractListener listener) {
        addInteractButton(id, iconPath, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.interact(UserInterface.this);
                Cell<Actor> cell = controlBlock.getCell(actor);
                actor.remove();
                controlBlock.getCells().removeValue(cell, true);
                controlBlock.invalidate();
            }
        });
    }

    public void addInteractButton(String id, String iconPath, EventListener listener) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = new TextureRegionDrawable(assetManager.get("ui/buttonBack.png", Texture.class));
        TextureRegion textureRegion = new TextureRegion(assetManager.get(iconPath, Texture.class));
        float pad = textureRegion.getRegionHeight() * 0.15f;
        style.imageUp = new TextureRegionDrawable(textureRegion);

        ImageButton button = new ImageButton(style);
        button.pad(pad);
        button.setName(id);
        button.addListener(listener);
        controlBlock.add(button);
    }

    void initMenu(BitmapFont font) {
        menu = new Group();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Image image = new Image(Utils.getColoredDrawable(1, 1, primaryColor));
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

        /*skin = new Skin(Gdx.files.internal("data/skin/uiskin.json"));

        ScrollPane scrollPane = new ScrollPane(menuTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setSize(w / 4, h - 100);
        scrollPane.setScrollbarsVisible(true);
        menu.addActor(scrollPane);

        this.menu.setX(w);
        addActor(menu);*/
    }

    private Label getLabel(String title, Label.LabelStyle labelStyle) {
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
        return label;
    }


    public float getDensity() {
        return Gdx.graphics.getDensity();
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
        if (messagesBlock != null)
            messagesBlock.dispose();
        debugMenu.dispose();
        uiFrame.dispose();
        timer.purge();
        timer.cancel();
    }

}
