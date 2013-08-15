package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.intimate.App;
import com.intimate.R;
import com.intimate.model.Interaction;
import com.intimate.model.Store;
import com.intimate.ui.MainActivity;
import com.intimate.utils.Prefs;
import com.intimate.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.provider.BaseColumns._ID;
import static com.intimate.utils.Utils.getPayloadJson;
import static com.intimate.utils.Utils.log;
import static com.intimate.utils.Utils.logError;

/**
 * Created by yurii_laguta on 20/07/13.
 */
public class RoomFrag extends Fragment {

    public static final String TAG = RoomFrag.class.getSimpleName();
    private JSONArray mData;
    private String mRoomId;
    private ListView mListView;
    private RoomAdapter mAdapter;
    private AdapterView.OnItemClickListener mListItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ((MainActivity) getActivity()).showInteraction(new Interaction(Interaction.TYPE_IMAGE));
        }
    };
    private Bundle mArgs;

    public static RoomFrag newInstance(Bundle args) {
        RoomFrag fragment = new RoomFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs = getArguments();
        mRoomId = getArguments().getString(_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //TODO separate layouts for rooms and room.
        View root = inflater.inflate(R.layout.frag_rooms, container, false);
        mListView = (ListView) root.findViewById(R.id.list);
        mListView.setEmptyView(root.findViewById(R.id.tv_empty));
        mListView.setOnItemClickListener(mListItemListener);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setProgressVisibility(true);
        App.sService.getRoom(Prefs.getLoginToken(), mArgs.getString("_id"), getRoomCallback);
    }

    Callback<Response> getRoomCallback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            final JSONObject resp = getPayloadJson(response);
            if (resp != null) {
                Log.d(TAG, "RESP: " + resp);
                JSONObject corr = null;
                try {
                    corr = resp.getJSONObject("corr");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (corr != null) {
                    mAdapter = new RoomAdapter(corr);
                    mListView.setAdapter(mAdapter);
                }
            } else {
                logError(TAG, response);
            }
            setProgressVisibility(false);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            log(TAG, retrofitError);
            setProgressVisibility(false);
        }
    };

    private void setProgressVisibility(boolean visible) {
        if(isResumed()){
            getActivity().setProgressBarIndeterminateVisibility(visible);
        }
    }

    private class RoomAdapter extends BaseAdapter {
        private static final String TYPE_IMAGE = "image";
        private static final String TYPE_TEXT = "text";
        private final JSONObject mSource;
        private final JSONArray mKeys;
        private JSONObject mMsgObj;
        private JSONObject mResObj;

        private RoomAdapter(JSONObject data) {
            mSource = data;
            mKeys = data.names();
        }

        @Override
        public int getCount() {
            return mKeys != null ? mKeys.length() : 0;
        }

        @Override
        public Object getItem(int position) {
            try {
                return mSource.get(mKeys.getString(position));
            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        //        "corr":
//            {"1375948416023":
//                {"lastUpdated":1375948416023,
//                 "user":"a1190110-ffff-11e2-959b-e3c7d4a5201e",
//                 "resourceId":"a12a6630-ffff-11e2-959b-e3c7d4a5201e",
//                 "associatedOn":1375948416023},
//            "1375948415962":
//                {"viewed":"true",
//                 "lastUpdated":1375948416031,
//                 "resourceId":"a120a230-ffff-11e2-959b-e3c7d4a5201e",
//                 "user":"a1190110-ffff-11e2-959b-e3c7d4a5201e",
//                 "associatedOn":1375948415962}},
//        "_id":"b0160e7873a3f404e02057a3289154b1",
//        "members":["a1190110-ffff-11e2-959b-e3c7d4a5201e","a119eb70-ffff-11e2-959b-e3c7d4a5201e"]}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            mMsgObj = (JSONObject) getItem(position);
            final String resId = mMsgObj.optString("resourceId");
            mResObj = Store.getInstance().getResource(resId);
            final String type = mResObj.optString("type");
            MsgHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_msg, parent, false);
                holder = new MsgHolder();
                holder.msgBodyTV = (TextView) convertView.findViewById(R.id.tv_msg_body);
                holder.profilePhotoIV = (ImageView) convertView.findViewById(R.id.iv_profile_photo);
                convertView.setTag(R.id.holder, holder);
            } else {
                holder = (MsgHolder) convertView.getTag(R.id.holder);
            }

            if (TYPE_IMAGE.equals(type)) {
                final String mediaId = mResObj.optString("mediaId");
                final String mediaURL = Utils.getMediaURL(mRoomId, resId, mediaId);
                Log.d(TAG, "MEDIA_URL" + mediaURL);
                Picasso.with(getActivity()).load(mediaURL).into(holder.profilePhotoIV);
                holder.profilePhotoIV.setImageDrawable(null);

            } else if (TYPE_TEXT.equals(type)) {
                holder.msgBodyTV.setText(mResObj.optString("message"));
                holder.profilePhotoIV.setImageDrawable(null);

            }
//                Picasso.with(getActivity()).load(mMsgObj.optString("picture")).into(holder.profilePhotoIV);

            return convertView;

        }

        class MsgHolder {
            TextView msgBodyTV;
            ImageView profilePhotoIV;
        }
    }
}
