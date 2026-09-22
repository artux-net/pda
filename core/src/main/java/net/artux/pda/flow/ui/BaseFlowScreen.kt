package net.artux.pda.flow.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
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

    // IO-only: this app has no Android-style "Main" coroutine dispatcher tied to the GL thread,
    // so UI updates after a suspend network call are marshalled back via Gdx.app.postRunnable
    // (see runIO) rather than switching dispatcher.
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        root.setFillParent(true)
        stage.addActor(root)
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

    override fun show() {
        Gdx.input.inputProcessor = stage
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
}
