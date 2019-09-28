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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.BookModel;
import pl.xezolpl.mylibrary.models.Status;
import pl.xezolpl.mylibrary.utlis.MyUtil;

public class OpenedBookActivity extends AppCompatActivity {
    private static final String TAG = "OpenedBookActivity";
    private TextView titleText, descriptionText, pagesText, authorText;
    private ImageView bookImage;
    private Button toReadBtn, currReadingBtn, alreadyReadBtn, favouriteBtn;
    private ActionBar actionBar;
    private BookModel thisBook=null;
    private int id;
    private ArrayList<BookModel> books;

    private Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_book);
        Intent intent = getIntent();
        id = intent.getIntExtra("bookId", 0);
        initWidgets();
        loadBookData();
        createOnClickListeners();

    }

    private void initWidgets() {
        titleText = (TextView) findViewById(R.id.titleText);
        authorText = (TextView) findViewById(R.id.authorText);
        pagesText = (TextView) findViewById(R.id.pagesText);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        bookImage = (ImageView) findViewById(R.id.bookImage);

        toReadBtn = (Button) findViewById(R.id.toReadBtn);
        currReadingBtn = (Button) findViewById(R.id.currReadingBtn);
        alreadyReadBtn = (Button) findViewById(R.id.alreadyReadBtn);
        favouriteBtn = (Button) findViewById(R.id.favouriteBtn);

    }
    private void loadBookData(){
        MyUtil myUtil = new MyUtil();
        books = myUtil.getAllBooks();
        for (BookModel b : books) {
            if (b.getId() == id) {
                thisBook = b;
                titleText.setText(b.getTitle());
                authorText.setText(b.getAuthor());
                pagesText.setText("Pages: " + b.getPages());
                descriptionText.setText(b.getDescription());
                Glide.with(this).asBitmap().load(b.getImageUrl()).into(bookImage);
            }
        }
    }
    private void createOnClickListeners(){
        //AlertDialog building...
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status change");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        toReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thisBook.getStatus()== Status.WANT_TO_READ){
                    builder.setMessage("This book is currently in to read list. Do you want to " +
                            "remove it from the list?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thisBook.setStatus(Status.NEUTRAL);
                        }
                    });
                    builder.create().show();
                }
                else{
                    thisBook.setStatus(Status.WANT_TO_READ);
                }
                Toast.makeText(getContext(),"Successfully changed the book status.",Toast.LENGTH_SHORT).show();
            }
        });
        currReadingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thisBook.getStatus()== Status.CURRENTLY_READING){
                    builder.setMessage("Your are currently reading this book. Do you want to " +
                            "remove it from the currently reading list?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thisBook.setStatus(Status.NEUTRAL);
                        }
                    });
                    builder.create().show();
                }
                else {
                    thisBook.setStatus(Status.CURRENTLY_READING);
                }
                Toast.makeText(getContext(),"Successfully changed the book status.",Toast.LENGTH_SHORT).show();
            }
        });
        alreadyReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thisBook.getStatus()== Status.ALREADY_READ){
                    builder.setMessage("You have already read this book. Do you want to " +
                            "remove it from the already read list?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            thisBook.setStatus(Status.NEUTRAL);
                        }
                    });
                    builder.create().show();
                }
                else {
                    thisBook.setStatus(Status.ALREADY_READ);
                }
                Toast.makeText(getContext(),"Successfully changed the book status.",Toast.LENGTH_SHORT).show();
            }
        });
        favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thisBook.isFavourite()) thisBook.setFavourite(false);
                else thisBook.setFavourite(true);
                }
        });
    }

}


