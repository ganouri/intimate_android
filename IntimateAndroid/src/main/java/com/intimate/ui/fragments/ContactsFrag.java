package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.intimate.App;
import com.intimate.R;
import com.intimate.model.Store;
import com.intimate.ui.CreateInteractionActivity;
import com.intimate.ui.MainActivity;
import com.intimate.utils.Prefs;
import com.intimate.utils.Utils;

import org.json.JSONArray;
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final JSONObject itemAtPosition = (JSONObject) parent.getItemAtPosition(position);
                if(getActivity() instanceof CreateInteractionActivity){
                    ((CreateInteractionActivity) getActivity()).onContactSelected(itemAtPosition.optString("email"));
                } else if (getActivity() instanceof MainActivity){
                    Utils.showToast(getActivity(), "Show contact detail page");
                }
            }
        });
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
                    App.sService.addContact(App.getToken(), email, mAddContactCB);
                }
            }
        });
        //TODO init views
        //TODO setListeners
        return view;
    }

//    App.sService.inviteUser(Prefs.getLoginToken(), email, mAddContactCB);
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        App.sService.getContacts(Prefs.getLoginToken(), getContactsCB);
        mAdapter = new ContactsAdapter(Store.getInstance().getContacts());
        mListView.setAdapter(mAdapter);
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

    private Callback<Response> mInviteContactCB = new Callback<Response>() {
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

    private Callback<Response> mAddContactCB = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            Log.d(TAG, "Resp: " + Utils.respToString(response));
            final String resp = getPayloadString(response);
            Log.d(TAG, "RESP: " + resp);
            if (isEmpty(resp) == false) {
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
            mBtnAddContact.setEnabled(true);
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
            return mKeys != null ? mKeys.length() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mData.opt(mKeys.optString(position));
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            mItem = (JSONObject) getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.tv_display_name)).setText(mItem.optString("nickname"));
            ((TextView) convertView.findViewById(R.id.tv_email_address)).setText(mItem.optString("email"));
            return convertView;
        }
    }
}