package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intimate.BuildConfig;
import com.intimate.R;
import com.intimate.interfaces.AuthController;

/**
 * Created by yurii_laguta on 9/08/13.
 */
public class LoginFrag extends Fragment {
    public static final String TAG = LoginFrag.class.getSimpleName();
    private EditText mPasswordET;
    private EditText mEmailET;
    private String mEmail;
    private String mPassword;
    private Button mLoginBtn;

    public static LoginFrag newInstance(Bundle args) {
        LoginFrag f = new LoginFrag();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        //TODO init from args
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_login, container, false);
        mEmailET = (EditText) view.findViewById(R.id.et_email);
        mPasswordET = (EditText) view.findViewById(R.id.et_password);
        mPasswordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (validateViews()){
                        ((AuthController) getActivity()).attemptLogin(mEmail, mPassword);
                    };
                    return true;
                }
                return false;
            }
        });

        if(BuildConfig.DEBUG){
            mEmailET.setText("y@m.com");
            mPasswordET.setText("qwer");
        }

        mLoginBtn = (Button) view.findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(mLoginListener);
        return view;
    }

    private View.OnClickListener mLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateViews()){
                ((AuthController) getActivity()).attemptLogin(mEmail, mPassword);
            }
        }
    };


    /**
     * @return true if fields valid
     */
    private boolean validateViews() {
        // Reset errors.
        mEmailET.setError(null);
        mPasswordET.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailET.getText().toString();
        mPassword = mPasswordET.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordET.setError(getString(R.string.error_field_required));
            focusView = mPasswordET;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordET.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordET;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailET.setError(getString(R.string.error_field_required));
            focusView = mEmailET;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailET.setError(getString(R.string.error_invalid_email));
            focusView = mEmailET;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        return !cancel;
    }
}