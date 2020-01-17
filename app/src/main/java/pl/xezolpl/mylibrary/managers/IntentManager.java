package pl.xezolpl.mylibrary.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public abstract class IntentManager {

    public static final int PICK_CAMERA_CODE = 300;
    public static final int PICK_GALLERY_CODE = 301;

    public static Uri setUpOutputUri(Activity activity){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image");
        return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static void pickCamera(Activity activity, Uri outputUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("android.intent.extra.quickCapture", true);
        activity.startActivityForResult(intent, PICK_CAMERA_CODE);
    }

    public static void pickCropper(Activity activity, Uri outputUri, int minWidth, int minHeight) {
        CropImage.activity(outputUri)
                .setOutputUri(outputUri)
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setMinCropWindowSize(minWidth, minHeight)
                .start(activity);
    }

    public static void pickGallery(Activity activity){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, PICK_GALLERY_CODE);
    }

    public static String getRealPath(Activity activity, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = activity.getContentResolver().query(uri,
                filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }
}
