package net.artux.pda.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.artux.pda.Models.RegisterUser;
import net.artux.pda.Models.Status;
import net.artux.pda.PdaAPI;
import net.artux.pda.R;
import net.artux.pda.app.App;

import java.io.IOException;

import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    RegisterUser mRegisterUser;

    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mLoginView;
    private EditText mEmailView;
    private EditText mNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    int avatarId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mLoginView = findViewById(R.id.login);

        mEmailView = findViewById(R.id.email);

        mNameView = findViewById(R.id.name);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.avatarView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RegisterActivity.this, PickImageActivity.class),0);
            }
        });


        mLoginView.setText("hhh");
        mEmailView.setText("hhdshfsjkhfjksdhfjksdhf131231432543543654343341234578798h@gmail.com");
        mPasswordView.setText("12345678");
        mNameView.setText("hhh");

        Button mRegisterBtn = findViewById(R.id.registerBtn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString().replace(" ", "");
        String password = mPasswordView.getText().toString().replace(" ", "");

        boolean cancel = false;
        View focusView = null;

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
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_password));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mRegisterUser = new RegisterUser(mLoginView.getText().toString(),
                    mNameView.getText().toString(),
                    mEmailView.getText().toString(),
                    mPasswordView.getText().toString(),
                    avatarId);
            showProgress(true);
            mAuthTask = new UserRegisterTask(mRegisterUser);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") & email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        avatarId = resultCode;
        ImageView avatarPreview = findViewById(R.id.avatarPreview);
        avatarPreview.setImageDrawable(getResources().getDrawable(App.avatars[resultCode]));
    }

    /**
     * Shows the progress UI and hides the login form.
     */
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
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public class UserRegisterTask extends AsyncTask<Void, Void, Status> {

        private final RegisterUser mRegisterUser;


        UserRegisterTask(RegisterUser registerUser) {
            mRegisterUser = registerUser;
        }

        @Override
        protected net.artux.pda.Models.Status doInBackground(Void... params) {

            net.artux.pda.Models.Status status = new net.artux.pda.Models.Status();
            PdaAPI pdaAPI = App.getRetrofitService().getPdaAPI();

            try {
                Response<net.artux.pda.Models.Status> response = pdaAPI.registerUser(mRegisterUser).execute();
                if (response.body()!=null) {
                    status = response.body();
                    if (status != null) {
                        if (status.isSuccess()) {
                            return status;
                        }
                    }
                }
            } catch (IOException e) {
                status.setDescription(e.getMessage());
            }

            return status;
        }

        @Override
        protected void onPostExecute(final net.artux.pda.Models.Status status) {
            mAuthTask = null;
            showProgress(false);

            if (status!=null) {
                if (status.isSuccess()) {
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, status.getDescription(), Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                Log.d("Status", status.toString());
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
