package com.mickstarify.fortunemod;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        PreferenceCategory categories = (PreferenceCategory) findPreference("Categories");

        this.addCategories(categories);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        MainActivity.myFortuneDB.updatePreferences();
                        Log.v("Fortune", key);
                    }
                    // your stuff here
                };
        prefs.registerOnSharedPreferenceChangeListener(spChanged);
    }

    private void addCategories(PreferenceCategory categories) {
        for (String category : MainActivity.myFortuneDB.getCategories()){
            CheckBoxPreference cbp = new CheckBoxPreference(this);
            cbp.setTitle (String.format("%s (%d)", category, MainActivity.myFortuneDB.getNumberOfQuotes(category)));
            cbp.setKey("enabled-"+category);
            cbp.setDefaultValue(true);

            categories.addPreference(cbp);
        }
    }

    /**
     * Populate the activity with the top-level headers.
     */

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

}