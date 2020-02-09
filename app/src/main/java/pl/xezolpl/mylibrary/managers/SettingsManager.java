package pl.xezolpl.mylibrary.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.util.Locale;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;

public class SettingsManager {

    private Context context;
    private SharedPreferences mPreferences;

    public SettingsManager(Context context) {
        this.context = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void loadTheme() {
        String theme = mPreferences.getString("theme", "light");
        int themeId;

        if (theme != null && theme.equals("dark")) {
            themeId = R.style.AppThemeDark;
        } else {
            themeId = R.style.AppTheme;
        }
        context.setTheme(themeId);
    }

    public void loadLanguage() {
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
        QuoteCategoryViewModel qcvm = ViewModelProviders.of((AppCompatActivity)context).get(QuoteCategoryViewModel.class);

        qcvm.getCategory("Uncategorized").observe((AppCompatActivity)context, quoteCategory -> {
            quoteCategory.setName(context.getString(R.string.uncategorized));
            qcvm.update(quoteCategory);
        });
    }
}
