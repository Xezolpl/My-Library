package pl.xezolpl.mylibrary.activities;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.BooksTabFragment;
import pl.xezolpl.mylibrary.adapters.SectionsPagerAdapter;
import pl.xezolpl.mylibrary.models.Status;

public class AllBooksActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private EditText search_filter_textEdt;
    private androidx.appcompat.widget.Toolbar toolBar;
    private ViewPager view_pager;
    private boolean withToolBar = false;
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);
        initWidgets();

        //display tabs
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setUpViewPager(view_pager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(view_pager);

    }

    private void initWidgets() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        search_filter_textEdt = (EditText) findViewById(R.id.search_filter_textEdt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolBar = (Toolbar) findViewById(R.id.toolBar);
            withToolBar=true;
            setSupportActionBar(toolBar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_filter:{
                search_filter_textEdt.setVisibility(View.VISIBLE);
                search_filter_textEdt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        ArrayAdapter myAdapter = new ArrayAdapter(AllBooksActivity.this,R.layout.listitem_book_rec_view,
                        ((BooksTabFragment)sectionsPagerAdapter.getItem(view_pager.getCurrentItem())).getAdapter().getBooks());
                        myAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                break;
            }
            case R.id.more_btn:{
                //
                break;
            }

        }
        return true;
    }

    private void setUpViewPager(ViewPager viewPager){
        sectionsPagerAdapter.addFragment(new BooksTabFragment(this, Status.NEUTRAL), "All books");
        sectionsPagerAdapter.addFragment(new BooksTabFragment(this, Status.WANT_TO_READ), "Want to read books");
        sectionsPagerAdapter.addFragment(new BooksTabFragment(this, Status.CURRENTLY_READING), "Currently reading books");
        sectionsPagerAdapter.addFragment(new BooksTabFragment(this, Status.ALREADY_READ), "Already read books");
        viewPager.setAdapter(sectionsPagerAdapter);
    }
}
