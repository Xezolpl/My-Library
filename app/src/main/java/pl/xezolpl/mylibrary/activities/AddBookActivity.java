package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.nikartm.button.FitButton;

import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.CategoryRecViewAdapter;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;

public class AddBookActivity extends AppCompatActivity {
    private static final String TAG = "AddBookActivity";

    private EditText add_book_title, add_book_author, add_book_description, add_book_pages;
    private ImageView add_book_image;
    private Spinner status_spinner;
    private FitButton add_book_ok_btn, add_book_cancel_btn, select_category_btn, select_image_btn;
    private Book thisBook = null;
    private String bookId, imageUrl = null;
    private boolean inEditing = false;

    public static final int SELECT_COVER_REQ_CODE = 1;
    private int backCounter =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initWidgets();
        setUpStatusSpinner();
        setFinishOnTouchOutside(false);
        setOnClickListeners();

        Intent intent = getIntent();
        if (intent.hasExtra("book")) {
            thisBook = (Book) intent.getSerializableExtra("book");
            inEditing = true;
            loadBookData(thisBook);
            bookId = thisBook.getId();
        } else {
            bookId = UUID.randomUUID().toString();
        }
    }

    private void loadBookData(Book thisBook) {
        add_book_title.setText(thisBook.getTitle());
        add_book_author.setText(thisBook.getAuthor());
        add_book_description.setText(thisBook.getDescription());
        add_book_pages.setText(String.valueOf(thisBook.getPages()));
        status_spinner.setSelection(thisBook.getStatus());
        Glide.with(this).asBitmap().load(thisBook.getImageUrl()).into(add_book_image);
        imageUrl = thisBook.getImageUrl();
    }

    private void initWidgets() {

        add_book_title = findViewById(R.id.add_book_title);
        add_book_author = findViewById(R.id.add_book_author);
        add_book_description = findViewById(R.id.add_book_description);
        add_book_pages = findViewById(R.id.add_book_pages);

        add_book_image = findViewById(R.id.add_book_image);
        status_spinner = findViewById(R.id.status_spinner);

        add_book_ok_btn = findViewById(R.id.add_book_ok_btn);
        add_book_cancel_btn = findViewById(R.id.add_book_cancel_btn);
        select_image_btn = findViewById(R.id.select_image_btn);
        select_category_btn = findViewById(R.id.select_category_btn);
    }

    private void setOnClickListeners() {
        select_image_btn.setOnClickListener(view -> {
            //todo: does user  entered the isbn?
            Intent intent = new Intent(AddBookActivity.this, SelectCoverActivity.class);
            startActivityForResult(intent, SELECT_COVER_REQ_CODE);
        });
        select_category_btn.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(AddBookActivity.this)
                    .setPositiveButton("OK", null)
                    .setView(R.layout.fragment_categories)
                    .create();
            dialog.show();

            RecyclerView recView = dialog.findViewById(R.id.recView);
            if (recView != null) {
                recView.setAdapter(new CategoryRecViewAdapter(AddBookActivity.this,
                        CategoryRecViewAdapter.SELECTING_CATEGORIES_MODE, bookId));
                recView.setLayoutManager(new GridLayoutManager(AddBookActivity.this, 2));
            } else {
                Log.w(TAG, "select_category_btn -> onClick: ", new NullPointerException());
            }
        });


        add_book_ok_btn.setOnClickListener(view -> {
            if (areValidOutputs()) {
                BookViewModel model = ViewModelProviders.of(AddBookActivity.this).get(BookViewModel.class);
                if (inEditing) {
                    model.update(thisBook);

                    Intent intent = new Intent();
                    intent.putExtra("book", thisBook);
                    setResult(RESULT_OK, intent);

                } else {
                    model.insert(thisBook);
                }
                finish();
            }
        });

        add_book_cancel_btn.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

    }

    private void setUpStatusSpinner() {
        ArrayAdapter<CharSequence> statusArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(statusArrayAdapter);
    }

    private boolean areValidOutputs() {
        String title, author, description;
        int status, pages = 0;

        if (add_book_title.getText().length() > 1) {
            title = add_book_title.getText().toString();
        } else return false;

        author = add_book_author.getText().toString();
        description = add_book_description.getText().toString();
        status = status_spinner.getSelectedItemPosition();


        if (add_book_pages.getText().length() > 0) {
            try {
                pages = Integer.valueOf(add_book_pages.getText().toString());
            } catch (NumberFormatException exc) {
                Toast.makeText(this, getString(R.string.type_pages_as_a_number), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (imageUrl == null) {
            imageUrl = getApplicationInfo().dataDir + "/files/covers/standard_cover.jpg";
        }

        thisBook = new Book(title, author, imageUrl, description, pages, bookId, status);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_COVER_REQ_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                imageUrl = data.getStringExtra("url");
                Glide.with(this).asBitmap().load(imageUrl).into(add_book_image);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backCounter == 0){
                backCounter = 1;
                (new Handler()).postDelayed(()->backCounter=0, 2000);
            } else {
                backCounter = 0;
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}