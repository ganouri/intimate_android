package com.intimate.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidzeitgeist.mustache.fragment.CameraFragment;
import com.androidzeitgeist.mustache.listener.CameraFragmentListener;
import com.intimate.App;
import com.intimate.Extra;
import com.intimate.R;
import com.intimate.model.KoResource;
import com.intimate.server.IntimateInterface;
import com.intimate.ui.fragments.ContactsChooserFrag;
import com.intimate.ui.fragments.ContactsFrag;
import com.intimate.ui.view.TouchImageView;
import com.intimate.utils.Prefs;
import com.intimate.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import static android.view.View.GONE;

public class CreateInteractionActivity extends FragmentActivity implements CameraFragmentListener {

    private static final String TAG = CreateInteractionActivity.class.getSimpleName();
    private static final int PICTURE_QUALITY = 90;
    private CameraFragment mCamFragment;
    private Button mBtnTakePhoto;
    private Button mBtnTakeAnotherShoot;
    private Button mBtnSelectContact;
    private FrameLayout mFrameContainer;
    private LinearLayout mBtnsContainer;
    private TouchImageView mPhotoView;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_select_contact:
                    showLocalContacts();
//                    showSelectContact();
                    break;

                case R.id.btn_take_another_shoot:
                    mPhotoView.setImageDrawable(null);
                    final boolean delete = mLastFile.delete();
                    if (!delete) {
                        Log.e(TAG, "haven't deleted file on having another shoot");
                    }
                    showCameraFrag();
                    break;
            }
            v.setEnabled(false);
        }
    };
    private String mSelectedContact;
    private Callback<Response> createResourceCallback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            final JSONObject resource = Utils.getPayloadJson(response);
            final String resourceId = resource.optString("_id");
            if(mRoomId != null){
                App.sService.associateResource(App.getToken(), mRoomId, resourceId, mAssociateResourceCallback);
            } else {
                App.sService.getRoomId(Prefs.getLoginToken(), Prefs.getEmail() + IntimateInterface.DIVIDER + mSelectedContact, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        final String roomId = Utils.getPayloadString(response);

                        if (!"null".equals(roomId)) {
                            mRoomId = roomId;
                            App.sService.associateResource(Prefs.getLoginToken(), mRoomId, resourceId, mAssociateResourceCallback);
                        } else {
                            Utils.toastError(CreateInteractionActivity.this, response);
                            Utils.logError(TAG, response);
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Utils.log(TAG, retrofitError);
                    }
                });
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Utils.log(TAG, retrofitError);
            Utils.showToast(CreateInteractionActivity.this, retrofitError.getLocalizedMessage());
        }
    };


    private void showLocalContacts() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ContactsFrag.newInstance(null)).commit();
        mPhotoView.setVisibility(GONE);
//        mPhotoView.setImageDrawable(null);
        mBtnsContainer.setVisibility(GONE);
    }

    private File mLastFile;
    private String mRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new CameraFragment()).commit();

        mFrameContainer = (FrameLayout) findViewById(R.id.fragment_container);
        mBtnsContainer = (LinearLayout) findViewById(R.id.container_btns_after_shoot);
        mBtnTakePhoto = (Button) findViewById(R.id.btn_take_photo);
        mBtnSelectContact = (Button) findViewById(R.id.btn_select_contact);
        mBtnTakeAnotherShoot = (Button) findViewById(R.id.btn_take_another_shoot);

        mBtnSelectContact.setOnClickListener(onClickListener);
        mBtnTakeAnotherShoot.setOnClickListener(onClickListener);

        mPhotoView = new TouchImageView(this);
        mPhotoView.setVisibility(GONE);
        mFrameContainer.addView(mPhotoView);

        mRoomId = getIntent() != null && getIntent().hasExtra(Extra.ROOM_ID) ? getIntent().getStringExtra(Extra.ROOM_ID) : null;
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
                mediaStorageDir.getPath() + File.separator + "KOKOON_" + timeStamp + ".jpg"
        );

        try {
            FileOutputStream stream = new FileOutputStream(mediaFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, PICTURE_QUALITY, stream);
        } catch (IOException exception) {
            showSavingPictureErrorToast();

            Log.w(TAG, "IOException during saving bitmap", exception);
            return;
        }

        showPicture(mediaFile);
        mLastFile = mediaFile;
        setOptionButtonsVisibility(true);
    }

    private void showPicture(File mediaFile) {
        getSupportFragmentManager().beginTransaction().remove(mCamFragment).commit();
        mPhotoView.setVisibility(View.VISIBLE);
        Picasso.with(this).load(mediaFile).into(mPhotoView);

    }

    private void showCameraFrag() {
        mPhotoView.setVisibility(GONE);
        mPhotoView.setImageDrawable(null);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new CameraFragment()).commit();
        setOptionButtonsVisibility(false);
        mBtnTakePhoto.setEnabled(true);
    }

    private void showSelectContact() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ContactsChooserFrag.newInstance(null)).commit();
        mPhotoView.setVisibility(GONE);
        mPhotoView.setImageDrawable(null);
        mBtnsContainer.setVisibility(GONE);
    }

    private void setOptionButtonsVisibility(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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

        mBtnsContainer.setEnabled(true);
        mBtnsContainer.setVisibility(View.VISIBLE);
        mBtnsContainer.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBtnsContainer.setVisibility(show ? View.VISIBLE : GONE);
                    }
                });
    }

    private void showSavingPictureErrorToast() {
        Toast.makeText(this, getText(R.string.toast_error_save_picture), Toast.LENGTH_SHORT).show();
    }

    //Called after we have a picture and selected a contact:
    // first uploadResource
    // second getRoomid if don't have from service
    // third associate resource
    public void onContactSelected(final String email) {
        Log.d(TAG, "onContactSelected " + email);
        mSelectedContact = email;
        App.sService.addMedia(Prefs.getLoginToken(), Utils.getContentDisposition(mLastFile.getName(), mLastFile.getName()), "image/jpeg", "base64", new TypedFile("image/jpeg", mLastFile), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Utils.log(response2);
                // TODO: Check for error

                final String mediaId = Utils.getPayloadString(response2);
                if (!Utils.isEmpty(mediaId)) {
                    App.sService.createResource(App.getToken(), KoResource.TYPE_IMAGE, mediaId, createResourceCallback);
                } else {
                    Utils.toastError(CreateInteractionActivity.this, response);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Utils.log(TAG, retrofitError);

            }
        });
    }

    final Callback<Response> mAssociateResourceCallback = new Callback<Response>() {
        @Override
        public void success(Response response, Response response2) {
            Utils.log(TAG, response);
            final String payload = Utils.getPayloadString(response);
            if (Utils.isValid(payload)) {
                //TODO open this room with msg and state pending
                Intent data = getIntent();
                data.putExtra(Extra.ROOM_ID, mRoomId);
                setResult(RESULT_OK, data);
                Log.d(TAG, "call Finish()");
                finish();
                return;
            } else {
                Utils.toastError(CreateInteractionActivity.this, response2);
                Utils.logError(TAG, response);
            }
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            Utils.log(TAG, retrofitError);
        }
    };

}
