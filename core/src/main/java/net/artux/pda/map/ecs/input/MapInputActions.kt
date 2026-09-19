package net.artux.pda.map.ecs.input

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.ecs.battle.MoodComponent
import net.artux.pda.map.ecs.interactive.InteractionSystem
import net.artux.pda.map.ecs.vision.VisionComponent
import net.artux.pda.map.view.root.UserInterface
import net.artux.pda.map.view.view.window.BackpackWindow
import javax.inject.Inject

/**
 * The map-screen actions shared by every non-touch input source (gamepad, keyboard, ...), each
 * mirroring what the on-screen touch controls already do (see UserInterfaceModule) so all input
 * methods stay in sync with a single implementation.
 */
@PerGameMap
class MapInputActions @Inject constructor(
    private val interactionSystem: InteractionSystem,
    private val userInterface: UserInterface,
    private val backpackWindow: BackpackWindow
) {
    private val mm = ComponentMapper.getFor(MoodComponent::class.java)
    private val vcm = ComponentMapper.getFor(VisionComponent::class.java)

    fun interact() {
        interactionSystem.interactiveComponents.firstOrNull()?.interact()
    }

    /** Mirrors the on-screen target button's ChangeListener in UserInterfaceModule. */
    fun cycleTarget(player: Entity) {
        val moodComponent = mm.get(player)
        val playerEnemies = vcm.get(player).visibleEntities
        val current = moodComponent.enemy
        if (playerEnemies.size > 1) {
            val iterator = playerEnemies.iterator()
            if (current === playerEnemies.last() || current == null || !playerEnemies.contains(current))
                moodComponent.enemy = playerEnemies.first()
            else while (iterator.hasNext()) {
                if (iterator.next() === current) {
                    moodComponent.enemy = iterator.next()
                    break
                }
            }
        } else if (playerEnemies.size == 1)
            moodComponent.enemy = playerEnemies.first()
    }

    /** Mirrors the HUD tap listener in UserInterfaceModule. */
    fun toggleBackpack() {
        val stack = userInterface.stack
        if (stack.children.contains(backpackWindow, false))
            stack.removeActor(backpackWindow)
        else {
            stack.add(backpackWindow)
            backpackWindow.update()
        }
    }

    /** Mirrors the header pause button in HeaderInterfaceModule. */
    fun closeTopWindow() {
        val stack = userInterface.stack
        val lastIndex = stack.children.size - 1
        if (stack.children.size > 1)
            stack.removeActorAt(lastIndex, true)
    }
}
