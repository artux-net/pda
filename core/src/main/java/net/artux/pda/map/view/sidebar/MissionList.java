package net.artux.pda.map.view.sidebar;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.player.MissionsSystem;
import net.artux.pda.map.view.collection.list.item.MissionItem;
import net.artux.pda.model.quest.mission.MissionModel;

import javax.inject.Inject;

@PerGameMap
public class MissionList extends Sidebar {

    private final MissionsSystem missionsSystem;
    private final AssetsFinder assetsFinder;

    @Inject
    public MissionList(MissionsSystem missionsSystem,
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
            MissionItem missionItem = new MissionItem(getSkin(), missionModel, assetsFinder, params);
            content.addActor(missionItem);
            missionItem.addListener(new ActorGestureListener() {
                @Override
                public void tap(InputEvent event, float x, float y, int count, int button) {
                    super.tap(event, x, y, count, button);
                    missionsSystem.setActiveMissionByName(missionModel.getName());
                }
            });
        }
    }

}
