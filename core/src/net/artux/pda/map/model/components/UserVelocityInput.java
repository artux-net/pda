package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.ui.UserInterface;

public class UserVelocityInput implements Component {

    public Vector2 getVelocity(){
        return new Vector2(UserInterface.joyDeltaX, UserInterface.joyDeltaY);
    }

    public boolean isRunning() {
        return UserInterface.running;
    }
}