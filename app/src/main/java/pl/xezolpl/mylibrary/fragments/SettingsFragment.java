package pl.xezolpl.mylibrary.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.MainActivity;
import pl.xezolpl.mylibrary.managers.IntentManager;
import pl.xezolpl.mylibrary.managers.SettingsManager;


public class SettingsFragment extends PreferenceFragmentCompat {
    private Preference importPref, exportPref;
    private ListPreference langListPref, themeListPref;

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;

    private SettingsManager settingsManager;

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, null);
        preferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        prefEditor = preferences.edit();
        settingsManager = new SettingsManager(Objects.requireNonNull(getContext()));

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
        themeListPref.setValue(preferences.getString("theme", "standard"));
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
            prefEditor.putString("lang", newValue.toString()).commit();
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            return true;
        });

        themeListPref.setOnPreferenceChangeListener((preference, newValue) -> {
            prefEditor.putString("theme", newValue.toString()).commit();
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            return true;
        });
    }


}