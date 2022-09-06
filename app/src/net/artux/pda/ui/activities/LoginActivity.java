package net.artux.pda.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import net.artux.pda.R;
import net.artux.pda.model.user.LoginUser;
import net.artux.pda.ui.viewmodels.AuthViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private LoginUser loginUser;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private AuthViewModel authViewModel;

    private static final String PASSWORD_VALIDATION_REGEX = "^[A-Za-z\\d!@#$%^&*()_+№\";:?><\\[\\]{}]*$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (authViewModel == null)
            authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (authViewModel.isLoggedIn()) {
            startActivity(new Intent(this, LoadingActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_login);
            View mResetPassword = findViewById(R.id.forgotPassword);
            mResetPassword.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle(R.string.action_input_login_or_email);

                final EditText input = new EditText(LoginActivity.this);
                input.setText(mEmailView.getText());
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    authViewModel.resetPassword(input.getText().toString());
                });
                builder.setNegativeButton(R.string.action_cancel, (dialog, which) -> dialog.cancel());

                builder.show();
            });
            mEmailView = findViewById(R.id.email);

            mPasswordView = findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    hideKeyboard(LoginActivity.this);
                    attemptLogin();
                    return true;
                }
                return false;
            });

            findViewById(R.id.register).setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
            findViewById(R.id.help).setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, HelpActivity.class)));
            View mEmailSignInButton = findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(view -> attemptLogin());

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }
        authViewModel.getMember().observe(this, userModelResult -> {
            showProgress(false);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
        authViewModel.getStatus().observe(this, statusModel -> {
            showProgress(false);
            Toast.makeText(LoginActivity.this, statusModel.getDescription(), Toast.LENGTH_LONG).show();
        });
        authViewModel.login();
    }

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().replace(" ", "");
        String password = mPasswordView.getText().toString().replace(" ", "");

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loginUser = new LoginUser(email, password);
            showProgress(true);
            authViewModel.login(loginUser);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.matches(PASSWORD_VALIDATION_REGEX);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
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
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profileModel' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }
}

