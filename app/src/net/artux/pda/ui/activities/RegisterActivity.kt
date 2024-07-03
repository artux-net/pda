package net.artux.pda.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.R
import net.artux.pda.model.StatusModel
import net.artux.pda.model.user.RegisterUserModel
import net.artux.pda.ui.activities.adapters.AvatarsAdapter
import net.artux.pda.ui.viewmodels.AuthViewModel
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Random

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var registerUserModel: RegisterUserModel

    // UI references.
    private lateinit var mNicknameView: EditText
    private lateinit var mEmailView: EditText
    private lateinit var mPasswordView: EditText
    private lateinit var mRepeatPasswordView: EditText
    private lateinit var mProgressView: View
    private lateinit var mLoginFormView: View
    private lateinit var avatarsAdapter: AvatarsAdapter
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        avatarsAdapter = AvatarsAdapter()
        if (authViewModel.isLoggedIn()) {
            startActivity(Intent(this, LoadingActivity::class.java))
            finish()
        }
        mNicknameView = findViewById(R.id.nickname)
        mEmailView = findViewById(R.id.email)
        mPasswordView = findViewById(R.id.password)
        mRepeatPasswordView = findViewById(R.id.repeat_password)
        findViewById<View>(R.id.agreement).setOnClickListener { view: View? ->
            val url = getString(R.string.privacy_url)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.avatars)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = avatarsAdapter
        mPasswordView.setOnEditorActionListener { textView: TextView?, id: Int, keyEvent: KeyEvent? ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptRegister()
                return@setOnEditorActionListener true
            }
            false
        }
        val mRegisterBtn = findViewById<Button>(R.id.registerBtn)
        mRegisterBtn.setOnClickListener { view: View? -> attemptRegister() }
        mLoginFormView = findViewById(R.id.login_form)
        mProgressView = findViewById(R.id.register_progress)
        authViewModel.status.observe(this) { status: StatusModel ->
            showProgress(false)
            if (status.success) {
                val intent = Intent(this@RegisterActivity, FinishRegistrationActivity::class.java)
                intent.putExtra("email", registerUserModel.email)
                intent.putExtra("description", status.description)
                startActivity(intent)
                finish()
            } else {
                Snackbar.make(mLoginFormView, status.description, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok") { }
                    .show()
            }
        }
        loadTemplates()
    }

    fun loadTemplates() {
        try {
            val nicknameBuilder = StringBuilder()
            var resource = assets.open("templates/names")
            var br = BufferedReader(InputStreamReader(resource, StandardCharsets.UTF_8))
            val random = Random()
            br.lines()
                .skip(random.nextInt(34).toLong())
                .findAny()
                .ifPresent { name: String -> nicknameBuilder.append(name) }
            br.close()
            resource = assets.open("templates/nicks")
            br = BufferedReader(InputStreamReader(resource, StandardCharsets.UTF_8))
            br.lines()
                .skip(random.nextInt(34).toLong())
                .findAny()
                .ifPresent { name: String -> nicknameBuilder.append(" $name") }
            br.close()
            mNicknameView.setText(nicknameBuilder)
        } catch (ignored: IOException) {
            Timber.e(ignored)
        }
    }

    private fun isViewWithIncorrectData(editText: EditText, regex: String): Boolean {
        val value = editText.text.toString()
        if (TextUtils.isEmpty(value)) {
            editText.error = getString(R.string.error_field_required)
            return true
        } else if (!value.matches(regex.toRegex())) {
            editText.error = getString(
                R.string.error_invalid_value,
                join(", ", checkStringSymbolsByRegexp(value, regex))
            )
            return true
        }
        return false
    }

    private fun join(delimiter: String, collection: Collection<String>): String {
        val joinBuilder = StringBuilder()
        val strings = collection.toTypedArray()
        val lastIndex = strings.size - 1
        for (i in 0 until lastIndex) {
            joinBuilder.append(strings[i]).append(delimiter)
        }
        joinBuilder.append(strings[lastIndex])
        return joinBuilder.toString()
    }

    private fun attemptRegister() {
        // Reset errors.
        mNicknameView.error = null
        mEmailView.error = null
        mPasswordView.error = null
        mRepeatPasswordView.error = null
        var cancel = false
        var focusView: View? = null
        if (isViewWithIncorrectData(mEmailView, EMAIL_VALIDATION_REGEX)) {
            focusView = mEmailView
            cancel = true
        }
        if (isViewWithIncorrectData(mNicknameView, NAME_VALIDATION_REGEX)) {
            focusView = mNicknameView
            cancel = true
        }
        if (isViewWithIncorrectData(mPasswordView, PASSWORD_VALIDATION_REGEX)) {
            focusView = mPasswordView
            cancel = true
        }
        if (mPasswordView.text.toString() != mRepeatPasswordView.text.toString()) {
            mRepeatPasswordView.error = getString(R.string.notEqualPasswords)
            focusView = mRepeatPasswordView
            cancel = true
        }
        if (cancel && focusView != null) {
            focusView.requestFocus()
        } else {
            registerUserModel = RegisterUserModel(
                mNicknameView.text.toString(),
                mEmailView.text.toString(),
                mPasswordView.text.toString(), avatarsAdapter.selected.toString()
            )
            showProgress(true)
            authViewModel.registerUser(registerUserModel)
        }
    }

    private fun checkStringSymbolsByRegexp(str: String, regexp: String): Collection<String> {
        val result: MutableCollection<String> = ArrayList()
        for (chr in str.toCharArray()) {
            val chrOfStr = chr.toString()
            if (!chrOfStr.matches(regexp.toRegex())) {
                result.add(chrOfStr)
            }
        }
        return result
    }

    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)
        mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
        mLoginFormView.animate().setDuration(shortAnimTime.toLong()).alpha(
            (
                    if (show) 0 else 1).toFloat()
        ).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
            }
        })
        mProgressView.visibility = if (show) View.VISIBLE else View.GONE
        mProgressView.animate().setDuration(shortAnimTime.toLong()).alpha(
            (
                    if (show) 1 else 0).toFloat()
        ).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mProgressView.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    companion object {
        private const val EMAIL_VALIDATION_REGEX =
            "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
        private const val NAME_VALIDATION_REGEX = "^[A-Za-z\u0400-\u052F' ]*$"
        private const val PASSWORD_VALIDATION_REGEX = "^[A-Za-z\\d!@#$%^&*()_+№\";:?><\\[\\]{}]*$"
    }
}