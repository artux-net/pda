package net.artux.pda.map.engine.ecs.components.effects;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Timer;

import java.util.EnumSet;

public class Effects implements Component {

    private final EnumSet<Effect> effects;

    public Effects() {
        effects = EnumSet.noneOf(Effect.class);
    }

    public void add(Effect effect) {
        effects.add(effect);
    }

    public void remove(Effect effect) {
        effects.remove(effect);
    }

    public void clear() {
        effects.clear();
    }

    public EnumSet<Effect> getEffects() {
        return effects;
    }
}
