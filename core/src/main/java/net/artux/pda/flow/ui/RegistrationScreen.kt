package net.artux.pda.flow.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.artux.pda.flow.PdaFlowGame

class RegistrationScreen(game: PdaFlowGame) : BaseFlowScreen(game) {

    private val nicknameField = TextField("", skin)
    private val emailField = TextField("", skin)
    private val passwordField = TextField("", skin).apply {
        isPasswordMode = true
        setPasswordCharacter('*')
    }
    private val repeatPasswordField = TextField("", skin).apply {
        isPasswordMode = true
        setPasswordCharacter('*')
    }
    private val status = errorLabel()
    private val registerButton = TextButton("Зарегистрироваться", skin)
    private val loginLinkButton = TextButton("Уже есть аккаунт? Войти", skin, "menu")

    init {
        root.add(Label("Регистрация", skin, "title")).padBottom(30f).row()

        root.add(Label("Никнейм", skin)).left().row()
        root.add(nicknameField).width(400f).padBottom(10f).row()

        root.add(Label("Email", skin)).left().row()
        root.add(emailField).width(400f).padBottom(10f).row()

        root.add(Label("Пароль", skin)).left().row()
        root.add(passwordField).width(400f).padBottom(10f).row()

        root.add(Label("Повторите пароль", skin)).left().row()
        root.add(repeatPasswordField).width(400f).padBottom(20f).row()

        root.add(registerButton).width(400f).height(50f).padBottom(10f).row()
        root.add(loginLinkButton).width(400f).padBottom(10f).row()
        root.add(status).width(400f).row()

        registerButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) = onRegisterClicked()
        })
        loginLinkButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.goTo(LoginScreen(game))
            }
        })
    }

    private fun onRegisterClicked() {
        val nickname = nicknameField.text.trim()
        val email = emailField.text.trim()
        val password = passwordField.text
        val repeat = repeatPasswordField.text

        if (nickname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            status.setText("Заполните все поля")
            return
        }
        if (password != repeat) {
            status.setText("Пароли не совпадают")
            return
        }

        status.setText("Регистрация...")
        registerButton.isDisabled = true

        runIO({ registerAndLogin(nickname, email, password) }) { result ->
            registerButton.isDisabled = false
            result.onSuccess {
                game.session.email = email
                game.session.password = password
                game.goTo(StorySelectionScreen(game))
            }.onFailure {
                status.setText("Ошибка регистрации: ${it.message}")
            }
        }
    }

    // Mirrors the Android app's FinishRegistrationActivity: registering doesn't itself return
    // a session, so we immediately verify the same credentials work before moving on.
    private suspend fun registerAndLogin(nickname: String, email: String, password: String): Result<Unit> {
        val registerResult = game.api.register(nickname, email, password)
        if (registerResult.isFailure) {
            return Result.failure(registerResult.exceptionOrNull()!!)
        }
        return game.api.checkLogin(email, password).map { }
    }
}
