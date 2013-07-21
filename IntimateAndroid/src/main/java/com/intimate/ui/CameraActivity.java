package com.intimate.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidzeitgeist.mustache.fragment.CameraFragment;
import com.androidzeitgeist.mustache.listener.CameraFragmentListener;
import com.intimate.R;
import com.intimate.ui.fragments.ContactsChooserFrag;
import com.intimate.ui.view.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.GONE;

public class CameraActivity extends FragmentActivity implements CameraFragmentListener {

    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int PICTURE_QUALITY = 90;
    private CameraFragment mCamFragment;
    private ImageButton mBtnTakePhoto;
    private Button mBtnTakeAnotherShoot;
    private Button mBtnSelectContact;
    private FrameLayout mFrameContainer;
    private TouchImageView mPhotoView;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_select_contact:
                    showSelectContact();
                    break;

                case R.id.btn_take_another_shoot:
                    showCameraFrag();
                    break;
            }
            v.setEnabled(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new CameraFragment()).commit();

        mFrameContainer = (FrameLayout) findViewById(R.id.fragment_container);
        mBtnTakePhoto =(ImageButton) findViewById(R.id.btn_take_photo);
        mBtnSelectContact = (Button) findViewById(R.id.btn_select_contact);
        mBtnTakeAnotherShoot = (Button) findViewById(R.id.btn_take_another_shoot);

        mBtnSelectContact.setOnClickListener(onClickListener);
        mBtnTakeAnotherShoot.setOnClickListener(onClickListener);

        mPhotoView = new TouchImageView(this);
        mPhotoView.setVisibility(GONE);
        mFrameContainer.addView(mPhotoView);
    }


    /**
     * On fragment notifying about a non-recoverable problem with the camera.
     */
    @Override
    public void onCameraError() {
        Toast.makeText(
                this,
                getString(R.string.toast_error_camera_preview),
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    /**
     * The user wants to take a picture.
     *
     * @param view
     */
    public void takePicture(View view) {
        view.setEnabled(false);

        mCamFragment = (CameraFragment) getSupportFragmentManager().findFragmentById(
                R.id.fragment_container
        );

        mCamFragment.takePicture();
    }

    /**
     * A picture has been taken.
     */
    public void onPictureTaken(Bitmap bitmap) {
        File mediaStorageDir = new File(
                getCacheDir(),
                Environment.DIRECTORY_PICTURES
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                showSavingPictureErrorToast();
                return;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(
                mediaStorageDir.getPath() + File.separator + "KOKOON_"+ timeStamp + ".jpg"
        );

        try {
            FileOutputStream stream = new FileOutputStream(mediaFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, PICTURE_QUALITY, stream);
        } catch (IOException exception) {
            showSavingPictureErrorToast();

            Log.w(TAG, "IOException during saving bitmap", exception);
            return;
        }

//        MediaScannerConnection.scanFile(
//                this,
//                new String[]{mediaFile.toString()},
//                new String[]{"image/jpeg"},
//                null
//        );

        showPicture(mediaFile);
        setOptionButtonsVisibility(true);
    }

    private void showPicture(File mediaFile) {
        getSupportFragmentManager().beginTransaction().remove(mCamFragment).commit();
        mPhotoView.setVisibility(View.VISIBLE);
        Picasso.with(this).load(mediaFile).into(mPhotoView);

    }

    private void showCameraFrag(){
        mPhotoView.setVisibility(GONE);
        mPhotoView.setImageDrawable(null);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new CameraFragment()).commit();
        setOptionButtonsVisibility(false);
        mBtnTakePhoto.setEnabled(true);
    }

    private void showSelectContact(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ContactsChooserFrag.newInstance(null)).commit();
        mPhotoView.setVisibility(GONE);
        mPhotoView.setImageDrawable(null);
        mBtnSelectContact.setVisibility(GONE);
        mBtnTakeAnotherShoot.setVisibility(GONE);
    }

    private void setOptionButtonsVisibility(final boolean show) {
        int shortAnimTime = 3*getResources().getInteger(android.R.integer.config_shortAnimTime);

        mBtnTakePhoto.setEnabled(true);
        mBtnTakePhoto.setVisibility(View.VISIBLE);
        mBtnTakePhoto.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBtnTakePhoto.setVisibility(show ? GONE : View.VISIBLE);
                    }
                });

        mBtnSelectContact.setEnabled(true);
        mBtnSelectContact.setVisibility(View.VISIBLE);
        mBtnSelectContact.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBtnSelectContact.setVisibility(show ? View.VISIBLE : GONE);
                    }
                });

        mBtnTakeAnotherShoot.setEnabled(true);
        mBtnTakeAnotherShoot.setVisibility(View.VISIBLE);
        mBtnTakeAnotherShoot.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBtnTakeAnotherShoot.setVisibility(show ? View.VISIBLE : GONE);
                    }
                });
    }

    private void showSavingPictureErrorToast() {
        Toast.makeText(this, getText(R.string.toast_error_save_picture), Toast.LENGTH_SHORT).show();
    }
    
}
