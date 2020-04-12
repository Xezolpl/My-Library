package pl.xezolpl.mylibrary.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.Locale;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.models.QuoteCategory;
import pl.xezolpl.mylibrary.utilities.Markers;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;

public class SettingsManager {

    private Context context;
    private SharedPreferences mPreferences;

    private LiveData<QuoteCategory> categoryLiveData;

    public SettingsManager(Context context) {
        this.context = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SettingsManager loadTheme() {
        String theme = mPreferences.getString("theme", "standard");
        int themeId;

        if (theme != null && theme.equals("mild")) {
            themeId = R.style.AppThemeMild;
        } else if (theme != null && theme.equals("dark")) {
            themeId = R.style.AppThemeDark;
        } else {
            themeId = R.style.AppTheme;
        }
        context.setTheme(themeId);
        return this;
    }

    public SettingsManager loadDialogTheme() {
        String theme = mPreferences.getString("theme", "standard");
        int themeId;

        if (theme != null && theme.equals("mild")) {
            themeId = R.style.AppThemeMildDialog;
        } else if (theme != null && theme.equals("dark")) {
            themeId = R.style.AppThemeDarkDialog;
        } else {
            themeId = R.style.AppThemeDialog;
        }
        context.setTheme(themeId);
        return this;
    }

    public SettingsManager loadLanguage() {
        String language = mPreferences.getString("lang", "english");
        String lang;

        if (language != null && language.equals("polish")) {
            lang = "pl";
        } else {
            lang = "en";
        }

        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        //Uncategorized
        QuoteCategoryViewModel qcvm = new ViewModelProvider((AppCompatActivity) context).get(QuoteCategoryViewModel.class);
        AppCompatActivity activity = (AppCompatActivity) context;

        categoryLiveData = qcvm.getCategory("Uncategorized");
        categoryLiveData.observe(activity, quoteCategory -> {
            if(quoteCategory!=null) {
                quoteCategory.setName(context.getString(R.string.uncategorized));
                qcvm.update(quoteCategory);
            } else {
                QuoteCategory uncategorized = new QuoteCategory("Uncategorized", context.getString(R.string.uncategorized), Markers.BLUE_START_COLOR);
                qcvm.insert(uncategorized);
            }
            categoryLiveData.removeObservers(activity);
        });

        return this;
    }

    public boolean isIntroOpenedBefore(){
        if (mPreferences==null) return false;
        return mPreferences.getBoolean("isIntroOpenedBefore", false);
    }

    public boolean isRandomColorPickingEnabled(){
        if (mPreferences==null) return false;
        return mPreferences.getBoolean("isRandomColorPickingEnabled", false);
    }


}
