package net.artux.pda.map.engine.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Camera;

import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

public class PlayerComponent implements Component {

    public Camera camera;
    public UserModel userModel;
    public StoryDataModel gdxData;

    public PlayerComponent(Camera camera, UserModel userModel, StoryDataModel gdxData) {
        this.camera = camera;
        this.userModel = userModel;
        this.gdxData = gdxData;
    }
}