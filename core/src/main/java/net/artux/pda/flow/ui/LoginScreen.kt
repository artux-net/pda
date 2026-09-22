package net.artux.pda.flow.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.artux.pda.flow.PdaFlowGame

class LoginScreen(game: PdaFlowGame) : BaseFlowScreen(game) {

    private val emailField = TextField("", skin)
    private val passwordField = TextField("", skin).apply {
        isPasswordMode = true
        setPasswordCharacter('*')
    }
    private val status = errorLabel()
    private val loginButton = TextButton("Войти", skin)
    private val registerLinkButton = TextButton("Нет аккаунта? Зарегистрироваться", skin, "menu")

    init {
        root.add(Label("Вход", skin, "title")).padBottom(30f).row()

        root.add(Label("Email", skin)).left().row()
        root.add(emailField).width(400f).padBottom(10f).row()

        root.add(Label("Пароль", skin)).left().row()
        root.add(passwordField).width(400f).padBottom(20f).row()

        root.add(loginButton).width(400f).height(50f).padBottom(10f).row()
        root.add(registerLinkButton).width(400f).padBottom(10f).row()
        root.add(status).width(400f).row()

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
}
