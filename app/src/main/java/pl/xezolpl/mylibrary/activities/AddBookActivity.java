package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
    private Button add_book_ok_btn, add_book_cancel_btn, select_category_btn, select_image_btn;

    private Book thisBook = null;
    private String bookId;
    private boolean inEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

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
        Glide.with(this).asBitmap().load(thisBook.getImageUrl()).into(add_book_image);
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

        select_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent intent = new Intent(AddBookActivity.this, SelectCoverActivity.class);
                 startActivity(intent);
            }
        });

        select_category_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(AddBookActivity.this)
                        .setPositiveButton("OK", null)
                        .setView(R.layout.fragment_categories)
                        .create();
                dialog.show();

                RecyclerView recView = dialog.findViewById(R.id.recView);
                if (recView != null) {
                    recView.setAdapter(new CategoryRecViewAdapter(AddBookActivity.this,
                            CategoryRecViewAdapter.SELECTING_CATEGORIES_MODE, getSupportFragmentManager(), bookId));
                    recView.setLayoutManager(new GridLayoutManager(AddBookActivity.this, 2));
                }else{
                    Log.w(TAG, "select_category_btn -> onClick: ", new  NullPointerException());
                }
            }
        });


        add_book_ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        String title, author, imageUrl, description;
        int status, pages = 0;

        if (add_book_title.getText().length() > 1) {
            title = add_book_title.getText().toString();
        } else return false;

        author = add_book_author.getText().toString();
        imageUrl = "https://images-na.ssl-images-amazon.com/images/I/51uLvJlKpNL._SX321_BO1,204,203,200_.jpg";
        description = add_book_description.getText().toString();
        status = status_spinner.getSelectedItemPosition();


        if (add_book_pages.getText().length() > 0) {
            try {
                pages = Integer.valueOf(add_book_pages.getText().toString());
            } catch (NumberFormatException exc) {
                Toast.makeText(this, "Type pages as a number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        thisBook = new Book(title, author, imageUrl, description, pages, bookId, status);
        return true;
    }
}