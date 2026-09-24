package net.artux.pda.flow.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Scaling
import net.artux.pda.flow.PdaFlowGame

class LoginScreen(game: PdaFlowGame) : BaseFlowScreen(game) {

    private val emailField = TextField("", skin).apply { messageText = "Электронная почта" }
    private val passwordField = TextField("", skin).apply {
        messageText = "Пароль"
        isPasswordMode = true
        setPasswordCharacter('*')
    }
    private val status = errorLabel()
    private val loginButton = TextButton("ВОЙТИ", skin)
    private val registerLinkButton = TextButton("РЕГИСТРАЦИЯ", skin)

    init {
        // activity_login.xml: banner on top, both fields in one row (hints, no labels), then the
        // underlined actions aligned to the end.
        root.add(Image(skin, "banner").apply { setScaling(Scaling.fit) })
            .height(110f).width(FORM_WIDTH).padBottom(20f).row()

        val fields = Table()
        fields.add(emailField).width(COLUMN_WIDTH).padRight(COLUMN_GAP)
        fields.add(passwordField).width(COLUMN_WIDTH)
        root.add(fields).padBottom(15f).row()

        val actions = Table()
        actions.add(loginButton).padRight(20f)
        actions.add(registerLinkButton)
        root.add(actions).width(FORM_WIDTH).right().padBottom(10f).row()
        actions.right()

        root.add(status).width(FORM_WIDTH).row()

        loginButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) = onLoginClicked()
        })
        registerLinkButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.goTo(RegistrationScreen(game))
            }
        })
    }

    private fun onLoginClicked() {
        val email = emailField.text.trim()
        val password = passwordField.text

        if (email.isEmpty() || password.isEmpty()) {
            status.setText("Заполните все поля")
            return
        }

        status.setText("Вход...")
        loginButton.isDisabled = true

        runIO({ game.api.checkLogin(email, password) }) { result ->
            loginButton.isDisabled = false
            result.onSuccess {
                game.session.email = email
                game.session.password = password
                game.goTo(StorySelectionScreen(game))
            }.onFailure {
                status.setText("Неверный логин или пароль")
            }
        }
    }

    private companion object {
        const val COLUMN_WIDTH = 300f
        const val COLUMN_GAP = 20f
        const val FORM_WIDTH = COLUMN_WIDTH * 2 + COLUMN_GAP
    }
}
