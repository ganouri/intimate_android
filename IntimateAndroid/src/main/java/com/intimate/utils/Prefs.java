package com.intimate.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.intimate.App;

/** Created by yurii_laguta on 21/07/13. */
public class Prefs {

    private static Prefs instance = null;
    private static Context mCtx;
    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mEditor;

    private Prefs() {
    }

    public static Prefs getInstance() {
        if (instance == null) {
            if (instance == null) {
                instance = new Prefs();
            }
        }
        return instance;
    }

    /** Must be called with application context */
    public static void init(Context ctx){
        mCtx = ctx;
        mPrefs = mCtx.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    public static void setLogged(boolean value){
        mEditor.putBoolean("isLogged", value).apply();
    }

    public static boolean isLogged(){
        return mPrefs.getBoolean("isLogged", false);
    }

    public static void setLoginToken(String value) {
        App.setToken(value);
        mEditor.putString("loginToken", value).apply();
    }

    public static String getLoginToken(){
        return mPrefs.getString("loginToken", "");
    }

    public static void setEmail(String value) {
        mEditor.putString("email", value).apply();
    }

    public static String getEmail(){
        return mPrefs.getString("email", "");
    }
}
