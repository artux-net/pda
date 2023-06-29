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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Scaling;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.QuestPointsHelper;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.interactive.PassivityComponent;
import net.artux.pda.map.ecs.interactive.map.ConditionComponent;
import net.artux.pda.map.ecs.interactive.map.PointComponent;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.player.MissionsSystem;
import net.artux.pda.map.ecs.player.PlayerSystem;
import net.artux.pda.map.view.blocks.ImageTextButton;
import net.artux.pda.map.view.template.SideMenu;

import javax.inject.Inject;
import javax.inject.Named;

@PerGameMap
public class NavigationMenu extends SideMenu {

    private final MissionsSystem missionsSystem;
    private final PlayerSystem playerSystem;
    private final AssetsFinder assetsFinder;
    private final AssetManager assetManager;
    private final boolean testMode;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<PointComponent> pcm = ComponentMapper.getFor(PointComponent.class);

    @Inject
    public NavigationMenu(@Named("testerMode") boolean testMode,
                          PlayerSystem playerSystem, MissionsSystem missionsSystem,
                          LocaleBundle localeBundle,
                          AssetsFinder assetsFinder, Skin skin) {
        super(localeBundle.get("sideMenu.navigation"), skin);
        this.testMode = testMode;
        this.playerSystem = playerSystem;
        this.assetsFinder = assetsFinder;
        this.missionsSystem = missionsSystem;
        this.assetManager = assetsFinder.getManager();

        getContent().space(10f);
        update();
    }

    public void update() {
        VerticalGroup content = getContent();
        content.clear();

        Label.LabelStyle labelStyle = assetsFinder.getFontManager().getLabelStyle(32, Color.WHITE);
        for (final Entity pointEntity : missionsSystem.getEngine()
                .getEntitiesFor(Family.all(PointComponent.class, PointComponent.class, ConditionComponent.class).exclude(PassivityComponent.class).get())) {
            Vector2 position = pm.get(pointEntity).getPosition();
            PointComponent point = pcm.get(pointEntity);

            Texture texture = QuestPointsHelper.getPointTexture(assetManager, point.getType());
            if (texture != null) {
                Image pointIcon = new Image(texture);
                pointIcon.setScaling(Scaling.fit);

                ImageTextButton textButton = new ImageTextButton(pointIcon, point.getTitle(), labelStyle);
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
                content.addActor(textButton);
            }
        }

    }

}
