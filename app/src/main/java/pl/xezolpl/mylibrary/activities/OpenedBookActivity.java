package pl.xezolpl.mylibrary.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.TabFragmentPagerAdapter;
import pl.xezolpl.mylibrary.fragments.BookDetailsTabFragment;
import pl.xezolpl.mylibrary.fragments.BookNotesTabFragment;
import pl.xezolpl.mylibrary.managers.SettingsManager;
import pl.xezolpl.mylibrary.models.Book;

public class OpenedBookActivity extends AppCompatActivity {
    private TabLayout opened_book_tablayout;
    private ViewPager opened_book_viewpager;
    private Toolbar opened_book_toolbar;

    private Book thisBook = null;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new SettingsManager(this).loadTheme();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opened_book);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        thisBook = (Book) getIntent().getSerializableExtra("book");

        initWidgets();
        setUpViewPager();
    }

    private void setUpViewPager() {
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());

        Fragment bookDetailsTabFragment = new BookDetailsTabFragment(thisBook, this);
        Fragment bookNotesTabFragment = new BookNotesTabFragment(thisBook, this);

        adapter.addFragment(bookDetailsTabFragment, getString(R.string.book_details));
        adapter.addFragment(bookNotesTabFragment, getString(R.string.book_notes));

        opened_book_viewpager.setAdapter(adapter);
        opened_book_tablayout.setupWithViewPager(opened_book_viewpager);
    }

    private void initWidgets() {
        opened_book_viewpager = findViewById(R.id.opened_book_viewpager);
        opened_book_tablayout = findViewById(R.id.opened_book_tablayout);

        opened_book_toolbar = findViewById(R.id.opened_book_toolbar);
        opened_book_toolbar.setTitle(thisBook.getTitle());
        setSupportActionBar(opened_book_toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void setToolbarTitle(String title) {
        opened_book_toolbar.setTitle(title);
    }

}


