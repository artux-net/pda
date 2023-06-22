package net.artux.pda.map.content;

import com.badlogic.ashley.core.Entity;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.engine.ecs.components.ClickComponent;
import net.artux.pda.map.engine.ecs.components.Group;
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent;
import net.artux.pda.map.engine.ecs.systems.EntityProcessorSystem;
import net.artux.pda.map.utils.di.components.MapComponent;
import net.artux.pda.map.view.FontManager;
import net.artux.pda.map.view.UserInterface;
import net.artux.pda.map.view.dialog.ControlPointDialog;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.SpawnModel;

import java.util.List;

public class ControlPointsHelper {

    public static void createControlPointsEntities(MapComponent mapComponent) {
        GameMap map = mapComponent.getDataRepository().getGameMap();
        EntityProcessorSystem processor = mapComponent.getEntityProcessor();

        //add spawns on map
        List<SpawnModel> spawns = map.getSpawns();
        if (spawns == null)
            return;

        for (int i = 0; i < spawns.size(); i++) {
            SpawnModel spawnModel = spawns.get(i);
            boolean withSprite = !spawnModel.getParams().contains("hide");
            processor.generateSpawn(spawnModel, withSprite);
        }
    }

}