package com.intimate.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.analytics.tracking.android.EasyTracker;
import com.intimate.App;
import com.intimate.Extra;
import com.intimate.R;
import com.intimate.model.Interaction;
import com.intimate.model.Store;
import com.intimate.ui.fragments.ContactsFrag;
import com.intimate.ui.fragments.InteractionImageFrag;
import com.intimate.ui.fragments.RoomFrag;
import com.intimate.ui.fragments.RoomsFrag;
import com.intimate.utils.Prefs;
import com.intimate.utils.RequestCode;
import com.intimate.utils.Utils;

import org.json.JSONArray;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends FragmentActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Callback<Response> mGetResCallback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            Utils.log(TAG, response);
            final JSONArray resp = Utils.getPayloadJsonArray(response);
            if(resp != null) {
                Store.getInstance().setResources(resp);
            } else {
                Utils.logError(TAG, response);
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Utils.log(TAG, retrofitError);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        overridePendingTransition(0,0);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, PassCheckFrag.newInstance()).commit();
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ContactsChooserFrag.newInstance(null)).commit();
        }



        showRooms();
        findViewById(R.id.btn_create_interaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent("photo_inter", null, MainActivity.this, CreateInteractionActivity.class), RequestCode.CREATE_INTERACTION);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_test:
            App.sService.getRoomId(Prefs.getLoginToken(), "tyurii.laguta@gmail.com:yurii.laguta@gmail.com", new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    final String room = Utils.getPayloadString(response2);
                    Log.d(TAG, "Success: room" + room);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e(TAG, retrofitError.getMessage());
                }
            });
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_logout:
                Prefs.setLogged(false);
                Prefs.setLoginToken("");
                startActivity(new Intent(this, LaunchActivity.class));
                finish();
                return true;

            case R.id.action_contacts:
                showContacts();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showContacts() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ContactsFrag.newInstance(null), ContactsFrag.TAG).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
        App.sService.getResources(App.getToken(), mGetResCallback);
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

    public void onRoomSelected(String id) {
        Bundle args = new Bundle(1);
        args.putString("_id", id);
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
                Log.e(TAG, "Unknown interaction");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode.CREATE_INTERACTION){
            switch (resultCode){
                case RESULT_OK:
                    if(data != null){
                        final String roomId = data.getStringExtra(Extra.ROOM_ID);
                        onRoomSelected(roomId);
                    }
                    break;
            }
        }
    }
}
