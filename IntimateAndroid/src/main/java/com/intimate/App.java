package com.intimate;

import android.app.Application;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.gson.Gson;
import com.intimate.server.IntimateInterface;
import com.intimate.utils.AnalyticsExceptionParser;
import com.intimate.utils.Prefs;

import retrofit.RestAdapter;

/** Created by yurii_laguta on 21/07/13. */
public class App extends Application {

    private static RestAdapter restAdapter;
    public static IntimateInterface sService;
    private static String msToken;
    public static Gson sGson;
    private static String id;

    public static String getToken() {
        return msToken;
    }

    public static void setToken(String token) {
        App.msToken = token;
    }

    public static void setId(String id) {
        App.id = id;
    }

    public static String getId() {
        return id;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Init Prefs class
        Prefs.getInstance();
        Prefs.init(this);

        //Setting up Google Analytics as proper crash reporting tool
        EasyTracker.getInstance().setContext(this);
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (uncaughtExceptionHandler instanceof ExceptionReporter) {
            ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
            exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
        }

        restAdapter = new RestAdapter.Builder()
                .setServer(IntimateInterface.BASE_URL)
                .build();
        sService = restAdapter.create(IntimateInterface.class);
    }
}
