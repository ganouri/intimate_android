package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.intimate.App;
import com.intimate.R;
import com.intimate.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.intimate.utils.Utils.getErrorString;
import static com.intimate.utils.Utils.getPayloadJson;
import static com.intimate.utils.Utils.getPayloadString;
import static com.intimate.utils.Utils.isEmpty;
import static com.intimate.utils.Utils.log;
import static com.intimate.utils.Utils.logError;
import static com.intimate.utils.Utils.validEmail;

/**
 * Created by yurii_laguta on 10/08/13.
 */
public class ContactsFrag extends Fragment {
    public static final String TAG = ContactsFrag.class.getSimpleName();
    private ListView mListView;
    private EditText mEmailET;
    private Button mBtnAddContact;
    private ContactsAdapter mAdapter;

    public static ContactsFrag newInstance(Bundle args) {
        ContactsFrag f = new ContactsFrag();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        //TODO init from args
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_contacts, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        mEmailET = (EditText) view.findViewById(R.id.et_email);
        mBtnAddContact = (Button) view.findViewById(R.id.btn_add_contact);
        mBtnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmailET.getText().toString();
                boolean cancel = false;
                if (TextUtils.isEmpty(email)) {
                    mEmailET.setError(getString(R.string.error_field_required));
                    cancel = true;
                } else if (validEmail(email) == false) {
                    mEmailET.setError(getString(R.string.error_invalid_email));
                    cancel = true;
                }

                if (cancel) {
                    mEmailET.requestFocus();
                    return;
                } else {
                    v.setEnabled(false);
                    getActivity().setProgressBarIndeterminateVisibility(true);
                    App.sService.inviteUser(Prefs.getLoginToken(), email, addContactCB);
                }
            }
        });
        //TODO init views
        //TODO setListeners
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        App.sService.getContacts(Prefs.getLoginToken(), getContactsCB);
    }

    private final Callback<Response> getContactsCB = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            Log.d(TAG, "RESP: " + getPayloadJson(response));
            mAdapter = new ContactsAdapter(getPayloadJson(response));
            mListView.setAdapter(mAdapter);
            getActivity().setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            log(TAG, retrofitError);
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    };

    private final Callback<Response> addContactCB = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            final String resp = getPayloadString(response);
            if (isEmpty(resp) == false) {
                Log.d(TAG, "RESP: " + resp);
                App.sService.getContacts(Prefs.getLoginToken(), getContactsCB);
                mEmailET.setText("");
                mBtnAddContact.setEnabled(true);
            } else {
                logError(TAG, response);
                mEmailET.setError(getErrorString(response));
                mEmailET.requestFocus();
                mBtnAddContact.setEnabled(true);
            }
            getActivity().setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            log(TAG, retrofitError);
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    };

    class ContactsAdapter extends BaseAdapter {

        private final JSONObject mData;
        private JSONObject mItem;
        private JSONArray mKeys;

        ContactsAdapter(JSONObject data) {
            mData = data;
            mKeys = mData.names();
        }

        @Override
        public int getCount() {
            return mData.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return mKeys.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Malformed JSON");
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                mItem = (JSONObject) mData.get(mKeys.getString(position));

                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);
                }

                ((TextView) convertView.findViewById(R.id.tv_display_name)).setText(mItem.getString("nickname"));
                ((TextView) convertView.findViewById(R.id.tv_email_address)).setText(mItem.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
}