package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.intimate.R;
import com.intimate.ui.MainActivity;
import com.intimate.utils.Utils;

/** Created by yurii_laguta on 18/07/13. */
public class PassCheckFrag extends Fragment {

    private static final String TAG = PassCheckFrag.class.getSimpleName();
    View.OnClickListener mOnClickListiner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_enter:
                    if(isValidPass(passwordET.getText().toString())){
                        ((MainActivity)getActivity()).showRooms();
                    } else {
                        Utils.showToast(getActivity(), "Invalid Password");
                    }
                    break;

                default:
                    Log.e(TAG, "Unhandled button");
            }
        }
    };

    private boolean isValidPass(String pass) {
        return true;
    }

    private EditText passwordET;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_pass_check, container, false);
        passwordET = (EditText) view.findViewById(R.id.editText_pass);
        Button enter = (Button) view.findViewById(R.id.btn_enter);
        enter.setOnClickListener(mOnClickListiner);
        return view;
    }

    public static PassCheckFrag newInstance() {
        return new PassCheckFrag();
    }
}
