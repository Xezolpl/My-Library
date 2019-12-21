package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.AddBookActivity;
import pl.xezolpl.mylibrary.adapters.TabFragmentPagerAdapter;
import pl.xezolpl.mylibrary.models.Book;

public class AllBooksFragment extends Fragment {

    private FloatingActionButton fab;
    private ViewPager books_viewPager;
    private TabLayout books_tabLayout;

    private Context context;
    private TabFragmentPagerAdapter sectionsPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_books, container, false);
        context = getContext();

        initWidgets(view);
        setOnClickListeners();

        setUpViewPager();
        books_tabLayout.setupWithViewPager(books_viewPager);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                BooksListTabFragment tabFragment = (BooksListTabFragment) sectionsPagerAdapter
                        .getItem(books_viewPager.getCurrentItem());
                tabFragment.setFilter(s);
                return false;
            }
        });
    }


    private void initWidgets(View v) {
        books_viewPager = (ViewPager) v.findViewById(R.id.books_viewPager);
        books_tabLayout = (TabLayout) v.findViewById(R.id.books_tabLayout);
        fab = (FloatingActionButton) v.findViewById(R.id.add_book_fab);

    }

    private void setOnClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddBookActivity.class);
                startActivity(intent);
            }
        });
    }


    public void setUpViewPager() {
        sectionsPagerAdapter = new TabFragmentPagerAdapter(getFragmentManager());

        Fragment allBooksFragment = new BooksListTabFragment(Book.STATUS_NEUTRAL, context);
        Fragment wantBooksFragment = new BooksListTabFragment(Book.STATUS_WANT_TO_READ, context);
        Fragment currBooksFragment = new BooksListTabFragment(Book.STATUS_CURRENTLY_READING, context);
        Fragment alrBooksFragment = new BooksListTabFragment(Book.STATUS_ALREADY_READ, context);

        sectionsPagerAdapter.addFragment(allBooksFragment, "All books");
        sectionsPagerAdapter.addFragment(wantBooksFragment, "Want to read books");
        sectionsPagerAdapter.addFragment(currBooksFragment, "Currently reading books");
        sectionsPagerAdapter.addFragment(alrBooksFragment, "Already read books");

        books_viewPager.setAdapter(sectionsPagerAdapter);
    }

}