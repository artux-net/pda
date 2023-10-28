package net.artux.pda.map.ecs.effects;

import com.badlogic.ashley.core.Component;

import java.util.EnumSet;

public class EffectsComponent implements Component {

    private final EnumSet<Effect> effects;

    public EffectsComponent() {
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
