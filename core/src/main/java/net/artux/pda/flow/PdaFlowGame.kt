package net.artux.pda.flow

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import net.artux.pda.flow.network.FlowApiClient
import net.artux.pda.flow.ui.BaseFlowScreen
import net.artux.pda.flow.ui.FlowSkin
import net.artux.pda.flow.ui.RegistrationScreen

/**
 * Root entry point for the iOS pre-game flow: registration -> login -> story selection ->
 * stage dialogue -> map. Replaces MockDataFactory.createApplication() as what IOSLauncher
 * boots; MockDataFactory itself is left in place, unused by this path, as a reference/fallback.
 *
 * A plain com.badlogic.gdx.Game (not net.artux.engine.scenes.SceneManager) - see
 * BaseFlowScreen's doc comment for why the existing Scene/SceneManager/CoreComponent machinery
 * doesn't fit here. PlatformInterface isn't a constructor dependency here (unlike
 * MockDataFactory) - StageScreen builds a fresh FlowPlatformInterface only once real
 * credentials are known, right before handing off to the map.
 */
class PdaFlowGame(val api: FlowApiClient = FlowApiClient()) : Game() {

    lateinit var skin: Skin
        private set

    /** On-screen keyboard height in points (0 = hidden), reported by the platform. */
    var keyboardHeight = 0f
        private set

    val session = FlowSession()

    override fun create() {
        skin = FlowSkin.load()
        setScreen(RegistrationScreen(this))
    }

    /**
     * Use instead of the inherited setScreen()/`screen =`: Game.setScreen() only calls
     * hide() on the outgoing screen, never dispose() - every screen here owns a Stage (and
     * its SpriteBatch/textures), so switching screens without this would leak one every time,
     * the same class of bug fixed earlier in QuestActivity's own screen switching.
     */
    fun goTo(next: Screen) {
        screen?.dispose()
        setScreen(next)
    }

    /**
     * Called on the GL thread by the platform launcher whenever the on-screen keyboard shows,
     * hides or changes size - libGDX itself has no API for this.
     */
    fun onKeyboardHeightChanged(height: Float) {
        keyboardHeight = height
        (screen as? BaseFlowScreen)?.updateKeyboardShift()
    }

    override fun dispose() {
        screen?.dispose()
        skin.dispose()
    }
}
