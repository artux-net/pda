package net.artux.pda.map.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.player.MissionsSystem;
import net.artux.pda.map.view.blocks.MissionBlock;
import net.artux.pda.map.view.template.SideMenu;
import net.artux.pda.model.quest.mission.MissionModel;

import javax.inject.Inject;

@PerGameMap
public class MissionMenu extends SideMenu {

    private final MissionsSystem missionsSystem;
    private final AssetsFinder assetsFinder;

    @Inject
    public MissionMenu(MissionsSystem missionsSystem,
                       LocaleBundle localeBundle,
                       AssetsFinder assetsFinder, Skin skin) {
        super(localeBundle.get("sideMenu.missions"), skin);
        this.assetsFinder = assetsFinder;
        this.missionsSystem = missionsSystem;

        update();
    }

    public void update() {
        VerticalGroup content = getContent();
        content.clear();
        String[] params = missionsSystem.getCurrentParams();
        for (final MissionModel missionModel : missionsSystem.getCurrentMissions()) {
            MissionBlock missionBlock = new MissionBlock(getSkin(), missionModel, assetsFinder, params);
            content.addActor(missionBlock);
            missionBlock.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    missionsSystem.setActiveMissionByName(missionModel.getName());
                }
            });
        }
    }

}
