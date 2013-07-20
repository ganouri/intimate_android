package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.intimate.R;
import com.intimate.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Created by yurii_laguta on 20/07/13. */
public class RoomsFrag extends Fragment {

    private JSONArray mDataSource;
    private ListView mListView;
    private RoomsAdapter mAdapter;
    private AdapterView.OnItemClickListener mRoomListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ((MainActivity)getActivity()).onRoomSelected(id);
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
