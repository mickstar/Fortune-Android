package com.mickstarify.fortunemod;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    List<CheckBoxPreference> cbp_categories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        this.cbp_categories = new LinkedList<>();

        PreferenceCategory categories = (PreferenceCategory) findPreference("Categories");
        this.addCategories(categories);


        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        MainActivity.myFortuneDB.updatePreferences();
                    }
                    // your stuff here
                };
        prefs.registerOnSharedPreferenceChangeListener(spChanged);

        Button button = new Button(this);
        button.setText("Toggle All Categories");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getBoolean("enabled-art",true)){
                    disableAllCategories();
                }
                else{
                    enableAllCategories();
                }
            }
        });

        setListFooter(button);
    }

    private void addCategories(PreferenceCategory categories) {
        for (String category : MainActivity.myFortuneDB.getCategories()){
            CheckBoxPreference cbp = new CheckBoxPreference(this);
            cbp.setTitle (String.format("%s (%d)", category, MainActivity.myFortuneDB.getNumberOfQuotes(category)));
            cbp.setKey("enabled-"+category);
            cbp.setDefaultValue(true);

            categories.addPreference(cbp);
            cbp_categories.add(cbp);
        }
    }

    protected void enableAllCategories(){
        for (CheckBoxPreference cbp : cbp_categories){
            cbp.setChecked(true);
        }
    }
    protected void disableAllCategories(){
        for (CheckBoxPreference cbp : cbp_categories){
            cbp.setChecked(false);
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