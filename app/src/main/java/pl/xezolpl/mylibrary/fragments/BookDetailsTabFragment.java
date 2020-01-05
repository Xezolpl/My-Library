package pl.xezolpl.mylibrary.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddBookActivity;
import pl.xezolpl.mylibrary.activities.OpenedBookActivity;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.utilities.DeletingHelper;
import pl.xezolpl.mylibrary.utilities.Requests;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;

import static android.app.Activity.RESULT_OK;

public class BookDetailsTabFragment extends Fragment {
    private static final String TAG = "BookDetailsTabFragment";

    private TextView bookTitle_text, bookDescription_text, bookPages_text, bookAuthor_text;
    private ImageView book_image;
    private Button setToRead_btn, setCurrReading_btn, setAlreadyRead_btn;

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
                final DeletingHelper deletingHelper = new DeletingHelper(BookDetailsTabFragment.this);
                new AlertDialog.Builder(context)
                        .setTitle("Confirm deleting")
                        .setMessage("Are you sure you want delete this book? " +
                                "Delete with quotes?")
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletingHelper.deleteBook(thisBook,true);
                                ((Activity) context).finish();
                            }
                        })
                        .setNeutralButton(getString(R.string.without_quotes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletingHelper.deleteBook(thisBook,false);
                                ((Activity) context).finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .create()
                        .show();
                return false;
            }
        });

        final MenuItem favouriteItem = menu.findItem(R.id.action_favourite);

        if (!thisBook.isFavourite()){
            favouriteItem.setIcon(ContextCompat.getDrawable(context, R.mipmap.favourite_star_off));
        }

        favouriteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(thisBook.isFavourite()){
                    favouriteItem.setIcon(ContextCompat.getDrawable(context, R.mipmap.favourite_star_off));
                    thisBook.setFavourite(false);
                }else{
                    thisBook.setFavourite(true);
                    favouriteItem.setIcon(ContextCompat.getDrawable(context, R.mipmap.favourite_star));
                }
                bookViewModel.update(thisBook);
                return false;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Requests.EDIT_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                thisBook = (Book) data.getSerializableExtra("book");

                OpenedBookActivity activity = (OpenedBookActivity) getActivity();
                if (activity != null) {
                    activity.setToolbarTitle(thisBook.getTitle());
                }
                loadBookData();
            } else {
                Log.w(TAG, "onActivityResult: ", new NullPointerException("Null result book"));
            }
        }
    }

    private void initWidgets(View v) {
        bookTitle_text = v.findViewById(R.id.bookTitle_text);
        bookAuthor_text = v.findViewById(R.id.bookAuthor_text);
        bookPages_text = v.findViewById(R.id.bookPages_text);
        bookDescription_text = v.findViewById(R.id.bookDescription_text);

        book_image = v.findViewById(R.id.book_image);

        setToRead_btn = v.findViewById(R.id.setToRead_btn);
        setCurrReading_btn = v.findViewById(R.id.setCurrReading_btn);
        setAlreadyRead_btn = v.findViewById(R.id.setAlreadyRead_btn);
    }

    private void loadBookData() {
        bookTitle_text.setText(thisBook.getTitle());
        bookAuthor_text.setText(thisBook.getAuthor());
        String pages = "Pages: " + thisBook.getPages();
        bookPages_text.setText(pages);
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
    }

    private void updateBook(int status) {
        thisBook.setStatus(status);
        bookViewModel.update(thisBook);
        Toast.makeText(getContext(), "Successfully changed the book status.", Toast.LENGTH_SHORT).show();
    }
}