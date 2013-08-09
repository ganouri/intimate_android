package com.intimate.interfaces;

/**
 * Created by yurii_laguta on 9/08/13.
 */
public interface AuthController {
    void attemptSignUp(String mNickname, String mEmail, String mPassword);

    void attemptLogin(String mEmail, String mPassword);

    void showSignUp();

    void showLogin();
}
