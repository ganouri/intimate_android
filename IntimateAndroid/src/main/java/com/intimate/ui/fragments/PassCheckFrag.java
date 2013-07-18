package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.intimate.R;

/** Created by yurii_laguta on 18/07/13. */
public class PassCheckFrag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflate = inflater.inflate(R.layout.frag_pass_check, container, false);
        EditText pass = (EditText) inflate.findViewById(R.id.editText_pass);
        
        return inflate;
    }

    public static PassCheckFrag newInstance() {
        return new PassCheckFrag();
    }
}
