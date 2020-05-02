package pl.xezolpl.mylibrary.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.IntroActivity;
import pl.xezolpl.mylibrary.activities.MainActivity;
import pl.xezolpl.mylibrary.managers.IntentManager;
import pl.xezolpl.mylibrary.managers.PermissionsManager;


public class SettingsFragment extends PreferenceFragmentCompat {
    private Preference importPref, exportPref, tutorialPref;
    private ListPreference langListPref, themeListPref;
    private CheckBoxPreference randomColorCheckboxPref;

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey);

        preferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        prefEditor = preferences.edit();

        initPreferences();
        setListeners();
        setHasOptionsMenu(true);
    }

    private void initPreferences() {
        importPref = findPreference("import");
        exportPref = findPreference("export");
        tutorialPref = findPreference("tutorial");
        langListPref = findPreference("lang");
        themeListPref = findPreference("theme");
        randomColorCheckboxPref = findPreference("isRandomColorPickingEnabled");
    }

    private void setListeners() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            importPref.setOnPreferenceClickListener(preference -> {
                if(PermissionsManager.checkStoragePermission(activity)){
                    IntentManager.pickDatabase(activity);
                } else {
                    PermissionsManager.requestStoragePermission(activity);
                }
                return false;
            });

            exportPref.setOnPreferenceClickListener(preference -> {
                IntentManager.saveDatabase(activity);
                return false;
            });

            tutorialPref.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getContext(), IntroActivity.class));
                return false;
            });

            langListPref.setOnPreferenceChangeListener((preference, newValue) -> {
                prefEditor.putString("lang", newValue.toString()).commit();
                Intent intent = new Intent(getContext(), MainActivity.class);
                activity.finish();
                startActivity(intent);
                return true;
            });

            themeListPref.setOnPreferenceChangeListener((preference, newValue) -> {
                prefEditor.putString("theme", newValue.toString()).commit();
                Intent intent = new Intent(getContext(), MainActivity.class);
                activity.finish();
                startActivity(intent);
                return true;
            });

            randomColorCheckboxPref.setOnPreferenceChangeListener((preference, newValue) -> {
                prefEditor.putBoolean("isRandomColorPickingEnabled", randomColorCheckboxPref.isChecked()).commit();
                return true;
            });
        } else {
            Toast.makeText(getContext(), getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
    }

    public void setLanguagePref(String languagePref){

    }
}