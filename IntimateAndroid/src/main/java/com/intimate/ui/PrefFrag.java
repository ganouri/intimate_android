package com.intimate.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by yurii_laguta on 3/08/13.
 */

public class PrefFrag extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int res=getActivity()
                .getResources()
                .getIdentifier(getArguments().getString("resource"),
                        "xml",
                        getActivity().getPackageName());

        addPreferencesFromResource(res);
    }
}
