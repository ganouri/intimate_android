package com.intimate.utils;

import com.google.analytics.tracking.android.ExceptionParser;

import static org.apache.commons.lang3.exception.ExceptionUtils.*;

/** Created by yurii_laguta on 21/07/13. */
public class AnalyticsExceptionParser implements ExceptionParser {
    /*
     * (non-Javadoc)
     * @see com.google.analytics.tracking.android.ExceptionParser#getDescription(java.lang.String, java.lang.Throwable)
     */
    public String getDescription(String p_thread, Throwable p_throwable) {
        return "Thread: " + p_thread + ", Exception: " + getStackTrace(p_throwable);
    }
}