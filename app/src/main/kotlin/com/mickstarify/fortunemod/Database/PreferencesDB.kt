package com.mickstarify.fortunemod.Database

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.mickstarify.fortunemod.MainActivity

/**
 * Copyright Michael Johnston 2014*
 * Created by michael on 3/12/14.
 */
class PreferencesDB(context: Context) {
    private val preferences: SharedPreferences

    init {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context)

        //        if (this.isFirstExecution()){
        //            this.initPreferences();
        //        }
    }

    fun offensiveQuotesEnabled(): Boolean {
        return this.preferences.getBoolean("allowOffensive", false)
    }

    fun initPreferences() {
        val prefEditor = this.preferences.edit()
        prefEditor.putBoolean("hasRun", true)
        prefEditor.putBoolean("allowOffensive", false)
        for (category in MainActivity.fortuneDB.getCategories()) {
            prefEditor.putBoolean("enabled-" + category, true)
            if (!MainActivity.fortuneDB.isCategoryOffensive(category)) {
                prefEditor.putBoolean("hasOffensive-" + category, false)
            } else {
                prefEditor.putBoolean("hasOffensive-" + category, true)
                prefEditor.putBoolean("offensiveEnabled-" + category, true)
            }
        }

        prefEditor.commit()
    }

    fun isCategoryEnabled(category: String): Boolean {
        return preferences.getBoolean("enabled-" + category, true)
    }

    val isFirstExecution: Boolean
        get() {
            if (this.preferences.getBoolean("hasRun", false)) {
                return false
            }
            return true
        }
}
