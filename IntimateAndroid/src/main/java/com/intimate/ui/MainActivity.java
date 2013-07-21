package com.intimate.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;
import com.intimate.NavigationController;
import com.intimate.R;
import com.intimate.model.Interaction;
import com.intimate.ui.fragments.InteractionImageFrag;
import com.intimate.ui.fragments.PassCheckFrag;
import com.intimate.ui.fragments.RoomFrag;
import com.intimate.ui.fragments.RoomsFrag;

public class MainActivity extends FragmentActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, PassCheckFrag.newInstance()).commit();
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ContactsChooserFrag.newInstance(null)).commit();
        }

        findViewById(R.id.btn_create_interaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationController.createImageInteraction(MainActivity.this);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    public void showRooms() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                RoomsFrag.newInstance(null)).addToBackStack(RoomsFrag.TAG).commit();
    }

    public void onRoomSelected(long id) {
        Bundle args = new Bundle(1);
        args.putLong("_id", id);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                RoomFrag.newInstance(args)).addToBackStack(RoomFrag.TAG).commit();
    }

    public void showInteraction(Interaction interaction) {
        switch (interaction.getType()) {
            case Interaction.TYPE_IMAGE:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        InteractionImageFrag.newInstance(null)).addToBackStack(InteractionImageFrag.TAG).commit();
                break;

            default:
                Log.e(TAG, "Unkown intercation");
        }
    }
}
