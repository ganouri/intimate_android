package com.intimate.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidzeitgeist.mustache.fragment.CameraFragment;
import com.androidzeitgeist.mustache.listener.CameraFragmentListener;
import com.intimate.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoTakerActivity extends FragmentActivity implements CameraFragmentListener {

    private static final String TAG = PhotoTakerActivity.class.getSimpleName();
    private static final int PICTURE_QUALITY = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
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

        CameraFragment fragment = (CameraFragment) getSupportFragmentManager().findFragmentById(
                R.id.camera_fragment
        );

        fragment.takePicture();
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
        Toast.makeText(this, "bitmap size:" + bitmap.getByteCount(),1).show();
//        Intent intent = new Intent(this, PhotoTakerActivity.class);
//        intent.setData(Uri.fromFile(mediaFile));
//        startActivity(intent);

        finish();
    }

    private void showSavingPictureErrorToast() {
        Toast.makeText(this, getText(R.string.toast_error_save_picture), Toast.LENGTH_SHORT).show();
    }
    
}
