package com.intimate;

import android.app.Application;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.intimate.utils.AnalyticsExceptionParser;
import com.intimate.utils.Prefs;

/** Created by yurii_laguta on 21/07/13. */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Init preferences
        Prefs.getInstance();
        Prefs.init(this);

        //Setting up Google Analytics as proper crash reporting tool
        EasyTracker.getInstance().setContext(this);
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (uncaughtExceptionHandler instanceof ExceptionReporter) {
            ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
            exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
        }
    }
}
