package pl.xezolpl.mylibrary.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.ViewModels.BookViewModel;
import pl.xezolpl.mylibrary.adapters.BooksRecViewAdapter;
import pl.xezolpl.mylibrary.fragments.BooksTabFragment;
import pl.xezolpl.mylibrary.adapters.SectionsPagerAdapter;
import pl.xezolpl.mylibrary.dialogs.AddBookDialog;
import pl.xezolpl.mylibrary.models.Book;

public class AllBooksActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar books_toolBar;
    private FloatingActionButton fab;

    private ViewPager books_viewPager;
    private TabLayout books_tabLayout;

    private SectionsPagerAdapter sectionsPagerAdapter;
    private BooksRecViewAdapter recViewAdapter;

    private BookViewModel bookViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);

        initWidgets();
        setUpViewPager(books_viewPager);
        setOnClickListeners();
        books_tabLayout.setupWithViewPager(books_viewPager);

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        bookViewModel.getAllBooks().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {
                recViewAdapter.setBooks(books);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
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
                recViewAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    private void initWidgets() {
        books_viewPager =  (ViewPager) findViewById(R.id.books_viewPager);
        books_tabLayout = (TabLayout) findViewById(R.id.books_tabLayout);
        fab = (FloatingActionButton) findViewById(R.id.add_book_fab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            books_toolBar = (Toolbar) findViewById(R.id.books_toolBar);
            setSupportActionBar(books_toolBar);
        }
    }

    private void setOnClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: AddBookDialog as a custom class
                AddBookDialog addBookDialog = new AddBookDialog();
                addBookDialog.show(getSupportFragmentManager(),"This");
            }
        });

        books_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                recViewAdapter = ((BooksTabFragment) sectionsPagerAdapter.getItem(position)).getAdapter();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private void setUpViewPager(ViewPager viewPager) {
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(sectionsPagerAdapter);
        recViewAdapter = ((BooksTabFragment) sectionsPagerAdapter.getItem(0)).getAdapter();
    }
}
