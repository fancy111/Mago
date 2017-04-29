package com.monster.fancy.debug.mago;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;


public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView login_phone;
    private EditText login_password;
    private String phone, password;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if the user has logged in,jump to the main page
        if (AVUser.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            this.finish();
        }
        setContentView(R.layout.activity_login);

        login_phone = (AutoCompleteTextView) findViewById(R.id.login_phone_edt);
        login_password = (EditText) findViewById(R.id.login_password_edt);
        mProgressView = findViewById(R.id.mProgressView);
    }

    //the on click event for login button
    public void login(View view) {
        phone = login_phone.getText().toString();
        password = login_password.getText().toString();
        attemptLogin(phone, password);
    }

    public void forgetPassword(View view) {
        //Intent intent = new Intent(LoginActivity.this,.class);
        //startActivity(intent);
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(String phone, String password) {
//        if (mAuthTask != null) {
//            return;
//        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            login_password.setError(getString(R.string.error_invalid_password));
            focusView = login_password;
            cancel = true;
        }

        // Check for a valid phone number.
        if (TextUtils.isEmpty(phone)) {
            login_phone.setError(getString(R.string.error_invalid_mobile_phone));
            focusView = login_phone;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            AVUser.loginByMobilePhoneNumberInBackground(phone, password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    //if log in success
                    if (avUser != null) {
                        Log.d("Login", "Successd with mobile phone number.");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        showProgress(false);
                        Toast.makeText(getBaseContext(), getString(R.string.error_invalid_phone_or_password), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
        }
    }
}
