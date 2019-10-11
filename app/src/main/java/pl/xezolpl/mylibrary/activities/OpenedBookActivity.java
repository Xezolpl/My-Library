package pl.xezolpl.mylibrary.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.Book;

public class OpenedBookActivity extends AppCompatActivity {
    private static final String TAG = "OpenedBookActivity";

    private TextView bookTitle_text, bookDescription_text, bookPages_text, bookAuthor_text;
    private ImageView book_image;
    private Button setToRead_btn, setCurrReading_btn, setAlreadyRead_btn, setFavourite_btn;

    private Book thisBook=null;
    private String id;

    private Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_book);

        Intent intent = getIntent();
        id = intent.getStringExtra("bookId");

        initWidgets();
        loadBookData();
        createOnClickListeners();

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

    }
    private void loadBookData(){
        List<Book> books = new ArrayList<>();
        for (Book b : books) {
            if (b.getId().equals(id)) {
                thisBook = b;
                bookTitle_text.setText(b.getTitle());
                bookAuthor_text.setText(b.getAuthor());
                bookPages_text.setText("Pages: " + b.getPages());
                bookDescription_text.setText(b.getDescription());
                Glide.with(this).asBitmap().load(b.getImageUrl()).into(book_image);
            }
        }
    }
    private void createOnClickListeners(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status change");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        setToRead_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thisBook.getStatus()== Book.STATUS_WANT_TO_READ){
                    builder.setMessage("This book is currently in to read list. Do you want to " +
                            "remove it from the list?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thisBook.setStatus(Book.STATUS_NEUTRAL);
                        }
                    });
                    builder.create().show();
                }
                else{
                    thisBook.setStatus(Book.STATUS_WANT_TO_READ);
                }
                Toast.makeText(getContext(),"Successfully changed the book status.",Toast.LENGTH_SHORT).show();

            }
        });
        setCurrReading_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thisBook.getStatus()== Book.STATUS_CURRENTLY_READING){
                    builder.setMessage("Your are currently reading this book. Do you want to " +
                            "remove it from the currently reading list?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thisBook.setStatus(Book.STATUS_NEUTRAL);
                        }
                    });
                    builder.create().show();
                }
                else {
                    thisBook.setStatus(Book.STATUS_CURRENTLY_READING);
                }
                Toast.makeText(getContext(),"Successfully changed the book status.",Toast.LENGTH_SHORT).show();
            }
        });
        setAlreadyRead_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thisBook.getStatus()== Book.STATUS_ALREADY_READ){
                    builder.setMessage("You have already read this book. Do you want to " +
                            "remove it from the already read list?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thisBook.setStatus(Book.STATUS_NEUTRAL);
                        }
                    });
                    builder.create().show();
                }
                else {
                    thisBook.setStatus(Book.STATUS_ALREADY_READ);
                }
                Toast.makeText(getContext(),"Successfully changed the book status.",Toast.LENGTH_SHORT).show();

            }
        });
        setFavourite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thisBook.isFavourite()) thisBook.setFavourite(false);
                else thisBook.setFavourite(true);
                }
        });
    }

}


