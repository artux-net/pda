package net.artux.pda.flow.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Scaling
import net.artux.pda.flow.PdaFlowGame

class RegistrationScreen(game: PdaFlowGame) : BaseFlowScreen(game) {

    private val nicknameField = TextField("", skin).apply { messageText = "Прозвище" }
    private val emailField = TextField("", skin).apply { messageText = "Email" }
    private val passwordField = TextField("", skin).apply {
        messageText = "Пароль"
        isPasswordMode = true
        setPasswordCharacter('*')
    }
    private val repeatPasswordField = TextField("", skin).apply {
        messageText = "Повторите пароль"
        isPasswordMode = true
        setPasswordCharacter('*')
    }
    private val status = errorLabel()
    private val registerButton = TextButton("РЕГИСТРАЦИЯ", skin)
    private val loginLinkButton = TextButton("ВОЙТИ", skin)

    init {
        // activity_register.xml: banner, a 2x2 grid of hinted fields (email | nickname,
        // password | repeat), then the underlined action at the end. Android gets back to login
        // with the system back button; there's none here, hence the extra "ВОЙТИ" on the left.
        root.add(Image(skin, "banner").apply { setScaling(Scaling.fit) })
            .height(80f).width(FORM_WIDTH).padBottom(15f).row()

        val fields = Table()
        fields.defaults().width(COLUMN_WIDTH).padBottom(12f)
        fields.add(emailField).padRight(COLUMN_GAP)
        fields.add(nicknameField).row()
        fields.add(passwordField).padRight(COLUMN_GAP)
        fields.add(repeatPasswordField).row()
        root.add(fields).padBottom(8f).row()

        val actions = Table()
        actions.add(loginLinkButton).expandX().left()
        actions.add(registerButton).right()
        root.add(actions).width(FORM_WIDTH).padBottom(10f).row()

        root.add(status).width(FORM_WIDTH).row()

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

    private companion object {
        const val COLUMN_WIDTH = 300f
        const val COLUMN_GAP = 20f
        const val FORM_WIDTH = COLUMN_WIDTH * 2 + COLUMN_GAP
    }
}
