package pl.xezolpl.mylibrary.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.TabFragmentPagerAdapter;
import pl.xezolpl.mylibrary.fragments.BookDetailsTabFragment;
import pl.xezolpl.mylibrary.fragments.BookNotesTabFragment;
import pl.xezolpl.mylibrary.models.Book;

public class OpenedBookActivity extends AppCompatActivity {
    private static final String TAG = "OpenedBookActivity";

    public static final int RESULT_DELETE=2;
    private Toolbar opened_book_toolbar;
    private TabLayout opened_book_tablayout;
    private ViewPager opened_book_viewpager;

    private Book thisBook = null;

    private Context getContext() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_book);

        Intent intent = getIntent();
        thisBook = (Book) intent.getSerializableExtra("book");

        initWidgets();

        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());

        Fragment bookDetailsTabFragment = new BookDetailsTabFragment(thisBook,this);
        Fragment bookNotesTabFragment = new BookNotesTabFragment(thisBook,this);

        adapter.addFragment(bookDetailsTabFragment,"Book's details");
        adapter.addFragment(bookNotesTabFragment,"Book's notes");

        opened_book_viewpager.setAdapter(adapter);
        opened_book_tablayout.setupWithViewPager(opened_book_viewpager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            this.setResult(RESULT_OK,data);
            finish();
        }
    }

    private void initWidgets() {
        opened_book_viewpager = (ViewPager) findViewById(R.id.opened_book_viewpager);
        opened_book_tablayout = (TabLayout) findViewById(R.id.opened_book_tablayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            opened_book_toolbar = (Toolbar) findViewById(R.id.opened_book_toolbar);
            opened_book_toolbar.setTitle(thisBook.getTitle());
            setSupportActionBar(opened_book_toolbar);
        }
    }
}


