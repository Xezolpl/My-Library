package pl.xezolpl.mylibrary.activities;

import android.accounts.NetworkErrorException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikartm.button.FitButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import pl.xezolpl.mylibrary.FetchBook;
import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.CoversRevViewAdapter;

import static pl.xezolpl.mylibrary.utilities.TextRecognitionHelper.PICK_CAMERA_CODE;
import static pl.xezolpl.mylibrary.utilities.TextRecognitionHelper.PICK_GALLERY_CODE;

public class SelectCoverActivity extends AppCompatActivity {
    private static final String TAG = "SelectCoverActivity";

    private MaterialEditText bookInput;
    private FitButton refreshBtn, moreCoversBtn, galleryBtn, cameraBtn;

    private CoversRevViewAdapter adapter;
    private RecyclerView recView;

    private int coversToSearch = 10;
    private String searchString = "";
    private Uri imgUri = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cover);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        initWidgets();
        setOnClickListeners();

        adapter = new CoversRevViewAdapter(this);
        recView.setLayoutManager(new GridLayoutManager(this, 2));
        recView.setAdapter(adapter);

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
        galleryBtn.setOnClickListener(view -> {
            setUpImgUri();
            //starting gallery
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_GALLERY_CODE);
        });
        cameraBtn.setOnClickListener(view -> {
            setUpImgUri();
            //starting camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            intent.putExtra("android.intent.extra.quickCapture", true);
            startActivityForResult(intent, PICK_CAMERA_CODE);
        });
        refreshBtn.setOnClickListener(view -> {
            if (Objects.requireNonNull(bookInput.getText()).length() > 0) {
                searchString = bookInput.getText().toString();
                coversToSearch = 10;
                searchBooks(searchString);

                try {
                    Thread.sleep(1000);
                    moreCoversBtn.setVisibility(View.VISIBLE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        moreCoversBtn.setOnClickListener(view -> {
            if (!searchString.isEmpty()) {
                coversToSearch += 10;
                searchBooks(searchString);
            }
        });
    }

    private void setUpImgUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image");
        imgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void searchBooks(String search) {
        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
        if (networkInfo.isConnected()) {
            new FetchBook(adapter, this, coversToSearch).execute(search);
        }
        // Otherwise update the TextView to tell the user there is no connection or no search term.
        else {
            Log.e(TAG, "searchBooks: ", new NetworkErrorException("No network"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && imgUri != null) {

            Uri realUri = Uri.parse(getRealPath(imgUri));

            //Get file url in app directory
            String fileUrl = createCoverFileInAppDirectory(realUri,
                    requestCode == PICK_CAMERA_CODE);

            //Send it to the AddBookActivity
            if (fileUrl!=null){
                setResult(RESULT_OK, new Intent().putExtra("url", fileUrl));
                finish();
            }else{
                Toast.makeText(this, getString(R.string.adding_cover_error),
                        Toast.LENGTH_LONG).show();
            }

        }
    }


    /**
     * Creates a bitmap from uri next scales it into the cover's format (280px-385pix)
     * then creates a file from bitmap and saves it into the application's directory/files/covers
     *
     * @param uri
     * @return successfully created file's' path in app directory, if there was an error - returns null
     */
    public String createCoverFileInAppDirectory(@NotNull Uri uri, boolean deleteParentFile) {

        String createdFilePath = null;

        FileInputStream in;
        BufferedInputStream buf;

        try {
            in = new FileInputStream(uri.toString());
            buf = new BufferedInputStream(in);

            Bitmap _bitmapPreScale = BitmapFactory.decodeStream(buf);
            Bitmap _bitmapScaled = scaleBitmap(_bitmapPreScale, 280, 385);

            //compress to JPEG
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            _bitmapScaled.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            //create file's path in app directory
            String newFilePath = getApplicationInfo().dataDir;
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
            buf.close();
            bytes.close();
            in.close();

            if (deleteParentFile){
                new File(uri.toString()).delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createdFilePath;
    }

    public String getRealPath(Uri uri) {
        //Get real file path
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);

        cursor.close();
        return path;
    }

    public Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
        int oldWidth = bitmap.getWidth();
        int oldHeight = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / oldWidth;
        float scaleHeight = ((float) newHeight) / oldHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, oldWidth, oldHeight, matrix, true);
    }
}
