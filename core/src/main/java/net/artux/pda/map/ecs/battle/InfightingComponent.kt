package net.artux.pda.map.ecs.battle

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Array
import net.artux.pda.map.ecs.characteristics.HealthComponent
import net.artux.pda.map.ecs.effects.Effect

class InfightingComponent(
    val distance: Float,
    val damage: Float,
    val interval: Float,
    var damageType: HealthComponent.DamageType = HealthComponent.DamageType.SIMPLE
) : Component {
    private var timeout = 0f

    val sounds: Array<Sound> = Array(10)
    var additionalEffect: Effect? = null


    var effectTime: Int = 3

    fun setEffect(effect: Effect, secs: Int){
        this.additionalEffect = effect
        this.effectTime = secs
    }

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