package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.intimate.App;
import com.intimate.R;
import com.intimate.ui.MainActivity;
import com.intimate.utils.Prefs;
import com.intimate.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created by yurii_laguta on 20/07/13. */
public class RoomsFrag extends Fragment {

    public static final String TAG =  RoomsFrag.class.getSimpleName();
    private JSONArray mDataSource;
    private ListView mListView;
    private RoomsAdapter mAdapter;
    private AdapterView.OnItemClickListener mRoomListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO get item id
            ((MainActivity)getActivity()).onRoomSelected(mListView.getItemAtPosition(position).toString());
        }
    };

    public static RoomsFrag newInstance(Bundle args) {
        RoomsFrag fragment = new RoomsFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mDataSource = new JSONObject(getActivity().getString(R.string.data_source)).getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_rooms, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        mAdapter = new RoomsAdapter(mDataSource);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mRoomListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        App.sService.getUserInfoWithRooms(Prefs.getLoginToken(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                final JSONObject payload = Utils.getPayloadJson(response);
                final String s = Utils.respToString(response);
                if(payload != null){
                    //TODO parse this Json into user with rooms
                    try {
                        final JSONArray rooms = payload.getJSONArray("rooms");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Rooms " + Utils.getPayloadString(response));

                } else {
                    Utils.toastError(getActivity(), response);
                    Utils.logError(TAG, response);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Utils.log(TAG, retrofitError);
            }
        });
    }

    class RoomsAdapter extends BaseAdapter {
        private JSONArray mData;
        private JSONObject mRoomObj;
        public RoomsAdapter(JSONArray mDataSource) {
            mData = mDataSource;
        }

        @Override
        public int getCount() {
            return mData.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return mData.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RoomHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_room, parent, false);
                holder = new RoomHolder();
                holder.roomNameTv = (TextView) convertView.findViewById(R.id.tv_room_name);
                convertView.setTag(R.id.holder, holder);
            } else {
                holder = (RoomHolder) convertView.getTag(R.id.holder);
            }
            mRoomObj = (JSONObject) getItem(position);
            try {
                holder.roomNameTv.setText(mRoomObj.getString("company"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }

        class RoomHolder {
            TextView roomNameTv;
        }
    }
}
