package com.intimate.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Memory cache
 *
 * Created by yurii_laguta on 10/08/13.
 */
public class Store {
    private static Store ourInstance = new Store();
    private JSONObject contacts;

    public static Store getInstance() {
        return ourInstance;
    }

    private Store() {
    }

    private HashMap<String, JSONObject> mResourcesMap;

    private JSONArray mResources;

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

    public void setContacts(JSONObject contacts) {
        this.contacts = contacts;
    }

    public JSONObject getContacts() {
        return contacts;
    }

    public JSONObject getContact(String id){
        return contacts.optJSONObject(id);
    }
}
