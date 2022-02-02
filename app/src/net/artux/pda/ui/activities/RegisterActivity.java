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

import java.util.ArrayList;
import java.util.Collection;

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

    private static final String EMAIL_VALIDATION_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private static final String LOGIN_VALIDATION_REGEX = "^[a-zA-Z0-9-_.]+$";
    private static final String NAME_VALIDATION_REGEX = "^[A-Za-z\u0400-\u052F']*$";
    private static final String PASSWORD_VALIDATION_REGEX = "^[A-Za-z\\d!@#$%^&*()_+№\";:?><\\[\\]{}]*$";


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
                String url = "https://www.artux.net/privacy";
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

    boolean isViewWithIncorrectData(EditText editText, String regex){
        String value = editText.getText().toString();
        if (TextUtils.isEmpty(value)) {
            editText.setError(getString(R.string.error_field_required));
            return true;
        }else if(!value.matches(regex)){
            editText.setError(getString(R.string.error_invalid_value,
                    join(", ", checkStringSymbolsByRegexp(value, regex))));
            return true;
        }
        return false;
    }

    private String join(String delimiter, Collection<String> collection){
        StringBuilder joinBuilder = new StringBuilder();
        String[] strings = collection.toArray(new String[0]);
        int lastIndex = strings.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            joinBuilder.append(strings[i]).append(delimiter);
        }
        joinBuilder.append(strings[lastIndex]);
        return joinBuilder.toString();
    }

    private void attemptRegister() {
        // Reset errors.
        mLoginView.setError(null);
        mNameView.setError(null);
        mNicknameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRepeatPasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (isViewWithIncorrectData(mEmailView, EMAIL_VALIDATION_REGEX)) {
            focusView = mEmailView;
            cancel = true;
        }

        if (isViewWithIncorrectData(mLoginView, LOGIN_VALIDATION_REGEX)) {
            focusView = mLoginView;
            cancel = true;
        }

        if (isViewWithIncorrectData(mNameView, NAME_VALIDATION_REGEX)) {
            focusView = mNameView;
            cancel = true;
        }

        if (isViewWithIncorrectData(mNicknameView, NAME_VALIDATION_REGEX)) {
            focusView = mNicknameView;
            cancel = true;
        }

        if (isViewWithIncorrectData(mPasswordView, PASSWORD_VALIDATION_REGEX)) {
            focusView = mPasswordView;
            cancel = true;
        }

        if (!mPasswordView.getText().toString().equals(mRepeatPasswordView.getText().toString())){
            mRepeatPasswordView.setError(getString(R.string.notEqualPasswords));
            focusView = mRepeatPasswordView;
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
                    if (status!=null)
                        if (status.isSuccess()) {
                            Intent intent = new Intent(RegisterActivity.this, FinishRegistrationActivity.class);
                            intent.putExtra("email", mRegisterUser.getEmail());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, status.getDescription(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    else{
                        Toast.makeText(RegisterActivity.this, getString(R.string.wrong_response), Toast.LENGTH_LONG)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<Status> call, Throwable throwable) {
                    showProgress(false);
                    Toast.makeText(RegisterActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
    }

    private Collection<String> checkStringSymbolsByRegexp(String str, String regexp) {
        Collection<String> result = new ArrayList<>();
        for (char chr : str.toCharArray()) {
            String chrOfStr = Character.toString(chr);
            if (!chrOfStr.matches(regexp)) {
                result.add(chrOfStr);
            }
        }
        return result;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
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
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
