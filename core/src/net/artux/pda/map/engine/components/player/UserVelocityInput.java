package net.artux.pda.map.engine.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.ui.UserInterface;

public class UserVelocityInput implements Component {

    public UserVelocityInput(){
        UserInterface.joyDeltaX = 0;
        UserInterface.joyDeltaY = 0;
    }

    public Vector2 getVelocity(){
        return new Vector2(UserInterface.joyDeltaX, UserInterface.joyDeltaY); //todo bad
    }

}