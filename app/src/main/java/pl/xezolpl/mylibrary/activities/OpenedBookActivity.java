package pl.xezolpl.mylibrary.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.fragments.BooksTabFragment;
import pl.xezolpl.mylibrary.models.Book;

public class OpenedBookActivity extends AppCompatActivity {
    private static final String TAG = "OpenedBookActivity";
    private TextView bookTitle_text, bookDescription_text, bookPages_text, bookAuthor_text;
    private ImageView book_image;
    private Button setToRead_btn, setCurrReading_btn, setAlreadyRead_btn, setFavourite_btn;
    private Toolbar opened_book_toolbar;
    private Book thisBook = null;

    private Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_book);

        Intent intent = getIntent();
        thisBook = (Book) intent.getSerializableExtra("book");
        initWidgets();
        loadBookData();
        createOnClickListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.opened_book_menu, menu);

        MenuItem editItem = menu.findItem(R.id.action_edit);
        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //I TOTALLY DONT KNOW WHAT CAN I DO
                allBooksActivity.startActivityForResult(BooksTabFragment.UPDATE_BOOK_ACTIVITY_REQUEST_CODE, thisBook);
                return false;
            }
        });

        MenuItem delItem = menu.findItem(R.id.action_delete);
        delItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //AlertDialog.Builder builder;
                return false;
            }
        });
        return true;
    }

    private void initWidgets() {
        bookTitle_text = (TextView) findViewById(R.id.bookTitle_text);
        bookAuthor_text = (TextView) findViewById(R.id.bookAuthor_text);
        bookPages_text = (TextView) findViewById(R.id.bookPages_text);
        bookDescription_text = (TextView) findViewById(R.id.bookDescription_text);

        book_image = (ImageView) findViewById(R.id.book_image);

        setToRead_btn = (Button) findViewById(R.id.setToRead_btn);
        setCurrReading_btn = (Button) findViewById(R.id.setCurrReading_btn);
        setAlreadyRead_btn = (Button) findViewById(R.id.setAlreadyRead_btn);
        setFavourite_btn = (Button) findViewById(R.id.setFavourite_btn);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            opened_book_toolbar = (Toolbar) findViewById(R.id.opened_book_toolbar);
            opened_book_toolbar.setTitle(thisBook.getTitle());
            setSupportActionBar(opened_book_toolbar);
        }
    }

    private void loadBookData() {
        bookTitle_text.setText(thisBook.getTitle());
        bookAuthor_text.setText(thisBook.getAuthor());
        bookPages_text.setText("Pages: " + thisBook.getPages());
        bookDescription_text.setText(thisBook.getDescription());
        Glide.with(this).asBitmap().load(thisBook.getImageUrl()).into(book_image);
    }

    private void createOnClickListeners() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status change");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        setToRead_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thisBook.getStatus() == Book.STATUS_WANT_TO_READ) {
                    builder.setMessage("This book is currently in to read list. Do you want to " +
                            "remove it from the list?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thisBook.setStatus(Book.STATUS_NEUTRAL);
                        }
                    });
                    builder.create().show();
                } else {
                    thisBook.setStatus(Book.STATUS_WANT_TO_READ);
                }
                Toast.makeText(getContext(), "Successfully changed the book status.", Toast.LENGTH_SHORT).show();
                updateBook();

            }
        });


        setCurrReading_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thisBook.getStatus() == Book.STATUS_CURRENTLY_READING) {
                    builder.setMessage("Your are currently reading this book. Do you want to " +
                            "remove it from the currently reading list?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thisBook.setStatus(Book.STATUS_NEUTRAL);
                        }
                    });
                    builder.create().show();
                } else {
                    thisBook.setStatus(Book.STATUS_CURRENTLY_READING);
                }
                Toast.makeText(getContext(), "Successfully changed the book status.", Toast.LENGTH_SHORT).show();
                updateBook();
            }
        });


        setAlreadyRead_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thisBook.getStatus() == Book.STATUS_ALREADY_READ) {
                    builder.setMessage("You have already read this book. Do you want to " +
                            "remove it from the already read list?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thisBook.setStatus(Book.STATUS_NEUTRAL);
                        }
                    });
                    builder.create().show();
                } else {
                    thisBook.setStatus(Book.STATUS_ALREADY_READ);
                }
                Toast.makeText(getContext(), "Successfully changed the book status.", Toast.LENGTH_SHORT).show();
                updateBook();
            }
        });


        setFavourite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thisBook.isFavourite()) thisBook.setFavourite(false);
                else thisBook.setFavourite(true);
                updateBook();
            }
        });

    }

    private void updateBook(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("resultBook",thisBook);
        setResult(RESULT_OK,resultIntent);
    }
}


