package pl.xezolpl.mylibrary.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.CoversRevViewAdapter;
import pl.xezolpl.mylibrary.managers.IntentManager;
import pl.xezolpl.mylibrary.managers.PermissionsManager;
import pl.xezolpl.mylibrary.managers.SettingsManager;
import pl.xezolpl.mylibrary.utilities.FetchBook;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static pl.xezolpl.mylibrary.managers.IntentManager.PICK_CAMERA_CODE;
import static pl.xezolpl.mylibrary.managers.IntentManager.PICK_GALLERY_CODE;

public class SelectCoverActivity extends AppCompatActivity {
    private static final String TAG = "SelectCoverActivity";

    private EditText bookInput;
    private FitButton refreshBtn, moreCoversBtn, galleryBtn, cameraBtn;

    private CoversRevViewAdapter adapter;
    private RecyclerView recView;

    private int coversToSearch = 10;
    private String searchString = "";
    private Uri imgUri = null;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new SettingsManager(this).loadTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_cover);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initWidgets();
        setOnClickListeners();

        adapter = new CoversRevViewAdapter(this);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void initWidgets() {
        recView = findViewById(R.id.book_covers_rec_view);
        bookInput = findViewById(R.id.bookInput);

        galleryBtn = findViewById(R.id.galleryBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        refreshBtn = findViewById(R.id.refreshBtn);
        moreCoversBtn = findViewById(R.id.moreCoversBtn);
        moreCoversBtn.setVisibility(View.GONE);
    }

    private void setOnClickListeners() {
        galleryBtn.setOnClickListener(view -> { // pick from gallery
            try {
                if (!PermissionsManager.checkStoragePermission(this)) {
                    PermissionsManager.requestStoragePermission(this);
                } else {
                    IntentManager.pickGallery(this);
                }
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "setOnClickListeners: ", e);
            }
        });

        cameraBtn.setOnClickListener(view -> { //pick camera
            try {
                if (!PermissionsManager.checkCameraPermission(this)) {
                    PermissionsManager.requestCameraPermission(this);
                } else {
                    imgUri = IntentManager.setUpImageOutputUri(this);
                    IntentManager.pickCamera(this, imgUri);
                }
            } catch (Exception exc) {
                Toast.makeText(this, getString(R.string.recognition_error), Toast.LENGTH_LONG).show();
                Log.e(TAG, "setOnClickListeners: camera", exc);
            }
        });

        refreshBtn.setOnClickListener(view -> { // search for covers
            if (!PermissionsManager.checkInternetPermission(this)) {
                PermissionsManager.requestInternetPermission(this);

            } else if (bookInput != null && bookInput.getText().length() > 0) {
                searchString = bookInput.getText().toString();
                coversToSearch = 10;
                searchBooks(searchString);
            }
        });

        moreCoversBtn.setOnClickListener(view -> { // show more covers
            if (!searchString.isEmpty()) {
                coversToSearch += 10;
                searchBooks(searchString);
            }
        });
    }

    public void setMoreCoversBtnVisible(boolean b) {
        moreCoversBtn.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    /**
     * Searches for the covers by FetchBook AsyncTask
     *
     * @param search specified search String (may be e.g. author, title isbn or anything ;)
     */
    public void searchBooks(String search) {
        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
        if (networkInfo != null && networkInfo.isConnected()) {
            new FetchBook(adapter, this, coversToSearch).execute(search);
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Creates a bitmap from uri next scales it into the cover's format (280px-385pix)
     * then creates a file from bitmap and saves it into the application's directory/files/covers
     *
     * @param bitmap scaled bitmap with image
     * @return successfully created file's path in app directory, if there was an error - returns null
     */
    public Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / oldWidth;
        float scaleHeight = ((float) newHeight) / oldHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, matrix, true);
    }

    public String createCoverFileInAppDirectory(@NotNull Bitmap bitmap) {

        String createdFilePath = null;
        try {
            //compress to JPEG
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

            //check do files and covers folders exist
            String newFilePath = getApplicationInfo().dataDir;

            File filesDir = new File(newFilePath + "/files");
            if (!filesDir.exists()) {
                if (!filesDir.mkdir()) {
                    return null;
                }
            }

            File coversDir = new File(newFilePath + "/files/covers");
            if (!coversDir.exists()) {
                if (!coversDir.mkdir()) {
                    return null;
                }
            }

            //create file's path in app directory
            newFilePath += "/files/covers/" + UUID.randomUUID().toString() + ".jpg";

            //create file
            File f = new File(newFilePath);
            if (f.createNewFile()) {
                createdFilePath = newFilePath;
                //write the bytes into the file
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();
            }


            // remember close the streams
            bytes.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return createdFilePath;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_CAMERA_CODE) {
                IntentManager.pickCropper(this, imgUri, 200, 200);

            } else if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == PICK_GALLERY_CODE) {
                Bitmap imageBitmap = null;
                try {

                    //From cropper
                    if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE && imgUri != null) {
                        //Get real file uri
                        Uri realUri = Uri.parse(IntentManager.getRealPath(this, imgUri));

                        //Create buffers to convert uri to bitmap
                        FileInputStream in = new FileInputStream(realUri.toString());
                        BufferedInputStream buf = new BufferedInputStream(in);

                        //Create bitmap
                        imageBitmap = BitmapFactory.decodeStream(buf);

                        //Close the streams
                        in.close();
                        buf.close();

                        //Delete the original file from camera
                        new File(realUri.toString()).delete();
                    }

                    //From gallery
                    else if (requestCode == PICK_GALLERY_CODE && data != null) {

                        //Get uri
                        Uri photoUri = data.getData();

                        //create bitmap
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    }

                    //If image bitmap is successfully created
                    if (imageBitmap != null) {

                        //Scale bitmap
                        Bitmap scaledBitmap = scaleBitmap(imageBitmap, 280, 385);

                        //Create file in app directory
                        String fileUrl = createCoverFileInAppDirectory(scaledBitmap);

                        //Set the result
                        if (fileUrl != null) {
                            setResult(RESULT_OK, new Intent().putExtra("url", fileUrl));
                            finish();
                        } else {
                            Toast.makeText(this, getString(R.string.adding_cover_error),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.adding_cover_error) + "\nError: " + e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionsManager.handlePermissionsResult(this, requestCode, grantResults)) {
            //if we granted the permissions

            if (requestCode == PermissionsManager.CAMERA_REQUEST) {
                imgUri = IntentManager.setUpImageOutputUri(this);
                IntentManager.pickCamera(this, imgUri);

            } else if (requestCode == PermissionsManager.STORAGE_REQUEST) {
                IntentManager.pickGallery(this);
            }
        }
    }
}
