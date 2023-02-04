package net.artux.pda.map.engine.helpers;

import net.artux.pda.map.di.components.MapComponent;
import net.artux.pda.map.engine.ecs.systems.EntityProcessorSystem;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.quest.story.StoryDataModel;

public class ControlPointsHelper {

    public static void createControlPointsEntities(MapComponent coreComponent) {
        GameMap map = coreComponent.getDataRepository().getGameMap();
        EntityProcessorSystem processor = coreComponent.getEntityProcessor();

        if (map.getSpawns() != null)
            for (final SpawnModel spawnModel : map.getSpawns()) {
                 processor.generateSpawn(spawnModel);
            }
    }

}