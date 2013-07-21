package com.intimate.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.intimate.R;
import com.squareup.picasso.Picasso;

import java.lang.String;

/** Created by yurii_laguta on 20/07/13. */
public class ContactsChooserFrag extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = ContactsChooserFrag.class.getSimpleName();
    private ListView mListView;
    private ContactsCursorAdapter mAdapter;

    public static ContactsChooserFrag newInstance(Bundle args) {
        ContactsChooserFrag f = new ContactsChooserFrag();
        f.setArguments(args);
        return f;
    }

    private ContactsChooserFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        //TODO init from args
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_contacts_chooser, container, false);
        mListView = (ListView) root.findViewById(R.id.list);
        mListView.setEmptyView(root.findViewById(R.id.tv_empty));
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ContactsCursorAdapter(getActivity(), null, false);
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(R.id.loader_cursor, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri contactUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;

        String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
//                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.PHOTO,
        };
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";

        return new CursorLoader(getActivity(), contactUri, PROJECTION, filter, null, order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private class ContactsCursorAdapter extends CursorAdapter {
        public ContactsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final View view = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false);
            ContactHolder holder = new ContactHolder();
            holder.profilePhoto = (ImageView) view.findViewById(R.id.iv_photo);
            holder.displayName = (TextView) view.findViewById(R.id.tv_display_name);
            holder.email = (TextView) view.findViewById(R.id.tv_email_address);
            view.setTag(R.id.holder, holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ContactHolder holder = (ContactHolder) view.getTag(R.id.holder);
            String photoURI = cursor.getString(2);
            holder.profilePhoto.setImageDrawable(null);
            if (photoURI != null) {
                Picasso.with(context).load(photoURI).into(holder.profilePhoto);
            } else {
                Picasso.with(context).load(R.drawable.ic_launcher).into(holder.profilePhoto);
            }
            holder.displayName.setText(cursor.getString(1));
            holder.email.setText(cursor.getString(3));
        }

        class ContactHolder {
            TextView displayName;
            TextView email;
            ImageView profilePhoto;
        }
    }
}