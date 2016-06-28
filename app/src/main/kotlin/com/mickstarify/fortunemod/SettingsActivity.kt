package com.mickstarify.fortunemod

// Copyright Michael Johnston 2014

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.PreferenceActivity
import android.preference.PreferenceCategory
import android.preference.PreferenceManager
import android.widget.Button
import java.util.*

class SettingsActivity : PreferenceActivity() {
    lateinit var cbp_categories: MutableList<CheckBoxPreference>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_general)

        this.cbp_categories = LinkedList<CheckBoxPreference>()

        val categories = findPreference("Categories") as PreferenceCategory
        this.addCategories(categories)


        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val spChanged = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            MainActivity.fortuneDB.updatePreferences()
        }
        // your stuff here
        prefs.registerOnSharedPreferenceChangeListener(spChanged)

        val button = Button(this)
        button.text = "Toggle All Categories"
        button.setOnClickListener {
            if (prefs.getBoolean("enabled-art", true)) {
                disableAllCategories()
            } else {
                enableAllCategories()
            }
        }

        setListFooter(button)
    }

    private fun addCategories(categories: PreferenceCategory) {
        for (category in MainActivity.fortuneDB.getCategories()) {
            val cbp = CheckBoxPreference(this)
            cbp.title = String.format("%s (%d)", category, MainActivity.fortuneDB.getNumberOfQuotes(category))
            cbp.key = "enabled-" + category
            cbp.setDefaultValue(true)

            categories.addPreference(cbp)
            cbp_categories.add(cbp)
        }
    }

    protected fun enableAllCategories() {
        for (cbp in cbp_categories) {
            cbp.isChecked = true
        }
    }

    protected fun disableAllCategories() {
        for (cbp in cbp_categories) {
            cbp.isChecked = false
        }
    }

    /**
     * Populate the activity with the top-level headers.
     */

    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

}
