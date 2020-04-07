package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.nikartm.button.FitButton;

import java.io.File;
import java.util.UUID;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.CategoryRecViewAdapter;
import pl.xezolpl.mylibrary.managers.BackupManager;
import pl.xezolpl.mylibrary.managers.SettingsManager;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.utilities.Requests;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;

/**
 * Most of the "add" classes have similar or that same methods so documentation for methods such as
 * initWidgets, setOnClickListeners etc. search here. I hope you enjoy ;)
 */
public class AddBookActivity extends AppCompatActivity {

    //Widgets
    private EditText add_book_title, add_book_author, add_book_description, add_book_pages; // basically they are MaterialEditTexts
    private ImageView add_book_image;
    private Spinner status_spinner;
    private FitButton add_book_ok_btn, add_book_cancel_btn, select_category_btn, select_image_btn;

    //Models
    private Book thisBook = null;

    //Variables
    private String bookId, imageUrl = null;
    private boolean inEdition = false;
    private boolean isFavourite = false;
    private int backCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new SettingsManager(this).loadDialogTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_book);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setFinishOnTouchOutside(false);

        initWidgets();
        setUpStatusSpinner();
        setOnClickListeners();

        Intent intent = getIntent();
        if (intent.hasExtra("book")) { //if we are editing this book
            thisBook = (Book) intent.getSerializableExtra("book");
            loadBookData(thisBook);
            bookId = thisBook.getId();
            isFavourite = thisBook.isFavourite();
            inEdition = true;
        } else { // we are creating the new book ->  lets set its new, unique id
            bookId = UUID.randomUUID().toString();
        }
    }

    /**
     * Loads data to the widgets (inEdition)
     * @param thisBook currently editing book
     */
    private void loadBookData(Book thisBook) {
        add_book_title.setText(thisBook.getTitle());
        add_book_author.setText(thisBook.getAuthor());
        add_book_description.setText(thisBook.getDescription());
        add_book_pages.setText(String.valueOf(thisBook.getPages()));
        status_spinner.setSelection(thisBook.getStatus());
        imageUrl = thisBook.getImageUrl();

        if (new File(imageUrl).exists() || imageUrl.contains("books.google")) {
            Glide.with(this).asBitmap().load(imageUrl).into(add_book_image);
        } else {
            Glide.with(this).asBitmap().load(new BackupManager(this).standardCoverUrl).into(add_book_image);
        }
    }

    /**
     * Standard widgets initiation
     */
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

    /**
     * Sets the onClickListeners to the widgets
     */
    private void setOnClickListeners() {

        select_image_btn.setOnClickListener(view ->
            startActivityForResult(new Intent(this,
                    SelectCoverActivity.class), Requests.SELECT_COVER_REQUEST));

        select_category_btn.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(AddBookActivity.this)
                    .setPositiveButton("OK", null)
                    .setView(R.layout.fragment_categories)
                    .create();
            dialog.show();

            RecyclerView recView = dialog.findViewById(R.id.recView);
            if (recView != null) {
                recView.setAdapter(new CategoryRecViewAdapter(AddBookActivity.this,
                        CategoryRecViewAdapter.SELECT_CATEGORIES_MODE, bookId));
                recView.setLayoutManager(new GridLayoutManager(AddBookActivity.this, 2));
            } else {
                Toast.makeText(this, getString(R.string.something_went_wrong_try_again), Toast.LENGTH_LONG).show();
            }
        });

        add_book_ok_btn.setOnClickListener(view -> {
            if (areValidOutputs()) {
                BookViewModel model = new ViewModelProvider(this).get(BookViewModel.class);
                if (inEdition) {
                    model.update(thisBook);

                    //Returns intent to set the new values to the widgets in BookDetailsTabFragment
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

    /**
     *  Creates adapter for the status_spinner and sets it
     */
    private void setUpStatusSpinner() {
        ArrayAdapter<CharSequence> statusArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, R.layout.simple_spinner_item);
        statusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status_spinner.setAdapter(statusArrayAdapter);
    }

    /**
     * Checks are the outputs valid
     * @return true if title is longer than 1 char, if pages are not number - false, and in every other case false;
     */
    private boolean areValidOutputs() {
        String title, author, description;
        int status, pages = 0;

        if (add_book_title.getText().length() > 1) {
            title = add_book_title.getText().toString();
        } else {
            Toast.makeText(this, getString(R.string.short_title), Toast.LENGTH_SHORT).show();
            return false;
        }

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

        //Setting imageUrl to the standard cover if it is a null
        if (imageUrl == null) {
            imageUrl = getApplicationInfo().dataDir + "/files/covers/standard_cover.jpg";
        }

        thisBook = new Book(title, author, imageUrl, description, pages, bookId, status);
        thisBook.setFavourite(isFavourite);
        return true;
    }

    /**
     * Getting the imageUrl from SelectCoverActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Requests.SELECT_COVER_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUrl = data.getStringExtra("url");
            Glide.with(this).asBitmap().load(imageUrl).into(add_book_image);
        }
    }

    /**
     * Protection against accidental exit from dialog
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backCounter == 0) {
                backCounter = 1;
                (new Handler()).postDelayed(() -> backCounter = 0, 2000);
            } else {
                backCounter = 0;
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}