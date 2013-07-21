package com.intimate;

import android.content.Context;
import android.content.Intent;

import com.intimate.ui.CameraActivity;
import com.intimate.ui.MainActivity;

/** Created by yurii_laguta on 21/07/13. */
public class NavigationController {

    public static void goToMainActivity(Context ctx){
        ctx.startActivity(new Intent(ctx, MainActivity.class));
    }

    public static void createImageInteraction(Context ctx){
        ctx.startActivity(new Intent(ctx, CameraActivity.class));
    }
}
