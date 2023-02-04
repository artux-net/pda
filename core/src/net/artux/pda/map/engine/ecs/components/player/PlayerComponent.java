package net.artux.pda.map.engine.ecs.components.player;

import com.badlogic.ashley.core.Component;

import net.artux.pda.model.quest.story.StoryDataModel;

public class PlayerComponent implements Component {

    public StoryDataModel gdxData;

    public PlayerComponent(StoryDataModel gdxData) {
        this.gdxData = gdxData;
    }
}