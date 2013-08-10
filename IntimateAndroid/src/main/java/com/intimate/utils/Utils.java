package com.intimate.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created by yurii_laguta on 20/07/13. */
public class Utils {

    public static void showToast(android.content.Context ctx, String msg) {
        //TODO replace with crouton
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static String md5(String value){
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher()
                .putString(value, Charsets.UTF_8)
                .hash();
        return hc.toString();
    }

    public static String authHash(String email, String pass){
        return md5(":" + email + ":" + md5(pass) + ":");
    }

    public static String getRoomId(String[] userEmails){
        Arrays.sort(userEmails);
        return md5(Joiner.on(':').join(userEmails));
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String getPayloadString(Response resp){
        try {
            JSONObject obj = new JSONObject(convertStreamToString(resp.getBody().in()));
            return obj.getString("payload");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getPayloadJson(Response resp){
        try {
            JSONObject obj = new JSONObject(convertStreamToString(resp.getBody().in()));
            return obj.getJSONObject("payload");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getPayloadJsonArray(Response resp){
        try {
            JSONObject obj = new JSONObject(convertStreamToString(resp.getBody().in()));
            return obj.getJSONArray("payload");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //form-data; name="my_file"; filename="flower.jpg"
    public static String getContentDisposition(String name, String fileName){
        return String.format("form-data; name=\"%s\"; filename=\"%s\"", name, fileName);
    }

    public static void log(Response response) {
        try {
            Log.d("INTIMATE", convertStreamToString(response.getBody().in()));
        } catch (IOException e) {
            Log.e("LOG", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void log(String tag, RetrofitError retrofitError) {
        Log.e(tag, "RetrofitError: " +retrofitError.getLocalizedMessage());
    }

    public static JSONObject getErrorJson(Response resp) {
        try {
            JSONObject obj = new JSONObject(convertStreamToString(resp.getBody().in()));
            return obj.getJSONObject("errors");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void log(String tag, Response response) {
        try {
            Log.d(tag, convertStreamToString(response.getBody().in()));
        } catch (IOException e) {
            Log.e(tag, e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isEmpty(String string) {
        return TextUtils.isEmpty(string) || "null".equals(string);
    }


    public static String getErrorString(Response resp) {
        try {
            JSONObject obj = new JSONObject(convertStreamToString(resp.getBody().in()));
            obj = obj.getJSONObject("errors");
            if(obj.names() != null){
                final String error = obj.getString(obj.names().get(0).toString());
                return error ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Unknown Error";
    }

    public static void toastError(Context ctx, Response response) {
        showToast(ctx, "ERROR: " +getErrorString(response));
    }

    public static void logError(String tag, Response response) {
        Log.e(tag, "ERROR: " + getErrorString(response));
    }

    public static String respToString(Response response) {
        try {
            return convertStreamToString(response.getBody().in());
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    public static boolean isValid(String payload) {
        return payload != null && !payload.equals("null");
    }

    public static boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
