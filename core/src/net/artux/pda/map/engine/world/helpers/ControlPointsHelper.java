package net.artux.pda.map.engine.world.helpers;

import net.artux.pda.map.di.core.MapComponent;
import net.artux.pda.map.engine.entities.EntityProcessorSystem;
import net.artux.pda.model.QuestUtil;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.quest.story.StoryDataModel;

public class ControlPointsHelper {

    public static void createControlPointsEntities(MapComponent coreComponent) {
        GameMap map = coreComponent.getDataRepository().getGameMap();
        StoryDataModel storyDataModel = coreComponent.getDataRepository().getCurrentStoryDataModel();
        EntityProcessorSystem processor = coreComponent.getEntityProcessor();

        if (map.getSpawns() != null)
            for (final SpawnModel spawnModel : map.getSpawns()) {

                if (QuestUtil.check(spawnModel.getCondition(), storyDataModel)) {
                    processor.generateSpawn(spawnModel);

                    //todo add all but not active
                }
            }
    }

}