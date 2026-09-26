package net.artux.pda.flow.ui

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Scaling
import net.artux.pda.flow.PdaFlowGame

/**
 * Shown instead of RegistrationScreen when a saved session exists (see FlowSession.save/
 * PdaFlowGame.create) - re-validates the saved credentials against the backend (they could have
 * been changed/revoked elsewhere) before jumping to StorySelectionScreen, instead of trusting
 * them blindly. Falls back to LoginScreen, credentials pre-filled, if that check fails.
 */
class AutoLoginScreen(game: PdaFlowGame, private val email: String, private val password: String) :
    BaseFlowScreen(game) {

    init {
        root.add(Image(skin, "banner").apply { setScaling(Scaling.fit) })
            .height(110f).width(300f).padBottom(20f).row()
        root.add(Label("Вход...", skin, "default")).row()

        runIO({ game.api.checkLogin(email, password) }) { result ->
            result.onSuccess {
                game.session.email = email
                game.session.password = password
                game.goTo(StorySelectionScreen(game))
            }.onFailure {
                game.session.clearSaved()
                game.goTo(LoginScreen(game, prefillEmail = email))
            }
        }
    }
}
