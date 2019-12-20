package pl.xezolpl.mylibrary.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.fragments.ContactFragment;
import pl.xezolpl.mylibrary.fragments.AllBooksFragment;
import pl.xezolpl.mylibrary.fragments.QuotesTabFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private AllBooksFragment allBooksFragment;
    private NavigationView nav_view;
    private int checkedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
         nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            allBooksFragment = new AllBooksFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    allBooksFragment).commit();
            nav_view.setCheckedItem(R.id.nav_books);
            checkedItem=0;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_books: {
                allBooksFragment.setUpViewPager();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, allBooksFragment).commit();
                checkedItem=0;
                break;
            }
            case R.id.nav_quotes: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new QuotesTabFragment(MainActivity.this,"")).commit();
                checkedItem=3;
                break;
            }
            case R.id.nav_contact: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactFragment()).commit();
                checkedItem=5;
                break;
            }
            default:{
                return false;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}