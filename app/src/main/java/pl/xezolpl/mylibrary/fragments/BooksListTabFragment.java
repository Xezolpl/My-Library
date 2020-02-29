package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.BooksRecViewAdapter;
import pl.xezolpl.mylibrary.models.Book;
import pl.xezolpl.mylibrary.models.CategoryWithBook;
import pl.xezolpl.mylibrary.viewmodels.BookViewModel;
import pl.xezolpl.mylibrary.viewmodels.CategoriesViewModel;

public class BooksListTabFragment extends Fragment {

    private Context context;
    private int tabBooksStatus;

    private BooksRecViewAdapter booksRecViewAdapter;

    private String categoryName = null;
    private boolean favourites = false;
    private RelativeLayout no_books_lay = null;

    public BooksListTabFragment() {
        tabBooksStatus = Book.STATUS_NEUTRAL;
    }

    BooksListTabFragment(int tabBooksStatus) {
        this.tabBooksStatus = tabBooksStatus;
    }

    public BooksListTabFragment(String categoryName) {
        this.categoryName = categoryName;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        setHasOptionsMenu(true);

        booksRecViewAdapter = new BooksRecViewAdapter(context);

        BookViewModel bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        /*ALL BOOKS*/
        if (tabBooksStatus == Book.STATUS_NEUTRAL) {
            bookViewModel.getAllBooks().observe(this, books -> {
                if (categoryName == null) {
                    booksRecViewAdapter.setBooks(books);
                    if (booksRecViewAdapter.getItemCount() == 0) no_books_lay.setVisibility(View.VISIBLE);
                    else no_books_lay.setVisibility(View.GONE);
                } else {
                    /*BOOKS WITH CATEGORY*/
                    final List<Book> booksWithCategory = new ArrayList<>();

                    CategoriesViewModel categoriesViewModel = new ViewModelProvider((FragmentActivity) context)
                            .get(CategoriesViewModel.class);

                    categoriesViewModel.getBooksByCategory(categoryName)
                            .observe((FragmentActivity) context, categories -> {
                                for (int i = 0; i < categories.size(); i++) {
                                    for (int j = 0; j < books.size(); j++) {
                                        CategoryWithBook cat1 = categories.get(i);
                                        Book book1 = books.get(j);
                                        if (cat1.getBookId().equals(book1.getId())) {
                                            booksWithCategory.add(book1);
                                            break;
                                        }
                                    }

                                }
                                booksRecViewAdapter.setBooks(booksWithCategory);
                                if (booksRecViewAdapter.getItemCount() == 0) no_books_lay.setVisibility(View.VISIBLE);
                                else no_books_lay.setVisibility(View.GONE);
                            });
                }
            });
        }
        /*OTHER STATUS*/
        else {
            bookViewModel.getBookWithStatus(tabBooksStatus).observe(this, books -> {
                booksRecViewAdapter.setBooks(books);
                if (booksRecViewAdapter.getItemCount() == 0) no_books_lay.setVisibility(View.VISIBLE);
                else no_books_lay.setVisibility(View.GONE);
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_books_list, container, false);

        RecyclerView booksRecView = view.findViewById(R.id.booksRecView);
        booksRecView.setAdapter(booksRecViewAdapter);
        booksRecView.setLayoutManager(new GridLayoutManager(context, 2));

        no_books_lay = view.findViewById(R.id.no_books_lay);
        no_books_lay.setVisibility(View.GONE);
        if (booksRecViewAdapter.getItemCount()==0) no_books_lay.setVisibility(View.VISIBLE);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.books_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (booksRecViewAdapter!=null){
                    booksRecViewAdapter.getFilter().filter(s);
                }
                return false;
            }
        });

        MenuItem action_favourite = menu.findItem(R.id.action_favourite);
        action_favourite.setIcon(ContextCompat.getDrawable(context, favourites ? R.mipmap.favourite_star :R.mipmap.favourite_star_off));
        action_favourite.setOnMenuItemClickListener(menuItem -> {
            if (favourites) {
                action_favourite.setIcon(ContextCompat.getDrawable(context, R.mipmap.favourite_star_off));
                booksRecViewAdapter.setFavouriteFilter(false);
                favourites = false;
            } else {
                action_favourite.setIcon(ContextCompat.getDrawable(context, R.mipmap.favourite_star));
                booksRecViewAdapter.setFavouriteFilter(true);
                favourites = true;
            }
            return false;
        });
    }
}
