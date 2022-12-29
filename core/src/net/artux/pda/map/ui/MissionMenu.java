package net.artux.pda.map.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.systems.CameraSystem;
import net.artux.pda.map.ui.bars.Utils;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.QuestUtil;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.Point;
import net.artux.pda.model.quest.CheckpointModel;
import net.artux.pda.model.quest.MissionModel;
import net.artux.pda.model.quest.story.ParameterModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.quest.story.StoryStateModel;

import javax.inject.Inject;

@PerGameMap
public class MissionMenu extends Table {

    private final DataRepository dataRepository;
    private final GameMap map;
    private final VerticalGroup menuTable;
    private final Label.LabelStyle labelStyle;
    private final CameraSystem cameraSystem;

    @Inject
    public MissionMenu(DataRepository dataRepository, CameraSystem cameraSystem, GameMap map, Label.LabelStyle labelStyle, Skin skin) {
        super();
        this.map = map;
        this.labelStyle = labelStyle;
        this.cameraSystem = cameraSystem;
        this.dataRepository = dataRepository;

        setFillParent(true);
        top();

        Label text = new Label("Задания", labelStyle);
        add(text).uniform();
        text.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update(Type.MISSIONS);
            }
        });
        text = new Label("Метки", labelStyle);
        add(text).uniform();
        text.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update(Type.POINTS);
            }
        });
        row();

        menuTable = new VerticalGroup();
        menuTable.expand().fill();
        row();
        ScrollPane scrollPane = new ScrollPane(menuTable, skin);
        scrollPane.setClamp(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setScrollbarsVisible(true);
        add(scrollPane)
                .top()
                .fill()
                .expand()
                .colspan(2);

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
        update(Type.MISSIONS);
    }

    void update(Type type) {
        menuTable.clear();
        StoryDataModel storyDataModel = dataRepository.getStoryDataModel();
        if (type == Type.MISSIONS) {
            String[] params = storyDataModel.getParameters().stream()
                    .map(ParameterModel::getKey).toArray(String[]::new);
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
        } else {
            StoryStateModel storyStateModel = storyDataModel.getCurrentState();
            for (final Point point : map.getPoints()) {
                if (point.getType() < 2 || point.getType() > 3)
                    if (QuestUtil.check(point.getCondition(), storyDataModel)) {
                        Label label = null;
                        if (point.getData().containsKey("chapter")) {
                            int chapterId = storyStateModel.getChapterId();
                            if ((Integer.parseInt(point.getData().get("chapter")) == chapterId
                                    || Integer.parseInt(point.getData().get("chapter")) == 0)) {
                                label = getLabel(point.getName(), labelStyle);
                                label.addListener(new ClickListener() {
                                    @Override
                                    public void clicked(InputEvent event, float x, float y) {
                                        super.clicked(event, x, y);
                                        Vector2 position = Mappers.vector2(point.getPos());
                                        cameraSystem.setDetached(true);
                                        cameraSystem.getCamera().position.x = position.x;
                                        cameraSystem.getCamera().position.y = position.y;
                                    }
                                });
                                menuTable.addActor(label);
                            }
                        } else {
                            label = getLabel(point.getName(), labelStyle);
                            label.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);
                                    Vector2 position = Mappers.vector2(point.getPos());
                                    cameraSystem.setDetached(true);
                                    cameraSystem.getCamera().position.x = position.x;
                                    cameraSystem.getCamera().position.y = position.y;
                                }
                            });
                            menuTable.addActor(label);
                        }
                    }
            }
        }
    }

    private Label getLabel(String title, Label.LabelStyle labelStyle) {
        Label label = new Label(title, labelStyle);
        label.setAlignment(Align.left);
        label.setWrap(true);
        return label;
    }

    enum Type {
        MISSIONS,
        POINTS
    }
}
