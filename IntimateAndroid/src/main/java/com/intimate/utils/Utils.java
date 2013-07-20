package com.intimate.utils;

import android.content.Context;
import android.widget.Toast;

/** Created by yurii_laguta on 20/07/13. */
public class Utils {

    public static void showToast(Context ctx, String msg) {
        //TODO replace with crouton
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
