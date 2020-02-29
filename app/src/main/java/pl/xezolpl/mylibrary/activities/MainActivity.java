package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.Objects;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.fragments.AllBooksFragment;
import pl.xezolpl.mylibrary.fragments.BooksListTabFragment;
import pl.xezolpl.mylibrary.fragments.CategoriesFragment;
import pl.xezolpl.mylibrary.fragments.ContactFragment;
import pl.xezolpl.mylibrary.fragments.QuotesTabFragment;
import pl.xezolpl.mylibrary.fragments.SettingsFragment;
import pl.xezolpl.mylibrary.managers.BackupManager;
import pl.xezolpl.mylibrary.managers.IntentManager;
import pl.xezolpl.mylibrary.managers.SettingsManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout drawer;

    private FragmentManager fm;
    private Fragment currFragment;

    private AllBooksFragment allBooksFragment;
    private CategoriesFragment categoriesFragment;
    private QuotesTabFragment quotesFragment;
    private SettingsFragment settingsFragment;
    private ContactFragment contactFragment;

    private int backCounter = 0;
    private boolean fromCategory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Load theme and language
        SettingsManager manager = new SettingsManager(this);
        manager.loadLanguage();
        manager.loadTheme();

        //Check does standard book's cover image exists
        String imageUrl = getApplicationInfo().dataDir + "/files/covers/standard_cover.jpg";
        if (!new File(imageUrl).exists()){
            BackupManager.downloadCover(imageUrl);
        }


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initWidgets();

        //Set up the drawer layout and its fragment
        if (savedInstanceState == null) {
            allBooksFragment = new AllBooksFragment();
            categoriesFragment = new CategoriesFragment();
            quotesFragment = new QuotesTabFragment(this, "");
            settingsFragment = new SettingsFragment();
            contactFragment = new ContactFragment();

            fm = getSupportFragmentManager();

            fm.beginTransaction()
                    .add(R.id.fragment_container, allBooksFragment)
                    .add(R.id.fragment_container, categoriesFragment)
                    .add(R.id.fragment_container, quotesFragment)
                    .add(R.id.fragment_container, settingsFragment)
                    .add(R.id.fragment_container, contactFragment)

                    .detach(categoriesFragment)
                    .detach(quotesFragment)
                    .detach(settingsFragment)
                    .detach(contactFragment)

                    .commit();

            currFragment = allBooksFragment;
        }
    }

    private void initWidgets() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        nav_view.setCheckedItem(R.id.nav_books);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Navigation between the fragments
     * @param menuItem drawer's tab clicked - corresponding to the specific frame's fragment
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_books: {
                fm.beginTransaction().detach(currFragment).attach(allBooksFragment).commit();
                currFragment = allBooksFragment;
                allBooksFragment.setUpViewPager(); //because the listItems weren't showing the books
                break;
            }
            case R.id.nav_categories: {
                fm.beginTransaction().detach(currFragment).attach(categoriesFragment).commit();
                currFragment = categoriesFragment;
                break;
            }
            case R.id.nav_quotes: {
                fm.beginTransaction().detach(currFragment).attach(quotesFragment).commit();
                currFragment = quotesFragment;
                break;
            }
            case R.id.nav_settings: {
                fm.beginTransaction().detach(currFragment).attach(settingsFragment).commit();
                currFragment = settingsFragment;
                break;
            }
            case R.id.nav_contact: {
                fm.beginTransaction().detach(currFragment).attach(contactFragment).commit();
                currFragment = contactFragment;
                break;
            }
            default: {
                return false;
            }
        }
        fromCategory = false;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Sets currentFragment to booksListTabFragment of categoryFragment - we can
     * se books attributed to the specific category
     * @param category specific category
     */
    public void setSelectedCategory(String category) {
        BooksListTabFragment booksWithCategoryFragment = new BooksListTabFragment(category);

        fm.beginTransaction().add(R.id.fragment_container, booksWithCategoryFragment)
                .detach(currFragment).commit();

        currFragment = booksWithCategoryFragment;
        fromCategory = true;
    }

    /**
     * If keycode is KEYCODE_BACK -> if we are in category books - we returns to the categoriesFragment
     * else first shows toast, on the second key back click - close the application
     * @return true if KEYCODE_BACK else super
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !drawer.isDrawerOpen(GravityCompat.START)) {
            if (fromCategory) { //we are in the categoriesFragment
                fm.beginTransaction().detach(currFragment).attach(categoriesFragment).commit();
                currFragment = categoriesFragment;
                fromCategory = false;
            } else {
                if (backCounter == 0) { //first backClick - the second time application will be closed (after 2s counter resets)
                    backCounter = 1;
                    Toast.makeText(this, getString(R.string.exit_app), Toast.LENGTH_SHORT).show();
                    (new Handler()).postDelayed(() -> backCounter = 0, 2000);
                } else {
                    backCounter = 0;
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //IMPORT THE DATABASE
        if (requestCode == IntentManager.PICK_DATABASE) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    File file = new File(Objects.requireNonNull(data.getData()).getPath());

                    if (new BackupManager(this).importDatabaseFile(file)) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, getString(R.string.db_restore_unknown_error), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.restore_db_fail), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onActivityResult: ", e);
                }
            } else {
                Toast.makeText(this, getString(R.string.restore_db_fail), Toast.LENGTH_SHORT).show();
            }
        }

        //EXPORT THE DATABASE
        else if (requestCode == IntentManager.SAVE_DATABASE) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    if (new BackupManager(this).exportDatabaseFile(new File(Objects.requireNonNull(data.getData()).getPath()))) {
                        Toast.makeText(this, getString(R.string.export_db_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.export_db_fail), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.export_db_fail) + "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.export_db_fail), Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}