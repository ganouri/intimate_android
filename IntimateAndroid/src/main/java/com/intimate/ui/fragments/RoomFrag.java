package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.intimate.R;
import com.intimate.model.Interaction;
import com.intimate.ui.MainActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.BaseColumns._ID;

/** Created by yurii_laguta on 20/07/13. */
public class RoomFrag  extends Fragment {

    public static final String TAG = RoomFrag.class.getSimpleName();
    private JSONArray mData;
    private String mRoomId;
    private ListView mListView;
    private RoomAdapter mAdapter;
    private AdapterView.OnItemClickListener mListItemListiner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ((MainActivity)getActivity()).showInteraction(new Interaction(Interaction.TYPE_IMAGE));
        }
    };

    public static RoomFrag newInstance(Bundle args) {
        RoomFrag fragment = new RoomFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRoomId = getArguments().getString(_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //TODO separate layouts for rooms and room.
        View root = inflater.inflate(R.layout.frag_rooms, container, false);
        mListView = (ListView) root.findViewById(R.id.list);
        mListView.setEmptyView(root.findViewById(R.id.tv_empty));
        try {
            mData = new JSONArray(getString(R.string.my_room_data));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter = new RoomAdapter(mData);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mListItemListiner);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private class RoomAdapter extends BaseAdapter {

        private final JSONArray mSource;
        private JSONObject mMsgObj;

        private RoomAdapter(JSONArray data) {
            mSource = data;
        }

        @Override
        public int getCount() {
            return mSource != null ? mSource.length() : 0;
        }

        @Override
        public Object getItem(int position) {
            try {
                return mSource.get(position);
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
            MsgHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_msg, parent, false);
                holder = new MsgHolder();
                holder.msgBodyTV = (TextView) convertView.findViewById(R.id.tv_msg_body);
                holder.profilePhotoIV = (ImageView) convertView.findViewById(R.id.iv_profile_photo);
                convertView.setTag(R.id.holder, holder);
            } else {
                holder = (MsgHolder) convertView.getTag(R.id.holder);
            }
            mMsgObj = (JSONObject) getItem(position);
            try {
                holder.msgBodyTV.setText(mMsgObj.getString("about"));
                holder.profilePhotoIV.setImageDrawable(null);
                Picasso.with(getActivity()).load(mMsgObj.getString("picture")).into(holder.profilePhotoIV);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }

        class MsgHolder {
            TextView msgBodyTV;
            ImageView profilePhotoIV;
        }
    }
}
