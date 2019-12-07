package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.TabFragmentPagerAdapter;
import pl.xezolpl.mylibrary.models.Book;

public class BookNotesTabFragment extends Fragment {

    private TabLayout book_notes_tablayout;
    private ViewPager book_notes_viewpager;
    private Context context;
    private Book thisBook;
    private TabFragmentPagerAdapter adapter;
    private QuotesTabFragment quotesTabFragment;
    private ChaptersTabFragment chaptersTabFragment;

    public BookNotesTabFragment(Book thisBook, Context context) {
        this.thisBook = thisBook;
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new TabFragmentPagerAdapter(getFragmentManager());
        quotesTabFragment = new QuotesTabFragment(context, thisBook.getId());
        chaptersTabFragment = new ChaptersTabFragment(context, thisBook.getId());

        adapter.addFragment(chaptersTabFragment, "Chapters & Notes");
        adapter.addFragment(quotesTabFragment, "Quotes");

        setHasOptionsMenu(true);
        quotesTabFragment.setMenuVisibility(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_book_notes, container, false);
        initWidgets(view);

        book_notes_viewpager.setAdapter(adapter);
        book_notes_tablayout.setupWithViewPager(book_notes_viewpager);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if(book_notes_viewpager.getCurrentItem()==0) quotesTabFragment.setMenuVisibility(false);
        else quotesTabFragment.setMenuVisibility(true);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        quotesTabFragment.setMenuVisibility(false);
    }

    private void initWidgets(View v) {
        book_notes_tablayout = (TabLayout) v.findViewById(R.id.book_notes_tablayout);
        book_notes_viewpager = (ViewPager) v.findViewById(R.id.book_notes_viewpager);
    }
}
