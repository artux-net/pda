package net.artux.pda.map.engine.helpers;

import net.artux.pda.map.di.components.MapComponent;
import net.artux.pda.map.engine.ecs.systems.EntityProcessorSystem;
import net.artux.pda.model.map.GameMap;

public class ControlPointsHelper {

    public static void createControlPointsEntities(MapComponent coreComponent) {
        GameMap map = coreComponent.getDataRepository().getGameMap();
        EntityProcessorSystem processor = coreComponent.getEntityProcessor();

        if (map.getSpawns() != null)
            for (int i = 0; i < map.getSpawns().size(); i++) {
                processor.generateSpawn(map.getSpawns().get(i));
            }
    }

}