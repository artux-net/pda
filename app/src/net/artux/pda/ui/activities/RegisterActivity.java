package net.artux.pda.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.pda.R;
import net.artux.pda.app.App;
import net.artux.pda.ui.activities.adapters.AvatarsAdapter;
import net.artux.pdalib.RegisterUser;
import net.artux.pdalib.Status;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    RegisterUser mRegisterUser;

    // UI references.
    private EditText mLoginView;
    private EditText mNameView;
    private EditText mNicknameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mRepeatPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    AvatarsAdapter avatarsAdapter = new AvatarsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mLoginView = findViewById(R.id.login);
        mNameView = findViewById(R.id.name);
        mNicknameView = findViewById(R.id.nickname);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mRepeatPasswordView = findViewById(R.id.repeat_password);
        findViewById(R.id.agreement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.artux.net/privacy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.avatars);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(avatarsAdapter);


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
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRepeatPasswordView.setError(null);

        String email = mEmailView.getText().toString().replace(" ", "");
        String password = mPasswordView.getText().toString().replace(" ", "");

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;

            cancel = true;
        }

        if (!mPasswordView.getText().toString().equals(mRepeatPasswordView.getText().toString())){
            mRepeatPasswordView.setError("Passwords are not equal.");
            focusView = mRepeatPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mRegisterUser = new RegisterUser(mLoginView.getText().toString(),
                    mNameView.getText().toString(),
                    mNicknameView.getText().toString(),
                    mEmailView.getText().toString(),
                    mPasswordView.getText().toString(),
                    avatarsAdapter.getSelected());
            showProgress(true);
            App.getRetrofitService().getPdaAPI().registerUser(mRegisterUser).enqueue(new Callback<Status>() {
                @Override
                public void onResponse(Call<Status> call, Response<Status> response) {
                    showProgress(false);
                    Status status = response.body();
                    if (status!=null){
                        Toast.makeText(RegisterActivity.this, status.getDescription(), Toast.LENGTH_LONG)
                                .show();
                        if (status.isSuccess()) {
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Status> call, Throwable throwable) {
                    showProgress(false);
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") & email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
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
}
