package com.intimate.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.intimate.R;
import com.intimate.ui.fragments.PassCheckFrag;
import com.intimate.ui.fragments.RoomFrag;
import com.intimate.ui.fragments.RoomsFrag;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, PassCheckFrag.newInstance()).commit();
//        startActivity(new Intent(this, LoginActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void showRooms() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, RoomsFrag.newInstance(null))
        .commit();
    }

    public void onRoomSelected(long id) {
        Bundle args = new Bundle(1);
        args.putLong("_id", id);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, RoomFrag.newInstance(args)).commit();
    }
}
