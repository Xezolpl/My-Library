package pl.xezolpl.mylibrary.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class TextRecognitionHelper {
    private Activity activity;

    public static final int CAMERA_REQUEST = 100;

    public static final int PICK_CAMERA_CODE = 300;

    private String[] cameraPermission;

    private Uri imgUri;

    public TextRecognitionHelper(Activity activity) {
        this.activity = activity;
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public void pickCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImgUri());
        intent.putExtra("android.intent.extra.quickCapture",true);
        activity.startActivityForResult(intent, PICK_CAMERA_CODE);
    }

    public void setUpImgUri(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");
        imgUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public Uri getImgUri(){return imgUri;}

    public void requestCameraPermission(){
        ActivityCompat.requestPermissions(activity, cameraPermission, CAMERA_REQUEST);
    }

    public boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(activity, cameraPermission[0]) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(activity, cameraPermission[1]) == PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }

}
