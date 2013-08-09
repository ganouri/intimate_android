package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intimate.R;
import com.intimate.interfaces.AuthController;

/**
 * Created by yurii_laguta on 9/08/13.
 */
public class WelcomeFrag extends Fragment {
    public static final String TAG = WelcomeFrag.class.getSimpleName();
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_sign_up:
                    ((AuthController)getActivity()).showSignUp();
                    break;

                case R.id.btn_login:
                    ((AuthController)getActivity()).showLogin();
                    break;
            }
        }
    };

    public static WelcomeFrag newInstance(Bundle args) {
        WelcomeFrag f = new WelcomeFrag();
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
        final View view = inflater.inflate(R.layout.frag_welcome, container, false);
        view.findViewById(R.id.btn_sign_up).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.btn_login).setOnClickListener(mOnClickListener);
        //TODO init views
        //TODO setListeners
        return view;
    }
}