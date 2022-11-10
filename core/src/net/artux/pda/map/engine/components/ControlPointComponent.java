package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

public class ControlPointComponent implements Component {

    public String title;

    public ControlPointComponent(String title) {
        this.title = title;
    }

    public String desc() {
        return "Объект: " +
                title;
    }
}