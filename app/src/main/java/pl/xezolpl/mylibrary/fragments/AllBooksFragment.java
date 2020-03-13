package pl.xezolpl.mylibrary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_books, container, false);

        initWidgets(view);
        setOnClickListeners();
        setUpViewPager();

        books_tabLayout.setupWithViewPager(books_viewPager);

        return view;
    }

    private void initWidgets(View v) {
        books_viewPager = v.findViewById(R.id.books_viewPager);
        books_tabLayout = v.findViewById(R.id.books_tabLayout);
        fab = v.findViewById(R.id.add_book_fab);

    }

    private void setOnClickListeners() {
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AddBookActivity.class);
            startActivity(intent);
        });
    }

    public void setUpViewPager() {
        FragmentActivity activity = getActivity();
        if(activity!=null) {
            TabFragmentPagerAdapter sectionsPagerAdapter = new TabFragmentPagerAdapter(activity.getSupportFragmentManager());

            Fragment allBooksFragment = new BooksListTabFragment(Book.STATUS_NEUTRAL);
            Fragment wantBooksFragment = new BooksListTabFragment(Book.STATUS_WANT_TO_READ);
            Fragment currBooksFragment = new BooksListTabFragment(Book.STATUS_CURRENTLY_READING);
            Fragment alrBooksFragment = new BooksListTabFragment(Book.STATUS_ALREADY_READ);

            sectionsPagerAdapter.addFragment(allBooksFragment, getString(R.string.all_books));
            sectionsPagerAdapter.addFragment(wantBooksFragment, getString(R.string.want_to_read));
            sectionsPagerAdapter.addFragment(currBooksFragment, getString(R.string.currently_reading));
            sectionsPagerAdapter.addFragment(alrBooksFragment, getString(R.string.already_read));

            books_viewPager.setAdapter(sectionsPagerAdapter);
        }
    }

}