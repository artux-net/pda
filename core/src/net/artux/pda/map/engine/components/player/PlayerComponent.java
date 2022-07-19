package net.artux.pda.map.engine.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Camera;

import net.artux.pda.map.models.UserGdx;
import net.artux.pda.map.models.user.GdxData;

public class PlayerComponent implements Component {

    public Camera camera;
    public UserGdx userModel;
    public GdxData gdxData;

    public PlayerComponent(Camera camera, UserGdx userModel, GdxData gdxData) {
        this.camera = camera;
        this.userModel = userModel;
        this.gdxData = gdxData;
    }
}