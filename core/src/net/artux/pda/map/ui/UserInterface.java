package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.core.PerGameMap;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.ui.bars.Utils;
import net.artux.pda.map.ui.blocks.AssistantBlock;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.CheckpointModel;
import net.artux.pda.model.quest.MissionModel;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.ParameterModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

@PerGameMap
public class UserInterface extends Group implements Disposable {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final Color primaryColor = Color.valueOf("#3c3d48");
    private final Color backgroundColor = Color.valueOf("#161719");
    private final Label.LabelStyle labelStyle;

    public float w = Gdx.graphics.getWidth();
    public float h = Gdx.graphics.getHeight();
    private Group menu;
    private boolean isMenuOpen = false;

    private BitmapFont font;
    private float leftMargin = 5;

    private final Group gameZone;
    private UIFrame uiFrame;
    private Label timeLabel;
    private BackpackMenu backpackMenu;
    private AssistantBlock assistantBlock;
    private final Skin skin;

    private AssetManager assetManager;
    private DataRepository dataRepository;
    private Timer timer = new Timer();
    private PlatformInterface platformInterface;

    @Inject
    public UserInterface(DataRepository dataRepository, AssetsFinder assetsFinder, Camera camera) {
        super();
        this.platformInterface = dataRepository.getPlatformInterface();
        this.dataRepository = dataRepository;
        this.assetManager = assetsFinder.getManager();

        skin = new Skin(Gdx.files.internal("data/skin/uiskin.json"));
        long loadTime = TimeUtils.millis();

        GameMap map = dataRepository.getGameMap();

        font = assetsFinder.getFontManager().getFont(24);
        initMenu(font);
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        uiFrame = new UIFrame(camera, font, primaryColor, backgroundColor);
        addActor(uiFrame);

        gameZone = new Group();
        gameZone.setX(uiFrame.getHeaderLeftX());
        gameZone.setY(uiFrame.standartFrameSize);
        float gameZoneWidth = w - uiFrame.standartFrameSize - uiFrame.headerLeftX;
        float gameZoneHeight = h - uiFrame.standartFrameSize - uiFrame.topFrameHeight;
        gameZone.setSize(gameZoneWidth, gameZoneHeight);
        addActor(gameZone);

        timeLabel = new Label("00:00", getLabelStyle());
        uiFrame.getLeftHeaderTable().add(timeLabel);
        uiFrame.getLeftHeaderTable().add(new Label(map.getTitle(), getLabelStyle()));

        assistantBlock = new AssistantBlock();
        assistantBlock.setPosition(w - w / 3 - h / 28f, h - h / 2 - h / 14f);
        addActor(assistantBlock);

        /*messagesBlock = new MessagesBlock(assetsFinder);
        messagesBlock.setPosition(leftMargin * getDensity(), h / 10 + h / 2.5f - uiFrame.standartFrameSize);
        addActor(messagesBlock);*/

        backpackMenu = new BackpackMenu(this, assetManager.get("skins/cloud/cloud-form-ui.json"));
        backpackMenu.setPosition(w / 4, h / 3);
        backpackMenu.setSize(w / 2, h / 2);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeLabel.setText(simpleDateFormat.format(new Date())); // TODO eat memory every time
            }
        }, 0, 2000);
        Gdx.app.log("UI", "Loading took " + (TimeUtils.millis() - loadTime) + " ms.");
    }

    public Label.LabelStyle getLabelStyle() {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    public void switchBackpack() {
        if (getChildren().indexOf(backpackMenu, false) == -1)
            addActor(backpackMenu);
        else
            removeActor(backpackMenu);
    }


    public AssistantBlock getAssistantBlock() {
        return assistantBlock;
    }

    void initMenu(BitmapFont font) {
        menu = new Group();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Image image = new Image(Utils.getColoredDrawable(1, 1, backgroundColor));
        image.setSize(w / 4 + 20, h);
        menu.addActor(image);
        Label text = new Label("Задания", new Label.LabelStyle(font, Color.WHITE));
        text.setX(w / 8 - text.getWidth() / 2);
        text.setY(h - 100);
        menu.addActor(text);

        VerticalGroup menuTable = new VerticalGroup();
        menuTable.align(Align.top | Align.left);
        menuTable.columnAlign(Align.left);

        GameMap map = dataRepository.getGameMap();
        StoryDataModel dataModel = dataRepository.getStoryDataModel();
        StoryStateModel storyStateModel = dataModel.getCurrentState();

        StoryModel storyModel = dataRepository.getStoryModel();
        List<MissionModel> missionModels = storyModel.getMissions();

      /*  for (final Point point : map.getPoints()) {
            if (point.getType() < 2 || point.getType() > 3)
                if (storyStateModel != null && QuestUtil.check(point.getCondition(), dataModel)) {
                    if (point.getData().containsKey("chapter")) {
                        int chapterId = storyStateModel.getChapterId();
                        if ((Integer.parseInt(point.getData().get("chapter")) == chapterId
                                || Integer.parseInt(point.getData().get("chapter")) == 0)) {
                            menuTable.addActor(getLabel(point.getName(), labelStyle));
                        }
                    } else {
                        menuTable.addActor(getLabel(point.getName(), labelStyle));
                    }
                }*/

        String[] params = dataModel.getParameters().stream()
                .map(ParameterModel::getKey).toArray(String[]::new);

        if (map != null)
            for (final MissionModel point : dataRepository.getStoryModel().getCurrentMissions(params)) {
                menuTable.addActor(getLabel("---------------------", labelStyle));
                menuTable.addActor(getLabel("Задание: " + point.getTitle(), labelStyle));
                boolean actualGone = false;
                for (int i = 0; i < point.getCheckpoints().size(); i++) {
                    CheckpointModel check = point.getCheckpoints().get(i);
                    if (check.isActual(params))
                        actualGone = true;
                    if (actualGone)
                        menuTable.addActor(getLabel("-> " + check.getTitle(), labelStyle));
                    else
                        menuTable.addActor(getLabel("X " + check.getTitle(), labelStyle));
                }
                menuTable.addActor(getLabel("---------------------", labelStyle));
            }


        ScrollPane scrollPane = new ScrollPane(menuTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setSize(w / 4, h - 100);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setScrollbarsVisible(true);
        menu.addActor(scrollPane);

        this.menu.setX(0);
        addActor(menu);
    }

    private Label getLabel(String title, Label.LabelStyle labelStyle) {
        Label label = new Label(title, labelStyle);
        label.setAlignment(Align.left);
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
        if (menu != null) {
            if (isMenuOpen && menu.getX() < 0)
                menu.setX(menu.getX() + 35);
            else if (!isMenuOpen && menu.getX() > -w * 0.25) menu.setX(menu.getX() - 35);
        }
    }

    @Override
    public void dispose() {
        skin.dispose();
        uiFrame.dispose();
        timer.purge();
        timer.cancel();
    }

    public void toast(String s) {
        platformInterface.toast(s);
    }

    public Group getGameZone() {
        return gameZone;
    }

    public UIFrame getUIFrame() {
        return uiFrame;
    }
}
