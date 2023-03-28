package net.artux.pda.map.view;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.content.QuestPointsHelper;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.map.ConditionComponent;
import net.artux.pda.map.engine.ecs.components.map.PointComponent;
import net.artux.pda.map.engine.ecs.systems.player.MissionsSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.map.view.blocks.ImageTextButton;
import net.artux.pda.map.view.blocks.MissionBlock;
import net.artux.pda.map.view.blocks.SlotTextButton;
import net.artux.pda.map.view.view.bars.Utils;
import net.artux.pda.model.quest.MissionModel;

import javax.inject.Inject;
import javax.inject.Named;

@PerGameMap
public class MissionMenu extends Table {

    private final VerticalGroup menuTable;
    private final MissionsSystem missionsSystem;
    private final PlayerSystem playerSystem;
    private final AssetsFinder assetsFinder;
    private final AssetManager assetManager;
    private final boolean testMode;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<PointComponent> pcm = ComponentMapper.getFor(PointComponent.class);
    private final ComponentMapper<ConditionComponent> ccm = ComponentMapper.getFor(ConditionComponent.class);

    @Inject
    public MissionMenu(@Named("testerMode") boolean testMode, PlayerSystem playerSystem, MissionsSystem missionsSystem, TextButton.TextButtonStyle textButtonStyle,
                       AssetsFinder assetsFinder, Skin skin) {
        super(skin);
        this.testMode = testMode;
        this.playerSystem = playerSystem;
        this.assetsFinder = assetsFinder;
        this.missionsSystem = missionsSystem;
        this.assetManager = assetsFinder.getManager();

        setFillParent(true);
        top();

        SlotTextButton text = new SlotTextButton("Задания", textButtonStyle);
        add(text)
                .uniform();
        text.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update(Type.MISSIONS);
            }
        });

        text = new SlotTextButton("Метки", textButtonStyle);
        add(text)
                .uniform();
        text.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                update(Type.POINTS);
            }
        });
        row();

        menuTable = new VerticalGroup();
        menuTable
                .expand()
                .fill();
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

    public void update(Type type) {
        menuTable.clear();
        if (type == Type.MISSIONS) {
            String[] params = missionsSystem.getParams();
            for (final MissionModel missionModel : missionsSystem.getMissions()) {
                MissionBlock missionBlock = new MissionBlock(getSkin(), missionModel, assetsFinder, params);
                menuTable.addActor(missionBlock);
                missionBlock.addListener(new ActorGestureListener() {

                    @Override
                    public void tap(InputEvent event, float x, float y, int count, int button) {
                        super.tap(event, x, y, count, button);
                        missionsSystem.setActiveMissionByName(missionModel.getName());
                    }
                });
            }
        } else {
            Label.LabelStyle labelStyle = assetsFinder.getFontManager().getLabelStyle(32, Color.WHITE);
            for (final Entity pointEntity : missionsSystem.getEngine()
                    .getEntitiesFor(Family.all(PointComponent.class, PointComponent.class, ConditionComponent.class).exclude(PassivityComponent.class).get())) {
                Vector2 position = pm.get(pointEntity).getPosition();
                PointComponent point = pcm.get(pointEntity);
                ConditionComponent condition = ccm.get(pointEntity);

                Texture texture = QuestPointsHelper.getPointTexture(assetManager, point.getType());
                if (texture != null) {
                    Image pointIcon = new Image(texture);
                    pointIcon.setScaling(Scaling.fit);

                    ImageTextButton textButton = new ImageTextButton(pointIcon, point.getTitle() + condition.toString(), labelStyle);
                    textButton.addListener(new ActorGestureListener() {
                        @Override
                        public void tap(InputEvent event, float x, float y, int count, int button) {
                            super.tap(event, x, y, count, button);
                            missionsSystem.setTargetPosition(position);
                        }

                        @Override
                        public boolean longPress(Actor actor, float x, float y) {
                            if (testMode)
                                playerSystem.getPlayer().getComponent(BodyComponent.class)
                                        .body.setTransform(position.x, position.y, 0);
                            return super.longPress(actor, x, y);
                        }
                    });
                    menuTable.addActor(textButton);
                }
            }
        }
    }

    public enum Type {
        MISSIONS,
        POINTS
    }
}
