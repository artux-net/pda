package net.artux.pda.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.app.LoaderManager
import android.content.ActivityNotFoundException
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputType
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.BuildConfig
import net.artux.pda.R
import net.artux.pda.app.PDAApplication
import net.artux.pda.common.PropertyFields
import net.artux.pda.model.StatusModel
import net.artux.pda.model.user.LoginUser
import net.artux.pda.model.user.UserModel
import net.artux.pda.ui.viewmodels.AuthViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
open class LoginActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>,
    View.OnClickListener {

    private var loginUser: LoginUser? = null

    private lateinit var mEmailView: AutoCompleteTextView
    private lateinit var mPasswordView: EditText
    private lateinit var mProgressView: View
    private lateinit var mLoginFormView: View
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    protected lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseRemoteConfig.getLong(PropertyFields.MINIMUM_VERSION)
        if (isAppSupported()) {
            showUpdateDialog()
            authViewModel.logout()
        }
        if (isServerUpdating()) {
            showWaitDialog()
            return
        }
        if (authViewModel.isLoggedIn()) {
            startActivity(Intent(this, LoadingActivity::class.java))
            finish()
        } else {
            mEmailView = findViewById(R.id.email)
            mPasswordView = findViewById(R.id.password)
            mPasswordView.setOnEditorActionListener { _: TextView?, id: Int, _: KeyEvent? ->
                if (id != EditorInfo.IME_ACTION_DONE && id != EditorInfo.IME_NULL) return@setOnEditorActionListener false
                hideKeyboard(this@LoginActivity)
                attemptLogin()
                true
            }
            findViewById<View>(R.id.forgotPassword).setOnClickListener(this)
            findViewById<View>(R.id.register).setOnClickListener(this)
            findViewById<View>(R.id.help).setOnClickListener(this)
            findViewById<View>(R.id.email_sign_in_button).setOnClickListener(this)
            findViewById<View>(R.id.logo).setOnLongClickListener {
                var info =
                    "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) ${BuildConfig.BUILD_TYPE}"
                info = "$info\n${BuildConfig.URL_API}"
                Toast.makeText(this, info, Toast.LENGTH_LONG).show()
                true
            }
            mLoginFormView = findViewById(R.id.login_form)
            mProgressView = findViewById(R.id.login_progress)
        }
        authViewModel.member.observe(this) { userModelResult: UserModel? ->
            showProgress(false)
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
        authViewModel.status.observe(this) { statusModel: StatusModel ->
            showProgress(false)
            if (!statusModel.success) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.error_login),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun attemptLogin() {
        // Reset errors.
        mEmailView.error = null
        mPasswordView.error = null

        // Store values at the time of the login attempt.
        val email = mEmailView.text.toString().replace(" ", "")
        val password = mPasswordView.text.toString().replace(" ", "")
        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.error = getString(R.string.error_invalid_password)
            focusView = mPasswordView
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.error = getString(R.string.error_field_required)
            focusView = mEmailView
            cancel = true
        }
        if (cancel) {
            focusView!!.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginUser = LoginUser(email, password)
            showProgress(true)
            authViewModel.login(loginUser!!)
        }
    }


    private fun isAppSupported() =
        (application as PDAApplication).versionCode < firebaseRemoteConfig.getLong(PropertyFields.MINIMUM_VERSION)
                && !BuildConfig.DEBUG

    private fun isServerUpdating() =
        firebaseRemoteConfig.getBoolean(PropertyFields.SERVER_UPDATING)
                && !BuildConfig.DEBUG

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this, R.style.PDADialogStyle)
        builder.setTitle(R.string.need_update)
        builder.setMessage(getString(R.string.need_update_message))
        builder.setPositiveButton(R.string.update) { dialog, which ->
            Timber.i("onOpenPlayStore")
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${this.packageName}")
                )
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${this.packageName}")
                )
                startActivity(intent)
            }
        }
        val dialog = builder.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun showWaitDialog() {
        val builder = AlertDialog.Builder(this, R.style.PDADialogStyle)
        builder.setTitle(R.string.need_wait)
        builder.setMessage(getString(R.string.need_wait_message))
        builder.setPositiveButton(R.string.iwait) { dialog, which ->
            finish()
        }
        val dialog = builder.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    override fun onResume() {
        super.onResume()
        if (isAppSupported()) {
            showUpdateDialog()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.matches(PASSWORD_VALIDATION_REGEX)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
            mProgressView.visibility = if (show) View.VISIBLE else View.GONE
            /*mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });*/
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.visibility = if (show) View.VISIBLE else View.GONE
            mLoginFormView.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<Cursor> {
        return CursorLoader(
            this,  // Retrieve data rows for the device user's 'profileModel' contact.
            Uri.withAppendedPath(
                ContactsContract.Profile.CONTENT_URI,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY
            ),
            ProfileQuery.PROJECTION,  // Select only email addresses.
            ContactsContract.Contacts.Data.MIMETYPE +
                    " = ?",
            arrayOf(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE),  // Show primary email addresses first. Note that there won't be
            // a primary email address if the user hasn't specified one.
            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        )
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails: MutableList<String> = ArrayList()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }
        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {}
    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        val adapter = ArrayAdapter(
            this@LoginActivity,
            android.R.layout.simple_dropdown_item_1line, emailAddressCollection
        )
        mEmailView.setAdapter(adapter)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.email_sign_in_button) {
            attemptLogin()
        }
        if (v.id == R.id.forgotPassword) {
            val builder = AlertDialog.Builder(this@LoginActivity, R.style.PDADialogStyle)
            builder.setTitle(R.string.action_input_email)
            val input = EditText(this@LoginActivity)
            input.text = mEmailView.text
            input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            builder.setView(input)
            builder.setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
                authViewModel.resetPassword(
                    input.text.toString()
                )
            }
            builder.setNegativeButton(R.string.action_cancel) { dialog: DialogInterface, which: Int -> dialog.cancel() }
            builder.show()
        }
        if (v.id == R.id.register) {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        if (v.id == R.id.help) {
            startActivity(Intent(this@LoginActivity, HelpActivity::class.java))
        }
    }

    private interface ProfileQuery {
        companion object {
            val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY
            )
            const val ADDRESS = 0
        }
    }

    companion object {
        private val PASSWORD_VALIDATION_REGEX = Regex("^[A-Za-z\\d!@#$%^&*()_+№\";:?><\\[\\]{}]*$")
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}