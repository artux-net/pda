package net.artux.pda.map.ui;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
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
import net.artux.pda.map.engine.components.PointComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.systems.player.CameraSystem;
import net.artux.pda.map.engine.systems.player.MissionsSystem;
import net.artux.pda.map.ui.bars.Utils;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.CheckpointModel;
import net.artux.pda.model.quest.MissionModel;

import javax.inject.Inject;

@PerGameMap
public class MissionMenu extends Table {

    private final DataRepository dataRepository;
    private final GameMap map;
    private final VerticalGroup menuTable;
    private final Label.LabelStyle labelStyle;
    private final CameraSystem cameraSystem;
    private final MissionsSystem missionsSystem;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    @Inject
    public MissionMenu(DataRepository dataRepository, MissionsSystem missionsSystem, CameraSystem cameraSystem, GameMap map, Label.LabelStyle labelStyle, Skin skin) {
        super();
        this.map = map;
        this.labelStyle = labelStyle;
        this.cameraSystem = cameraSystem;
        this.missionsSystem = missionsSystem;
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
        scrollPane.setScrollingDisabled(false, false);
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
        if (type == Type.MISSIONS) {
            for (final MissionModel point : missionsSystem.getMissions()) {
                menuTable.addActor(getLabel("---------------------", labelStyle));
                menuTable.addActor(getLabel("Задание: " + point.getTitle(), labelStyle));
                boolean actualGone = false;
                for (int i = 0; i < point.getCheckpoints().size(); i++) {
                    CheckpointModel check = point.getCheckpoints().get(i);
                    if (check.isActual(missionsSystem.getParams()))
                        actualGone = true;
                    if (actualGone)
                        menuTable.addActor(getLabel("-> " + check.getTitle(), labelStyle));
                    else
                        menuTable.addActor(getLabel("X " + check.getTitle(), labelStyle));
                }
                menuTable.addActor(getLabel("---------------------", labelStyle));
            }
        } else {
            for (final Entity pointEntity : missionsSystem.getEntities()) {
                PointComponent point = pm.
                Label label = getLabel(point.getTitle(), labelStyle);
                label.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        Vector2 position = point.getPosition();
                        cameraSystem.setDetached(true);
                        cameraSystem.getCamera().position.x = position.x;
                        cameraSystem.getCamera().position.y = position.y;
                        missionsSystem.setTargetEntity();
                    }
                });

                menuTable.addActor(label);
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
