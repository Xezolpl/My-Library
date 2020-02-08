package pl.xezolpl.mylibrary.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.lifecycle.ViewModelProviders;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;
import java.util.Objects;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.MainActivity;
import pl.xezolpl.mylibrary.managers.IntentManager;
import pl.xezolpl.mylibrary.viewmodels.QuoteCategoryViewModel;


public class SettingsFragment extends PreferenceFragmentCompat {
    private Preference importPref, exportPref;
    private ListPreference langListPref, themeListPref;

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, null);
        preferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        prefEditor = preferences.edit();

        initPreferences();
        loadPreferencesState();
        setListeners();
    }

    private void initPreferences() {
        importPref = findPreference("import");
        exportPref = findPreference("export");
        langListPref = findPreference("lang");
        themeListPref = findPreference("theme");
    }

    private void loadPreferencesState() {
        langListPref.setValue(preferences.getString("lang", "english"));
        themeListPref.setValue(preferences.getString("theme", "light"));
    }

    private void setListeners() {
        importPref.setOnPreferenceClickListener(preference -> {
            IntentManager.pickDatabase(Objects.requireNonNull(getActivity()));
            return false;
        });

        exportPref.setOnPreferenceClickListener(preference -> {
            IntentManager.saveDatabase(Objects.requireNonNull(getActivity()));
            return false;
        });

        langListPref.setOnPreferenceChangeListener((preference, newValue) -> {
            setLocale(newValue.toString());
            prefEditor.putString("lang", newValue.toString()).commit();
            return true;
        });

        themeListPref.setOnPreferenceChangeListener((preference, newValue) -> {
            setTheme(newValue.toString());
            prefEditor.putString("theme", newValue.toString()).commit();
            return true;
        });
    }

    private void setLocale(String lang) {

        //Set uncategorized to current language
        QuoteCategoryViewModel qcvm = ViewModelProviders.of(this).get(QuoteCategoryViewModel.class);

        qcvm.getCategory("Uncategorized").observe(this, quoteCategory -> {
            quoteCategory.setName(getString(R.string.uncategorized));
            qcvm.update(quoteCategory);
        });
        //"Uncategorized" not as a getString() because its id, and here it cannot be translatable for other languages

        if (lang.equals("polish")) {
            lang = "pl";
        } else {
            lang = "en";

        }
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(getContext(), MainActivity.class);
        Objects.requireNonNull(getActivity()).finish();
        startActivity(refresh);
    }

    private void setTheme(String theme) {
        Intent refresh = new Intent(getContext(), MainActivity.class);
        refresh.putExtra("theme", theme);
        Objects.requireNonNull(getActivity()).finish();
        startActivity(refresh);
    }
}