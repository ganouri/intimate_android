package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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
public class SignUpFrag extends Fragment {
    public static final String TAG = SignUpFrag.class.getSimpleName();
    private EditText mNicknameET;
    private EditText mPasswordET;
    private EditText mEmailET;
    private String mEmail;
    private String mPassword;
    private String mNickname;
    private Button mSignUpBtn;

    public static SignUpFrag newInstance(Bundle args) {
        SignUpFrag f = new SignUpFrag();
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
        final View view = inflater.inflate(R.layout.frag_signup, container, false);
        mNicknameET = (EditText) view.findViewById(R.id.et_nickname);
        mEmailET = (EditText) view.findViewById(R.id.et_email);
        mPasswordET = (EditText) view.findViewById(R.id.et_password);
        mPasswordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (validateViews()){
                        ((AuthController) getActivity()).attemptSignUp(mNickname, mEmail, mPassword);
                    };
                    return true;
                }
                return false;
            }
        });
        mSignUpBtn = (Button) view.findViewById(R.id.btn_signup);
        mSignUpBtn.setOnClickListener(mSignUpListener);

        if(BuildConfig.DEBUG){
            mNicknameET.setText("Yurii");
            mEmailET.setText("y@m.com");
            mPasswordET.setText("qwer");
        }

        final TextView mTermAndConditionTV = (TextView) view.findViewById(R.id.tv_terms_and_conditions);
        mTermAndConditionTV.setMovementMethod(LinkMovementMethod.getInstance());
        //TODO init views
        //TODO setListeners
        return view;
    }

    private View.OnClickListener mSignUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateViews()){
                ((AuthController) getActivity()).attemptSignUp(mNickname, mEmail, mPassword);
            }
        }
    };


    /**
     * @return true if fields valid
     */
    private boolean validateViews() {
        // Reset errors.
        mNicknameET.setError(null);
        mEmailET.setError(null);
        mPasswordET.setError(null);

        // Store values at the time of the login attempt.
        mNickname = mNicknameET.getText().toString();
        mEmail = mEmailET.getText().toString();
        mPassword = mPasswordET.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for valid nickname
        if (TextUtils.isEmpty(mNickname)) {
            mNicknameET.setError(getString(R.string.error_field_required));
            focusView = mNicknameET;
            cancel = true;
        }

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