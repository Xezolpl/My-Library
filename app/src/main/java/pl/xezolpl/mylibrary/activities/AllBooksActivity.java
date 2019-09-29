package pl.xezolpl.mylibrary.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.adapters.BooksRecViewAdapter;
import pl.xezolpl.mylibrary.adapters.BooksTabFragment;
import pl.xezolpl.mylibrary.adapters.SectionsPagerAdapter;
import pl.xezolpl.mylibrary.models.Status;

public class AllBooksActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private androidx.appcompat.widget.Toolbar toolBar;
    private ViewPager view_pager;
    private boolean withToolBar = false;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private BooksRecViewAdapter recViewAdapter;
    private BooksTabFragment allBooksFragment,wantToReadBooksFragment,
            currReadingBooksFragment,alreadyReadBooksFragment;
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
        //
        recViewAdapter = allBooksFragment.getAdapter();

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (view_pager.getCurrentItem()){
                    case 0: {
                        recViewAdapter = allBooksFragment.getAdapter(); break;
                    }
                    case 1: {
                        recViewAdapter = wantToReadBooksFragment.getAdapter(); break;
                    }
                    case 2: {
                        recViewAdapter = currReadingBooksFragment.getAdapter(); break;
                    }
                    case 3: {
                        recViewAdapter = alreadyReadBooksFragment.getAdapter(); break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initWidgets() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
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


    private void setUpViewPager(ViewPager viewPager){
        initTabFragments();
        sectionsPagerAdapter.addFragment(allBooksFragment, "All books");
        sectionsPagerAdapter.addFragment(wantToReadBooksFragment, "Want to read books");
        sectionsPagerAdapter.addFragment(currReadingBooksFragment, "Currently reading books");
        sectionsPagerAdapter.addFragment(alreadyReadBooksFragment, "Already read books");
        viewPager.setAdapter(sectionsPagerAdapter);
    }
    private void initTabFragments(){
         allBooksFragment = new BooksTabFragment(this, Status.NEUTRAL);
         wantToReadBooksFragment = new BooksTabFragment(this, Status.WANT_TO_READ);
         currReadingBooksFragment = new BooksTabFragment(this, Status.CURRENTLY_READING);
         alreadyReadBooksFragment = new BooksTabFragment(this, Status.ALREADY_READ);
    }
}
