package com.intimate.model;

/**
 * Created by yurii_laguta on 11/08/13.
 */
public class KoResource {
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_TEXT = "text";

    public String createdOn;
    public String mediaId;
    public long lastUpdated;
    public String _id;
    public String type;
    public String userId;

    public KoResource(){
    }
//
//    public static KoResource from(JSONObject obj){
//        App.sGson.fromJson(obj, KoResource.class);
//    }
}
