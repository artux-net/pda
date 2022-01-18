package net.artux.pda.map.model.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Camera;

public class InteractiveComponent implements Component {

    public String title;
    public int type;
    public InteractListener listener;

    public InteractiveComponent(String title, int type, InteractListener listener) {
        this.title = title;
        this.type = type;
        this.listener = listener;
    }

    public interface InteractListener{
        void interact();
    }
}
