package net.artux.pda.map.ecs.interactive;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.input.GestureDetector;

public class ClickComponent implements Component {

    public ClickListener clickListener;
    public final int clickRadius;

    public ClickComponent(int clickRadius, ClickListener clickListener) {
        this.clickRadius = clickRadius;
        this.clickListener = clickListener;
    }

    public interface ClickListener {

        void clicked();

    }

}
