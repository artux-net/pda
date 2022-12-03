package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;

public class StatusComponent implements Component {

    private final boolean active;

    public StatusComponent() {
        active = true;
    }

    public StatusComponent(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}