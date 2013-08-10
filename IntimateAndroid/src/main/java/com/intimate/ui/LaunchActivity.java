package com.intimate.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.intimate.App;
import com.intimate.NavigationController;
import com.intimate.R;
import com.intimate.interfaces.AuthController;
import com.intimate.ui.fragments.LoginFrag;
import com.intimate.ui.fragments.SignUpFrag;
import com.intimate.ui.fragments.WelcomeFrag;
import com.intimate.utils.Prefs;
import com.intimate.utils.Utils;

import org.json.JSONObject;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LaunchActivity extends FragmentActivity implements AuthController {

    public static final String TAG = LaunchActivity.class.getSimpleName();
    private View mAuthFormView;
    private View mAuthStatusView;
    private TextView mStatusMessageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        if (Prefs.isLogged()) {
            App.setToken(Prefs.getLoginToken());
            NavigationController.goToMainActivity(this);
            finish();
        }

        mAuthFormView = findViewById(R.id.frag_container);
        mAuthStatusView = findViewById(R.id.auth_status);
        mStatusMessageTV = (TextView) findViewById(R.id.auth_status_message);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag_container, WelcomeFrag.newInstance(null)).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.login, menu);
        return true;
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

            mAuthStatusView.setVisibility(View.VISIBLE);
            mAuthStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAuthStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mAuthFormView.setVisibility(View.VISIBLE);
            mAuthFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAuthFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mAuthStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAuthFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private static void log(String s) {
        Log.d(TAG, "ERROR_ " + s);
    }

    @Override
    public void attemptSignUp(String nickname, final String email, final String password) {
        mStatusMessageTV.setText(R.string.login_progress_signing_up);
        showProgress(true);
        App.sService.signup(nickname, email, password, new Callback<Response>() {
            @Override
            public void success(Response o, Response response) {
                final JSONObject payload = Utils.getPayloadJson(o);
                if (payload != null) {
                    attemptLogin(email, password);
                } else {
                    Utils.toastError(LaunchActivity.this, o);
                    Utils.logError(TAG, o);
                    showProgress(false);
                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Error: " + retrofitError.getMessage());
                showProgress(false);
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();

            }
        });
    }

    /**
     * Attempts to register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    public void attemptLogin(final String email, String password) {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        mStatusMessageTV.setText(R.string.login_progress_verifying_login);
        showProgress(true);
        App.sService.login(Utils.authHash(email, password), email, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                String loginToken = Utils.getPayloadString(response2);
                if (Utils.isEmpty(loginToken) == false) {
                    Prefs.setEmail(email);
                    Prefs.setLoginToken(Utils.getPayloadString(response2));
                    Prefs.setLogged(true);
                    NavigationController.goToMainActivity(LaunchActivity.this);
                    finish();
                } else {
                    final String error = Utils.getErrorString(response);
                    try {
                        Utils.showToast(LaunchActivity.this, Utils.getErrorJson(response).toString());
                        showProgress(false);
                        log(Utils.convertStreamToString(response2.getBody().in()));
                    } catch (IOException e) {
                        log(e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                log(retrofitError.getLocalizedMessage());
            }
        });
    }

    @Override
    public void showSignUp() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, SignUpFrag.newInstance(null)).commit();
    }

    @Override
    public void showLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_container, LoginFrag.newInstance(null)).commit();
    }
}
