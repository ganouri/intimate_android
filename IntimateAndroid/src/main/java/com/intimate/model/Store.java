package com.intimate.model;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Objects;
import com.intimate.App;
import com.intimate.BusProvider;
import com.intimate.utils.Utils;
import com.squareup.otto.Produce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Memory cache
 *
 * Created by yurii_laguta on 10/08/13.
 */
public class Store {
    private static Store ourInstance = new Store();
    public static final String TAG = Store.class.getSimpleName();
    private JSONObject mContacts;
    private JSONArray mResources;
    private JSONArray mRooms;

    private HashMap<String, JSONObject> mResourcesMap;
    private HashMap<String, JSONObject> mRoomMap;

    public static Store getInstance() {
        return ourInstance;
    }

    private Store() {
//        App.sService.getResources(App.getToken(), mGetResCallback);
//        App.sService.getContacts(App.getToken(), mGetContactsCallback);
//        App.sService.getRooms(App.getToken(), mGetRoomsCallback);
    }

    private Callback<Response> mGetResCallback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            Utils.log(TAG, response);
            final JSONArray resp = Utils.getPayloadJsonArray(response);
            if (resp != null) {
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

    private Callback<Response> mGetContactsCallback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            Utils.log(TAG, response);
            final JSONObject resp = Utils.getPayloadJson(response);
            if (resp != null) {
                Store.getInstance().setContacts(resp);
            } else {
                Utils.logError(TAG, response);
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Utils.log(TAG, retrofitError);
        }
    };

    private Callback<Response> mGetRoomsCallback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            Utils.log(TAG, response);
            final JSONArray resp = Utils.getPayloadJsonArray(response);
            if (resp != null) {
                Store.getInstance().setRooms(resp);
            } else {
                Utils.logError(TAG, response);
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Utils.log(TAG, retrofitError);
        }
    };

    @Produce
    public Store produceAnswer() {
        if(null != mContacts &&  null != mResourcesMap && null != mRoomMap)
            return this;
        return null;
    }

    public JSONArray getResources() {
        return mResources;
    }

    public void setResources(JSONArray resources) {
        JSONObject obj;
        this.mResources = resources;
        mResourcesMap = new HashMap<String, JSONObject>(mResources.length());
        for(int i = 0; i<mResources.length(); ++i){
            try {
                obj = mResources.getJSONObject(i);
                mResourcesMap.put(obj.getString("_id"), obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject getResource(String id){
        return mResourcesMap.get(id);
    }

    public void setContacts(JSONObject mContacts) {
        this.mContacts = mContacts;
    }

    public JSONObject getContacts() {
        return mContacts;
    }

    public JSONObject getContact(String id){
        return mContacts.optJSONObject(id);
    }

    public void setRooms(JSONArray mRooms) {
        this.mRooms = mRooms;
        mRoomMap = new HashMap<String, JSONObject>();
        JSONObject obj;
        for(int i = 0; i<mRooms.length(); ++i){
            try {
                obj = mRooms.getJSONObject(i);
                mRoomMap.put(obj.getString("_id"), obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONArray getRooms() {
        Log.d(TAG, "getRooms:" + mRooms);
        return mRooms;
    }

    public JSONObject getRoom(String id){
        return mRoomMap.get(id);
    }

    class SyncTask extends AsyncTask <Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            selfUpdate();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            BusProvider.getInstance().post(Store.this);
        }
    }

    public void startSyncTask(){
        new SyncTask().execute((Void)null);
    }

    public void selfUpdate(){
        final Response resources = App.sService.getResources(App.getToken());
        handleResources(resources);
        final Response contacts = App.sService.getContacts(App.getToken());
        handleContacts(contacts);
        final Response rooms = App.sService.getRooms(App.getToken());
        handleRooms(rooms);
    }

    private void handleResources(Response response){
        Utils.log(TAG, response);
        final JSONArray resp = Utils.getPayloadJsonArray(response);
        if (resp != null) {
            Store.this.setResources(resp);
        } else {
            Utils.logError(TAG, response);
        }
    }

    private void handleContacts(Response response){
        Utils.log(TAG, response);
        final JSONObject resp = Utils.getPayloadJson(response);
        if (resp != null) {
            Store.getInstance().setContacts(resp);
        } else {
            Utils.logError(TAG, response);
        }
    }

    private void handleRooms(Response response) {
        Utils.log(TAG, response);
        final JSONArray resp = Utils.getPayloadJsonArray(response);
        if (resp != null) {
            Store.getInstance().setRooms(resp);
        } else {
            Utils.logError(TAG, response);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("mContacts", mContacts)
                .add("mResources", mResources)
                .add("mRooms", mRooms)
                .add("mResourcesMap", mResourcesMap)
                .add("mRoomMap", mRoomMap)
                .add("mGetResCallback", mGetResCallback)
                .add("mGetContactsCallback", mGetContactsCallback)
                .add("mGetRoomsCallback", mGetRoomsCallback)
                .toString();
    }
}
