package net.artux.pda.map.engine.ecs.components

import com.badlogic.ashley.core.Component

class InfightingComponent(val distance: Float, val damage: Float, val interval: Float) : Component {
    private var timeout = 0f

    fun update(dt: Float) {
        if (timeout > 0) timeout -= dt
    }

    fun canDamageSomebody(): Boolean {
        if (timeout <= 0) {
            timeout += interval;
            return true;
        }
        return false;
    }
}