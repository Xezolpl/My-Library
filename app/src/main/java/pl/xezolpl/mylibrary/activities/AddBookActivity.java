package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.dialogs.SelectCoverDialog;
import pl.xezolpl.mylibrary.models.Book;

public class AddBookActivity extends AppCompatActivity {
    private static final String TAG = "AddBookActivity";

    private EditText add_book_title, add_book_author, add_book_description, add_book_pages, add_book_chapters_edtTxt;
    private Button select_image_btn;
    private ImageView add_book_image;
    private Spinner status_spinner;
    private RecyclerView book_covers_rec_view;
    private Button add_book_ok_btn, add_book_cancel_btn;

    private Book thisBook = null;
    private int chaptersCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        initWidgets();
        setUpStatusSpinner();
        setFinishOnTouchOutside(false);
        setOnClickListeners();
    }

    private void loadBookData(Book thisBook) {
        add_book_title.setText(thisBook.getTitle());
        add_book_author.setText(thisBook.getAuthor());
        add_book_description.setText(thisBook.getDescription());
        add_book_pages.setText(String.valueOf(thisBook.getPages()));
        Glide.with(this).asBitmap().load(thisBook.getImageUrl()).into(add_book_image);
    }

    private void initWidgets() {

        add_book_title = (EditText) findViewById(R.id.add_book_title);
        add_book_author = (EditText) findViewById(R.id.add_book_author);
        add_book_description = (EditText) findViewById(R.id.add_book_description);
        add_book_pages = (EditText) findViewById(R.id.add_book_pages);

        add_book_image = (ImageView) findViewById(R.id.add_book_image);
        select_image_btn = (Button) findViewById(R.id.select_image_btn);
        status_spinner = (Spinner) findViewById(R.id.status_spinner);
        book_covers_rec_view = (RecyclerView) findViewById(R.id.book_covers_rec_view);

        //result buttons
        add_book_ok_btn = (Button) findViewById(R.id.add_book_ok_btn);
        add_book_cancel_btn = (Button) findViewById(R.id.add_book_cancel_btn);
    }

    private void setOnClickListeners() {

        select_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectCoverDialog coverDialog = new SelectCoverDialog();
                coverDialog.show(getSupportFragmentManager(), "Select book's cover");
                //TODO:SELECT COVER AND SEND IT TO THIS ACTIVITY
            }
        });

        add_book_ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areValidOutputs()) {

                    String title = add_book_title.getText().toString();
                    String author = add_book_author.getText().toString();
                    String imageUrl = "https://images-na.ssl-images-amazon.com/images/I/51uLvJlKpNL._SX321_BO1,204,203,200_.jpg";
                    String description = add_book_description.getText().toString();
                    int pages = Integer.valueOf(add_book_pages.getText().toString());
                    int status = status_spinner.getSelectedItemPosition();
                    String id;

                    if (thisBook == null) {
                        id = UUID.randomUUID().toString();
                        thisBook = new Book(title, author, imageUrl, description, pages,
                                id, status);
                    } else {
                        id = thisBook.getId();
                        thisBook = new Book(title, author, imageUrl, description, pages,
                                id, status);
                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("book", thisBook);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        add_book_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void setUpStatusSpinner() {
        ArrayAdapter<CharSequence> statusArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(statusArrayAdapter);
    }

    private boolean areValidOutputs() {
        int pages = 0;
        try {
            pages = Integer.valueOf(add_book_pages.getText().toString());
        } catch (NumberFormatException exc) {
            Toast.makeText(this, "Type pages as a number", Toast.LENGTH_SHORT).show();
        }
        return (add_book_title.length() > 0 &&
                add_book_author.length() > 0 &&
                add_book_description.length() > 0 &&
                pages != 0);
    }
/* FIRST FIND API FOR IT!!!
    private void downloadImageFromUri(String address) {
        URL url;
        try {
            url = new URL(address);
        } catch (MalformedURLException e1) {
            url = null;
        }

        URLConnection conn;
        InputStream in;
        Bitmap bitmap;
        try {
            conn = url.openConnection();
            conn.connect();
            in = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e) {
            bitmap = null;
        }

        if (bitmap != null) {
            add_book_image.setImageBitmap(bitmap);
        }
    }
*/
}
