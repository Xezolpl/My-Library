package pl.xezolpl.mylibrary.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
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

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.fragments.AllBooksFragment;
import pl.xezolpl.mylibrary.fragments.BooksListTabFragment;
import pl.xezolpl.mylibrary.fragments.CategoriesFragment;
import pl.xezolpl.mylibrary.fragments.ContactFragment;
import pl.xezolpl.mylibrary.fragments.QuotesTabFragment;
import pl.xezolpl.mylibrary.fragments.SettingsFragment;
import pl.xezolpl.mylibrary.managers.IntentManager;
import pl.xezolpl.mylibrary.managers.PermissionsManager;
import pl.xezolpl.mylibrary.managers.SettingsManager;
import spencerstudios.com.ezdialoglib.EZDialog;
import spencerstudios.com.ezdialoglib.Font;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private AllBooksFragment allBooksFragment;
    private CategoriesFragment categoriesFragment;
    private QuotesTabFragment quotesFragment;
    private SettingsFragment settingsFragment;
    private ContactFragment contactFragment;

    private EZDialog.Builder ezDialogBuilder;

    private FragmentManager fm;
    private Fragment currFragment;
    private boolean fromCategory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsManager manager = new SettingsManager(this);
        manager.loadLanguage();
        manager.loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initWidgets();
        checkPermissions();

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

        //set up exit dialog builder
        ezDialogBuilder = new EZDialog.Builder(this)
                .setTitle(getString(R.string.exit_app))
                .setMessage(getString(R.string.exit_app_msg))
                .setPositiveBtnText(getString(R.string.just_exit))
                .setNegativeBtnText(getString(R.string.lets_read))
                .OnPositiveClicked(this::finish)
                .OnNegativeClicked(() -> {})
                //stylization
                .setTitleDividerLineColor(Color.parseColor("#ed0909"))
                .setTitleTextColor(Color.parseColor("#EE311B"))
                .setButtonTextColor(Color.parseColor("#ed0909"))
                .setMessageTextColor(Color.parseColor("#333333"))
                .setFont(Font.COMFORTAA)
                .setCancelableOnTouchOutside(true);
    }

    private void initWidgets(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setCheckedItem(R.id.nav_books);
    }

    private void checkPermissions(){
        if (!PermissionsManager.checkInternetPermission(this)){
            PermissionsManager.requestInternetPermission(this);
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
                fm.beginTransaction().detach(currFragment).attach(allBooksFragment).commit();
                currFragment = allBooksFragment;
                allBooksFragment.setUpViewPager();
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

    public void setSelectedCategory(String category) {
        BooksListTabFragment booksWithCategoryFragment = new BooksListTabFragment(category);

        fm.beginTransaction().add(R.id.fragment_container, booksWithCategoryFragment)
                .detach(currFragment).commit();

        currFragment = booksWithCategoryFragment;
        fromCategory = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !drawer.isDrawerOpen(GravityCompat.START)) {
            if(fromCategory){
                fm.beginTransaction().detach(currFragment).attach(categoriesFragment).commit();
                currFragment = categoriesFragment;
                fromCategory = false;
            }else{
                ezDialogBuilder.build();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IntentManager.PICK_DATABASE){
            if(resultCode== RESULT_OK && data!=null){
                //FIRST CONFIRM WITH EZDIALOG
                Toast.makeText(this, "Successfully restored database", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, getString(R.string.restore_db_fail), Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == IntentManager.SAVE_DATABASE){
            if(resultCode == RESULT_OK && data!=null){
                String filePath = data.getData().getPath();

            }else{

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}