package com.intimate.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.intimate.R;
import com.squareup.picasso.Picasso;

/** Created by yurii_laguta on 20/07/13. */
public class InteractionImageFrag extends Fragment {
    public static final String TAG = InteractionImageFrag.class.getSimpleName();
    private ImageView mImageView;

    public static InteractionImageFrag newInstance(Bundle args) {
        InteractionImageFrag fragment = new InteractionImageFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageView = (ImageView) inflater.inflate(R.layout.frag_interaction_image, container, false);
        return mImageView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Picasso.with(getActivity()).load(R.drawable.jessica_alba_01).into(mImageView);

    }
}
