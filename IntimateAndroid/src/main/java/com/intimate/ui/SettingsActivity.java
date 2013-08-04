package com.intimate.ui;

import android.preference.PreferenceActivity;

import com.intimate.R;

import java.util.List;

/**
 * Created by yurii_laguta on 3/08/13.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }
}
