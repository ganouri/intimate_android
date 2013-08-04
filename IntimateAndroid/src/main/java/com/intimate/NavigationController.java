package com.intimate;

import android.content.Intent;

import com.intimate.ui.CreateInteractionActivity;
import com.intimate.ui.MainActivity;

/** Created by yurii_laguta on 21/07/13. */
public class NavigationController {

    public static void goToMainActivity(android.content.Context ctx){
        ctx.startActivity(new Intent(ctx, MainActivity.class));
    }

    public static void createImageInteraction(android.content.Context ctx){
        ctx.startActivity(new Intent(ctx, CreateInteractionActivity.class));
    }
}
