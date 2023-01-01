package net.artux.pda.map.ui;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.components.PointComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.systems.player.MissionsSystem;
import net.artux.pda.map.engine.world.helpers.QuestPointsHelper;
import net.artux.pda.map.ui.bars.Utils;
import net.artux.pda.map.ui.blocks.ImageTextButton;
import net.artux.pda.map.ui.blocks.MissionBlock;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.model.quest.MissionModel;

import javax.inject.Inject;

@PerGameMap
public class MissionMenu extends Table {

    private final VerticalGroup menuTable;
    private final Label.LabelStyle labelStyle;
    private final MissionsSystem missionsSystem;
    private final AssetsFinder assetsFinder;
    private final AssetManager assetManager;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<PointComponent> pcm = ComponentMapper.getFor(PointComponent.class);

    @Inject
    public MissionMenu(MissionsSystem missionsSystem, AssetsFinder assetsFinder,
                       Skin skin) {
        super(skin);
        this.labelStyle = assetsFinder.getFontManager().getLabelStyle(38, Color.WHITE);
        this.missionsSystem = missionsSystem;
        this.assetsFinder = assetsFinder;
        this.assetManager = assetsFinder.getManager();

        setFillParent(true);
        top();
        defaults().pad(10f);

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
            String[] params = missionsSystem.getParams();
            for (final MissionModel missionModel : missionsSystem.getMissions()) {
                MissionBlock missionBlock = new MissionBlock(getSkin(), missionModel, assetsFinder, params);
                menuTable.addActor(missionBlock);
                missionBlock.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        missionsSystem.setActiveMissionByName(missionModel.getName());
                    }
                });
            }
        } else {
            for (final Entity pointEntity : missionsSystem.getEntities()) {
                PositionComponent position = pm.get(pointEntity);
                PointComponent point = pcm.get(pointEntity);

                Texture texture = QuestPointsHelper.getPointTexture(assetManager, point.getType());
                if (texture != null) {
                    Image pointIcon = new Image(texture);
                    pointIcon.setScaling(Scaling.fit);

                    ImageTextButton textButton = new ImageTextButton(pointIcon, point.getTitle(), labelStyle);
                    textButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            missionsSystem.setTargetPosition(position);
                        }
                    });
                    menuTable.addActor(textButton);
                }
            }
        }
    }

    enum Type {
        MISSIONS,
        POINTS
    }
}
