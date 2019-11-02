package pl.xezolpl.mylibrary.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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

    public BookNotesTabFragment(Book thisBook, Context context) {
        this.thisBook = thisBook;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabfragment_book_notes ,container,false);
        initWidgets(view);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getFragmentManager());
        //TODO: adapter.addFragment(OUR NEW FRAGMENT);
        adapter.addFragment(new QuotesTabFragment(context,thisBook.getId()),"Quotes");
        book_notes_viewpager.setAdapter(adapter);

        book_notes_tablayout.setupWithViewPager(book_notes_viewpager);
        return view;
    }

    private void initWidgets(View v){
        book_notes_tablayout = (TabLayout) v.findViewById(R.id.book_notes_tablayout);
        book_notes_viewpager = (ViewPager) v.findViewById(R.id.book_notes_viewpager);
    }
}
