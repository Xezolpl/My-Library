package pl.xezolpl.mylibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddBookActivity;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.utilities.Requests;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;

import static android.app.Activity.RESULT_OK;

public class BookDetailsTabFragment extends Fragment {

    private TextView bookTitle_text, bookDescription_text, bookPages_text, bookAuthor_text;
    private ImageView book_image;
    private Button setToRead_btn, setCurrReading_btn, setAlreadyRead_btn, setFavourite_btn;

    private Book thisBook;
    private BookViewModel bookViewModel;

    private Context context;

    public BookDetailsTabFragment(Book thisBook, Context context) {
        this.context = context;
        this.thisBook = thisBook;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_book_details, container, false);

        initWidgets(view);
        loadBookData();
        createOnClickListeners();

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.opened_book_menu, menu);

        MenuItem editItem = menu.findItem(R.id.action_edit);
        editItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(context, AddBookActivity.class);
                intent.putExtra("book", thisBook);
                startActivityForResult(intent, Requests.EDIT_REQUEST);
                return false;
            }
        });

        MenuItem delItem = menu.findItem(R.id.action_delete);
        delItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                bookViewModel.delete(thisBook);
                ((Activity)context).finish();
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Requests.EDIT_REQUEST && resultCode == RESULT_OK) {
            thisBook = (Book) data.getSerializableExtra("book");
            loadBookData();
        }
    }

    private void initWidgets(View v) {
        bookTitle_text = (TextView) v.findViewById(R.id.bookTitle_text);
        bookAuthor_text = (TextView) v.findViewById(R.id.bookAuthor_text);
        bookPages_text = (TextView) v.findViewById(R.id.bookPages_text);
        bookDescription_text = (TextView) v.findViewById(R.id.bookDescription_text);

        book_image = (ImageView) v.findViewById(R.id.book_image);

        setToRead_btn = (Button) v.findViewById(R.id.setToRead_btn);
        setCurrReading_btn = (Button) v.findViewById(R.id.setCurrReading_btn);
        setAlreadyRead_btn = (Button) v.findViewById(R.id.setAlreadyRead_btn);
        setFavourite_btn = (Button) v.findViewById(R.id.setFavourite_btn);
    }

    private void loadBookData() {
        bookTitle_text.setText(thisBook.getTitle());
        bookAuthor_text.setText(thisBook.getAuthor());
        bookPages_text.setText("Pages: " + thisBook.getPages());
        bookDescription_text.setText(thisBook.getDescription());
        Glide.with(this).asBitmap().load(thisBook.getImageUrl()).into(book_image);
    }

    private void createOnClickListeners() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Status change");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
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
                            updateBook(Book.STATUS_NEUTRAL);
                        }
                    });
                    builder.create().show();
                } else {
                    updateBook(Book.STATUS_WANT_TO_READ);
                }

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
                            updateBook(Book.STATUS_NEUTRAL);
                        }
                    });
                    builder.create().show();
                } else {
                    thisBook.setStatus(Book.STATUS_CURRENTLY_READING);
                }
                updateBook(Book.STATUS_CURRENTLY_READING);
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
                            updateBook(Book.STATUS_NEUTRAL);
                        }
                    });
                    builder.create().show();
                } else {
                    updateBook(Book.STATUS_ALREADY_READ);
                }
            }
        });


        setFavourite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thisBook.isFavourite()) thisBook.setFavourite(false);
                else thisBook.setFavourite(true);
                bookViewModel.update(thisBook);
            }
        });
    }

    private void updateBook(int status) {
        thisBook.setStatus(status);
        bookViewModel.update(thisBook);
        Toast.makeText(getContext(), "Successfully changed the book status.", Toast.LENGTH_SHORT).show();
    }
}