package com.mickstarify.fortunemod.Database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mickstarify.fortunemod.MainActivity;

import java.util.HashMap;

/**
 * Created by michael on 3/12/14.
 */
public class PreferencesDB {
    private SharedPreferences preferences;

    public PreferencesDB(Context context){
       this.preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (this.isFirstExecution()){
            this.initPreferences();
        }
    }

    public boolean offensiveQuotesEnabled (){
        return this.preferences.getBoolean("allowOffensive", false);
    }

    public void initPreferences(){
        SharedPreferences.Editor prefEditor = this.preferences.edit();
        prefEditor.putBoolean("hasRun", true);
        prefEditor.putBoolean("allowOffensive", false);
        for (String category : MainActivity.myFortuneDB.getCategories()){
            prefEditor.putBoolean("enabled-"+category, true);
            if (!MainActivity.myFortuneDB.isCategoryOffensive(category)){
                prefEditor.putBoolean("hasOffensive-"+category, false);
            }
            else{
                prefEditor.putBoolean("hasOffensive-"+category, true);
                prefEditor.putBoolean("offensiveEnabled-"+category, true);
            }
        }

        prefEditor.commit();
    }

    public boolean isCategoryEnabled (String category){
        return preferences.getBoolean("enabled-"+category, true);
    }

    public boolean isFirstExecution (){
        if (this.preferences.getBoolean("hasRun", false)) {
            return false;
        }
        return true;
    }
}
