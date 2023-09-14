package net.artux.pda.map.ecs.battle

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Array

class InfightingComponent(
    val distance: Float,
    val damage: Float,
    val interval: Float
) : Component {
    private var timeout = 0f

    val sounds: Array<Sound> = Array(10)

    fun update(dt: Float) {
        if (timeout > 0) timeout -= dt
    }

    fun canDamageSomebody(): Boolean {
        if (timeout <= 0) {
            timeout += interval
            return true
        }
        return false
    }
}