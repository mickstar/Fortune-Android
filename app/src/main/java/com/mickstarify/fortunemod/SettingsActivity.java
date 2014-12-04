package com.mickstarify.fortunemod;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.widget.Button;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // Add a button to the header list.
//        if (hasHeaders()) {
//            Button button = new Button(this);
//            setListFooter(button);
//        }

        //PreferenceCategory categories = (PreferenceCategory) findPreference(R.id.pref_cat_categories);
        //categories.addPreference(new CheckBoxPreference());
        addPreferencesFromResource(R.xml.pref_general);
//            button.setText("Some action");

        CheckBoxPreference cbp = new CheckBoxPreference().setTitle("test");
    }

    /**
     * Populate the activity with the top-level headers.
     */

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

}