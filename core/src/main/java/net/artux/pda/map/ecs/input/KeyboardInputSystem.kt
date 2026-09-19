package net.artux.pda.map.ecs.input

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.ecs.battle.PlayerBattleSystem
import net.artux.pda.map.ecs.battle.WeaponComponent
import net.artux.pda.map.ecs.characteristics.HealthComponent
import net.artux.pda.map.ecs.physics.PlayerMovingSystem
import net.artux.pda.map.ecs.systems.BaseSystem
import javax.inject.Inject
import kotlin.math.sqrt

/**
 * Polls the hardware keyboard each frame via Gdx.input.isKeyPressed() and drives the same entry
 * points the touch controls use (UserInterfaceModule) and MapInputActions, so a keyboard works
 * alongside touch/gamepad rather than replacing them - a key that's never pressed just never
 * triggers anything here.
 *
 * Mapping:
 *  WASD / arrow keys - move (same setVelocity() the on-screen touchpad calls)
 *  Left shift        - hold to run, mirrors the on-screen run button
 *  F / Space         - hold to shoot, mirrors the on-screen shoot button
 *  Tab               - cycle/lock the nearest visible target, mirrors the on-screen target button
 *  E                 - interact with the nearest available prompt (dialog/loot/transfer)
 *  Q (tap)           - switch weapon; Q (held) - reload, mirrors the weapon slot's tap/long-press
 *  I                 - open/close the backpack, mirrors tapping the HUD
 *  Escape            - close the topmost window on the UI stack, mirrors the header pause button
 */
@PerGameMap
class KeyboardInputSystem @Inject constructor(
    private val playerMovingSystem: PlayerMovingSystem,
    private val playerBattleSystem: PlayerBattleSystem,
    private val mapInputActions: MapInputActions
) : BaseSystem(Family.one().get()) {

    private val hm = ComponentMapper.getFor(HealthComponent::class.java)
    private val wm = ComponentMapper.getFor(WeaponComponent::class.java)

    private var movementWasActive = false

    private var shootWasDown = false
    private var tabWasDown = false
    private var eWasDown = false
    private var iWasDown = false
    private var escWasDown = false

    private var qWasDown = false
    private var qHoldTime = 0f
    private var qReloadFired = false
    private val reloadHoldSeconds = 0.5f

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (!isPlayerActive) return

        updateMovement()

        // Only call setPlayerShoot() while this key actually has something to say (held, or just
        // released) - GamepadInputSystem calls it too, and both calling it unconditionally every
        // frame would have whichever ran last in the engine's system order silently override the
        // other's state every single frame, even when neither key/button is actually pressed.
        val shootDown = Gdx.input.isKeyPressed(Input.Keys.F) || Gdx.input.isKeyPressed(Input.Keys.SPACE)
        if (shootDown || shootWasDown) playerBattleSystem.setPlayerShoot(shootDown)
        shootWasDown = shootDown

        val tabDown = Gdx.input.isKeyPressed(Input.Keys.TAB)
        if (tabDown && !tabWasDown) mapInputActions.cycleTarget(player)
        tabWasDown = tabDown

        val eDown = Gdx.input.isKeyPressed(Input.Keys.E)
        if (eDown && !eWasDown) mapInputActions.interact()
        eWasDown = eDown

        val iDown = Gdx.input.isKeyPressed(Input.Keys.I)
        if (iDown && !iWasDown) mapInputActions.toggleBackpack()
        iWasDown = iDown

        val escDown = Gdx.input.isKeyPressed(Input.Keys.ESCAPE)
        if (escDown && !escWasDown) mapInputActions.closeTopWindow()
        escWasDown = escDown

        updateWeaponKey(deltaTime)
    }

    private fun updateMovement() {
        var x = 0f
        var y = 0f
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) x -= 1f
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) x += 1f
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) y -= 1f
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) y += 1f

        // Pressing two keys at once (e.g. W+D) would otherwise move faster diagonally.
        val length = sqrt(x * x + y * y)
        if (length > 1f) {
            x /= length
            y /= length
        }

        val active = x != 0f || y != 0f
        if (active || movementWasActive) {
            playerMovingSystem.setVelocity(x, y)
            val stamina = hm.get(player).stamina
            playerMovingSystem.setRunning(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && stamina > 10f)
        }
        movementWasActive = active
    }

    private fun updateWeaponKey(deltaTime: Float) {
        val qDown = Gdx.input.isKeyPressed(Input.Keys.Q)
        if (qDown) {
            if (!qWasDown) {
                qHoldTime = 0f
                qReloadFired = false
            }
            qHoldTime += deltaTime
            if (qHoldTime >= reloadHoldSeconds && !qReloadFired) {
                playerBattleSystem.reload()
                qReloadFired = true
            }
        } else if (qWasDown && !qReloadFired) {
            wm.get(player).switchWeapons()
        }
        qWasDown = qDown
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {}
}
