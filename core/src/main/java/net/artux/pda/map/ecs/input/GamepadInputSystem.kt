package net.artux.pda.map.ecs.input

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerMapping
import com.badlogic.gdx.controllers.Controllers
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.ecs.battle.PlayerBattleSystem
import net.artux.pda.map.ecs.battle.WeaponComponent
import net.artux.pda.map.ecs.characteristics.HealthComponent
import net.artux.pda.map.ecs.physics.PlayerMovingSystem
import net.artux.pda.map.ecs.systems.BaseSystem
import javax.inject.Inject
import kotlin.math.sqrt

/**
 * Polls the current physical gamepad (via libGDX's Controllers API) each frame and drives the
 * same entry points the touch controls use (UserInterfaceModule) and MapInputActions, so a
 * controller works alongside touch rather than replacing it - Controllers.getCurrent() is simply
 * null when nothing is connected, and this system then does nothing.
 *
 * Mapping (see ControllerMapping - a generic Xbox/PS-style layout, this library's standard
 * abstraction for "known" controllers):
 *  Left stick        - move (same setVelocity() the on-screen touchpad calls)
 *  Left stick click  - hold to run, mirrors the on-screen run button
 *  R2                - hold to shoot, mirrors the on-screen shoot button
 *  R1                - cycle/lock the nearest visible target, mirrors the on-screen target button
 *  A                 - interact with the nearest available prompt (dialog/loot/transfer)
 *  X (tap)           - switch weapon; X (held) - reload, mirrors the weapon slot's tap/long-press
 *  Y                 - open/close the backpack, mirrors tapping the HUD
 *  B                 - close the topmost window on the UI stack, mirrors the header pause button
 */
@PerGameMap
class GamepadInputSystem @Inject constructor(
    private val playerMovingSystem: PlayerMovingSystem,
    private val playerBattleSystem: PlayerBattleSystem,
    private val mapInputActions: MapInputActions
) : BaseSystem(Family.one().get()) {

    private val hm = ComponentMapper.getFor(HealthComponent::class.java)
    private val wm = ComponentMapper.getFor(WeaponComponent::class.java)

    private val stickDeadzone = 0.2f
    private var stickWasActive = false

    private var r1WasDown = false
    private var aWasDown = false
    private var yWasDown = false
    private var bWasDown = false

    private var xWasDown = false
    private var xHoldTime = 0f
    private var xReloadFired = false
    private val reloadHoldSeconds = 0.5f

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (!isPlayerActive) return

        val controller = Controllers.getCurrent() ?: return
        val mapping = controller.mapping

        updateMovement(controller, mapping)

        val shootDown = controller.getButton(mapping.buttonR2)
        playerBattleSystem.setPlayerShoot(shootDown)

        val r1Down = controller.getButton(mapping.buttonR1)
        if (r1Down && !r1WasDown) mapInputActions.cycleTarget(player)
        r1WasDown = r1Down

        val aDown = controller.getButton(mapping.buttonA)
        if (aDown && !aWasDown) mapInputActions.interact()
        aWasDown = aDown

        val yDown = controller.getButton(mapping.buttonY)
        if (yDown && !yWasDown) mapInputActions.toggleBackpack()
        yWasDown = yDown

        val bDown = controller.getButton(mapping.buttonB)
        if (bDown && !bWasDown) mapInputActions.closeTopWindow()
        bWasDown = bDown

        updateWeaponButton(controller, mapping, deltaTime)
    }

    private fun updateMovement(controller: Controller, mapping: ControllerMapping) {
        var x = controller.getAxis(mapping.axisLeftX)
        // SDL/HID convention (which this axis follows) reports pushing the stick up as negative,
        // while setVelocity()'s touchpad-based caller treats +Y as up - flip it to match.
        var y = -controller.getAxis(mapping.axisLeftY)
        if (sqrt(x * x + y * y) < stickDeadzone) {
            x = 0f
            y = 0f
        }

        val active = x != 0f || y != 0f
        if (active || stickWasActive) {
            playerMovingSystem.setVelocity(x, y)
            val stamina = hm.get(player).stamina
            playerMovingSystem.setRunning(controller.getButton(mapping.buttonLeftStick) && stamina > 10f)
        }
        stickWasActive = active
    }

    private fun updateWeaponButton(controller: Controller, mapping: ControllerMapping, deltaTime: Float) {
        val xDown = controller.getButton(mapping.buttonX)
        if (xDown) {
            if (!xWasDown) {
                xHoldTime = 0f
                xReloadFired = false
            }
            xHoldTime += deltaTime
            if (xHoldTime >= reloadHoldSeconds && !xReloadFired) {
                playerBattleSystem.reload()
                xReloadFired = true
            }
        } else if (xWasDown && !xReloadFired) {
            wm.get(player).switchWeapons()
        }
        xWasDown = xDown
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {}
}
