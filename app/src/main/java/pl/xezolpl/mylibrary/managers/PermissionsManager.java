package pl.xezolpl.mylibrary.managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import pl.xezolpl.mylibrary.R;

public abstract class PermissionsManager {
    private static String[] cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] internetPermission = new String[]{Manifest.permission.INTERNET};

    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 101;
    public static final int INTERNET_REQUEST = 102;


    public static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, cameraPermission, CAMERA_REQUEST);
    }

    public static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, storagePermission, STORAGE_REQUEST);
    }

    public static void requestInternetPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, internetPermission, INTERNET_REQUEST);
    }

    public static boolean checkCameraPermission(Activity activity) {
        boolean result1 = ContextCompat.checkSelfPermission(activity, cameraPermission[0]) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(activity, cameraPermission[1]) == PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }

    public static boolean checkStoragePermission(Activity activity) {
        boolean result1 = ContextCompat.checkSelfPermission(activity, storagePermission[0]) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(activity, storagePermission[1]) == PackageManager.PERMISSION_GRANTED;
        return result1 && result2;
    }

    public static boolean checkInternetPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, internetPermission[0]) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean handlePermissionsResult(Context context, int requestCode, @NonNull int[] grantResults) {
        boolean result = false;
        if (grantResults.length > 0) {
            if (requestCode == CAMERA_REQUEST) {
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                result = cameraAccepted && writeStorageAccepted;

            } else if (requestCode == STORAGE_REQUEST) {
                boolean readStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                result = readStorageAccepted && writeStorageAccepted;

            } else if (requestCode == INTERNET_REQUEST) {
                result = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            }
        }
        if (!result){
            Toast.makeText(context, context.getString(R.string.need_permissions), Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
