package net.artux.pda.flow.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.artux.pda.flow.PdaFlowGame

/**
 * Shared scaffolding for the pre-game flow's screens (Registration/Login/StorySelection/Stage).
 * Not built on net.artux.engine.scenes.Scene/SceneManager on purpose - that base class needs a
 * CoreComponent (Dagger), which itself needs a DataRepository/GameMap that don't exist yet at
 * this point in the flow (see FlowSession's doc comment). Plain com.badlogic.gdx.Screen instead,
 * orchestrated by PdaFlowGame (a plain Game).
 */
abstract class BaseFlowScreen(protected val game: PdaFlowGame) : Screen {

    protected val skin: Skin get() = game.skin
    protected val stage: Stage = Stage(ScreenViewport())
    protected val root: Table = Table()
    private val scroll = ScrollPane(root)

    // IO-only: this app has no Android-style "Main" coroutine dispatcher tied to the GL thread,
    // so UI updates after a suspend network call are marshalled back via Gdx.app.postRunnable
    // (see runIO) rather than switching dispatcher.
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        // A landscape phone is only ~390 points tall - without scrolling, whatever a screen
        // lays out past that (the status/error label, the last transfer buttons) was simply
        // cut off and unreachable. Shorter content is still centred: ScrollPane stretches
        // root to at least its own size.
        scroll.setFillParent(true)
        scroll.setScrollingDisabled(true, false)
        scroll.setFadeScrollBars(false)
        stage.addActor(scroll)

        // Focus settles after this event (the old field loses it first), so look afterwards.
        stage.addListener(object : FocusListener() {
            override fun keyboardFocusChanged(event: FocusEvent, actor: Actor, focused: Boolean) {
                Gdx.app.postRunnable { updateKeyboardShift() }
            }
        })
    }

    /**
     * While the on-screen keyboard is up, lifts the whole screen so every text field on it sits
     * above the keyboard - on a landscape phone it covers the lower ~45%, which hid the fields
     * below the first row, so the next one couldn't even be tapped. Capped so the focused field
     * never leaves the top edge. Back to rest once the keyboard or the focus goes away.
     */
    fun updateKeyboardShift() {
        val focused = stage.keyboardFocus as? TextField
        val keyboardHeight = game.keyboardHeight
        val target = if (focused == null || keyboardHeight <= 0f) {
            0f
        } else {
            // Positions as they'd be unshifted.
            val lowestBottom = textFields(root).minOf { bottomOf(it) }
            val focusedTop = bottomOf(focused) + focused.height
            (keyboardHeight + KEYBOARD_MARGIN - lowestBottom)
                .coerceAtMost(stage.height - KEYBOARD_MARGIN - focusedTop)
                .coerceAtLeast(0f)
        }
        scroll.clearActions()
        scroll.addAction(Actions.moveTo(0f, target, KEYBOARD_SHIFT_SECONDS, Interpolation.fastSlow))
    }

    /** Launches [block] on IO, then runs [onResult] on the GL thread with its outcome. */
    protected fun <T> runIO(block: suspend () -> Result<T>, onResult: (Result<T>) -> Unit) {
        scope.launch {
            val result = block()
            Gdx.app.postRunnable { onResult(result) }
        }
    }

    protected fun errorLabel(): Label {
        val label = Label("", skin, "default")
        label.setColor(Color.SALMON)
        label.wrap = true
        return label
    }

    private fun bottomOf(actor: Actor): Float = actor.localToStageCoordinates(Vector2(0f, 0f)).y - scroll.y

    private fun textFields(group: Group): List<TextField> = group.children.flatMap { child ->
        when (child) {
            is TextField -> listOf(child)
            is Group -> textFields(child)
            else -> emptyList()
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        updateKeyboardShift()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.086f, 0.09f, 0.098f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

    override fun dispose() {
        scope.cancel()
        stage.dispose()
    }

    private companion object {
        const val KEYBOARD_MARGIN = 16f
        const val KEYBOARD_SHIFT_SECONDS = 0.25f
    }
}
