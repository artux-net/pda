package net.artux.pda.map.engine.world.helpers;

import com.badlogic.ashley.core.Engine;

import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.systems.DataSystem;
import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.model.QuestUtil;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.quest.story.StoryDataModel;

public class ControlPointsHelper {

    public static void createControlPointsEntities(final Engine engine, EntityBuilder entityBuilder) {
        GameMap map = engine.getSystem(DataSystem.class).getMap();
        StoryDataModel storyDataModel = engine.getSystem(PlayerSystem.class).getPlayerComponent().gdxData;

        for (final SpawnModel spawnModel : map.getSpawns()) {
            if (QuestUtil.check(spawnModel.getCondition(), storyDataModel)) {
                entityBuilder.generateSpawn(spawnModel);
            }
        }
    }

}