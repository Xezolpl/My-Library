package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddBookActivity;
import pl.xezolpl.mylibrary.adapters.BooksRecViewAdapter;
import pl.xezolpl.mylibrary.adapters.TabFragmentPagerAdapter;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;

public class AllBooksFragment extends Fragment {
    private FloatingActionButton fab;
    private Context context;
    private BooksRecViewAdapter booksRecViewAdapter;
    private RelativeLayout no_books_lay = null;
    private boolean favourites = false;
    private int activeStatus = 0;

    private TextView navTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        context = getContext();
        booksRecViewAdapter = new BooksRecViewAdapter(getContext());

        BookViewModel viewModel = new ViewModelProvider(this).get(BookViewModel.class);
        viewModel.getAllBooks().observe(this, books -> {
            if (books.size() == 0) no_books_lay.setVisibility(View.VISIBLE);
            else no_books_lay.setVisibility(View.GONE);

            booksRecViewAdapter.setBooks(books);

        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_books, container, false);

        initWidgets(view);
        setOnClickListeners();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.all_books_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Filtering mechanic
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (booksRecViewAdapter != null) {
                    booksRecViewAdapter.setFilterPattern(s);
                }
                return false;
            }
        });

        // MenuItem favourite - set its icon and text basing on is thisBook favourite or no
        MenuItem action_favourite = menu.findItem(R.id.favourite);
        action_favourite.setIcon(ContextCompat.getDrawable(context, favourites ? R.mipmap.favourite_star : R.mipmap.favourite_star_off));
        action_favourite.setOnMenuItemClickListener(menuItem -> {
            if (favourites) {
                action_favourite.setIcon(ContextCompat.getDrawable(context, R.mipmap.favourite_star_off));
                action_favourite.setTitle(getString(R.string.fav_books));
                booksRecViewAdapter.setFavouriteEnabled(false);
                favourites = false;
            } else {
                action_favourite.setIcon(ContextCompat.getDrawable(context, R.mipmap.favourite_star));
                action_favourite.setTitle(getString(R.string.all_books));
                booksRecViewAdapter.setFavouriteEnabled(true);
                favourites = true;
            }
            return false;
        });

        MenuItem actionToRead = menu.findItem(R.id.toRead);
        actionToRead.setOnMenuItemClickListener(menuItem -> {
            if (activeStatus == Book.STATUS_WANT_TO_READ) {
                activeStatus = Book.STATUS_NEUTRAL;
                navTextView.setText(R.string.all_books);
            } else {
                activeStatus = Book.STATUS_WANT_TO_READ;
                navTextView.setText(R.string.want_to_read);
            }
            booksRecViewAdapter.setStatusMode(activeStatus);
            return false;
        });

        MenuItem actionCurrReading = menu.findItem(R.id.currReading);
        actionCurrReading.setOnMenuItemClickListener(menuItem -> {
            if (activeStatus == Book.STATUS_CURRENTLY_READING) {
                activeStatus = Book.STATUS_NEUTRAL;
                navTextView.setText(R.string.all_books);
            } else {
                activeStatus = Book.STATUS_CURRENTLY_READING;
                navTextView.setText(R.string.currently_reading);
            }
            booksRecViewAdapter.setStatusMode(activeStatus);
            return false;
        });

        MenuItem actionAlreadyRead = menu.findItem(R.id.alreadyRead);
        actionAlreadyRead.setOnMenuItemClickListener(menuItem -> {
            if (activeStatus == Book.STATUS_ALREADY_READ) {
                activeStatus = Book.STATUS_NEUTRAL;
                navTextView.setText(R.string.all_books);
            } else {
                activeStatus = Book.STATUS_ALREADY_READ;
                navTextView.setText(R.string.already_read);
            }
            booksRecViewAdapter.setStatusMode(activeStatus);
            return false;
        });
    }

    private void initWidgets(View v) {
        fab = v.findViewById(R.id.add_book_fab);

        RecyclerView booksRecView = v.findViewById(R.id.booksRecView);
        booksRecView.setAdapter(booksRecViewAdapter);
        booksRecView.setLayoutManager(new GridLayoutManager(context, 2));

        no_books_lay = v.findViewById(R.id.no_books_lay);
        no_books_lay.setVisibility(View.GONE);
        if (booksRecViewAdapter.getItemCount() == 0) no_books_lay.setVisibility(View.VISIBLE);

        navTextView = v.findViewById(R.id.navTextView);

    }

    private void setOnClickListeners() {
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AddBookActivity.class);
            startActivity(intent);
        });
    }

    public BooksRecViewAdapter getBooksAdapter() {
        return booksRecViewAdapter;
    }
}