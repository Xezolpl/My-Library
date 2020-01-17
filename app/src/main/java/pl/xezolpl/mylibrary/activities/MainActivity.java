package pl.xezolpl.mylibrary.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.fragments.AllBooksFragment;
import pl.xezolpl.mylibrary.fragments.CategoriesFragment;
import pl.xezolpl.mylibrary.fragments.ContactFragment;
import pl.xezolpl.mylibrary.fragments.QuotesTabFragment;
import spencerstudios.com.ezdialoglib.EZDialog;
import spencerstudios.com.ezdialoglib.Font;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView nav_view;

    private AllBooksFragment allBooksFragment;
    private CategoriesFragment categoriesFragment;
    private QuotesTabFragment quotesFragment;
    private ContactFragment contactFragment;

    private EZDialog.Builder ezDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

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
            categoriesFragment = new CategoriesFragment();
            quotesFragment = new QuotesTabFragment(this, "");
            contactFragment = new ContactFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    allBooksFragment).commit();
            nav_view.setCheckedItem(R.id.nav_books);
        }

        //set up exit dialog builder
        ezDialogBuilder = new EZDialog.Builder(this)
                .setTitle(getString(R.string.exit_app))
                .setMessage(getString(R.string.exit_app_msg))
                .setPositiveBtnText(getString(R.string.just_exit))
                .setNegativeBtnText(getString(R.string.lets_read))
                .OnPositiveClicked(() -> finish())
                .OnNegativeClicked(() -> {})
                //stylization
                .setTitleDividerLineColor(Color.parseColor("#ed0909"))
                .setTitleTextColor(Color.parseColor("#EE311B"))
                .setButtonTextColor(Color.parseColor("#ed0909"))
                .setMessageTextColor(Color.parseColor("#333333"))
                .setFont(Font.COMFORTAA)
                .setCancelableOnTouchOutside(true);
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, allBooksFragment).commit();
                allBooksFragment.setUpViewPager();
                //yup setUpViewPager has to be invoked after the transaction because transaction creates the view
                break;
            }
            case R.id.nav_categories: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, categoriesFragment).commit();
                break;
            }
            case R.id.nav_quotes: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, quotesFragment).commit();
                break;
            }
            case R.id.nav_contact: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, contactFragment).commit();
                break;
            }
            default: {
                return false;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setNavViewItem(int position) {
        nav_view.setCheckedItem(position);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !drawer.isDrawerOpen(GravityCompat.START)) {
            if(nav_view.getMenu().findItem(R.id.nav_categories).isChecked() && categoriesFragment.getAdapter().isCategoryPicked()){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, categoriesFragment).commit();
            }else{
                ezDialogBuilder.build();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}