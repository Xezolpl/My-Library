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
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import pl.xezolpl.mylibrary.R;
import pl.xezolpl.mylibrary.activities.MainActivity;
import pl.xezolpl.mylibrary.managers.IntentManager;


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
        setHasOptionsMenu(true);
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
        FragmentActivity activity = getActivity();
        if (activity != null) {
            importPref.setOnPreferenceClickListener(preference -> {
                IntentManager.pickDatabase(activity);
                return false;
            });

            exportPref.setOnPreferenceClickListener(preference -> {
                IntentManager.saveDatabase(activity);
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
        } else {
            Toast.makeText(getContext(), getString(R.string.something_went_wrong_try_again), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
    }
}