package com.mickstarify.fortunemod.Database;

/**
 * Created by Michael on 11/10/2014.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mickstarify.fortunemod.Quote;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

class Category {
    String name;
    public boolean enabled = true;
    public int start;
    public int stop;

    public boolean hasOffensive;

    public int offensiveStart;
    public int offensiveStop;

    public Category (String name, int start, int stop){
        this.name = name;
        this.start = start;
        this.stop = stop;
        this.hasOffensive = false;

    }

    public Category(String name, int start, int stop, int offensiveStart, int offensiveStop){
        this.name = name;
        this.start = start;
        this.stop = stop;
        this.hasOffensive = true;
        this.offensiveStart = offensiveStart;
        this.offensiveStop = offensiveStop;
    }

    public String toString(){
        if (this.hasOffensive){
            return String.format("%d -> %d, %d -> %d", start, stop, offensiveStart, offensiveStop);

        }
        return String.format("%d %d", start, stop);
    }
}

public class FortuneDB {
    private SQLiteDatabase sql_db;
    PreferencesDB preferences;

    public boolean allowOffensive;

    int quoteIndexStart = 1;
    int quoteIndexStop = 21089;

    List<Category> categories;
    private int nQuotesEnabled;

    public FortuneDB(Context context){
        this.categories = new LinkedList<>();
        this.preferences = new PreferencesDB(context);
        FortuneDBHelper myDBHelper;
        myDBHelper = new FortuneDBHelper(context);

        try{
            myDBHelper.performUpdateIfNeeded(1);
            myDBHelper.createDataBase();
        }
        catch (IOException e){
            throw new Error ("Unable to create Database");
        }

        try {
            myDBHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.sql_db = myDBHelper.getReadableDatabase();


        this.obtainQuoteIndexes();
        this.updatePreferences();
    }


    public void updatePreferences() {
        this.allowOffensive = preferences.offensiveQuotesEnabled();
        this.nQuotesEnabled = 0;
        for (Category category: categories){
            category.enabled = preferences.isCategoryEnabled(category.name);
            if (category.enabled){
                this.nQuotesEnabled += this.getQuoteCount(category);
            }
        }
    }

    public List<String> getCategories(){
        List<String> categories = new ArrayList<>();
        Cursor cursor = sql_db.query("category", new String [] {"name"}, null, null, null, null, null);
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();++i){
            categories.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return categories;
    }

    public Quote getQuote (int id){
        Cursor cursor = sql_db.query("quote", new String [] {"quote", "isOffensive","category"},
                "__id="+Integer.toString(id), null, null, null, null);
        cursor.moveToFirst();

        boolean isOffensive = (cursor.getInt(cursor.getColumnIndex("isOffensive")) == 0) ? false : true;

        Quote quote = new Quote (
                id,
                cursor.getString(cursor.getColumnIndex("quote")),
                cursor.getString(cursor.getColumnIndex("category")),
                isOffensive

        );

        Log.v("Fortune",String.format("return %d/%s",id,quote.category));

        return quote;
    }

    public String getCategoryOfQuote (int id){
        Cursor cursor = sql_db.rawQuery("SELECT category FROM quote WHERE id="+Integer.toString(id), null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public Quote getRandomQuote(){
        if (this.nQuotesEnabled == 0){
            return this.getErrorQuote();
        }
        Random r = new Random();
        int id = r.nextInt(this.nQuotesEnabled);
        Log.v("Fortune", String.format("After %d in %d", id, this.nQuotesEnabled));

        int new_id = id;
        for (Category category : categories){
            if (category.enabled){
                if (new_id < getNonOffensiveQuoteCount(category)){
                    new_id = category.start+new_id;
                    return getQuote(new_id);
                }
                else{
                    new_id -= getNonOffensiveQuoteCount(category);
                }
            }
        }
        for (Category category : categories){
            if (category.enabled){
                if (new_id < getOffensiveQuoteCount(category)){
                    Log.v("Fortune",String.format("Approaching %s nid=%d s=%d, e=%d",category.name,new_id,category.offensiveStart,category.offensiveStop));
                    new_id = category.offensiveStart+new_id;
                    break;
                }
                else{
                    new_id -= getOffensiveQuoteCount(category);
                }
            }
        }

        return getQuote(new_id);
    }

    private int getNonOffensiveQuoteCount(Category category) {
        int total = category.stop - category.start + 1;
        if (total == 1) total = 0;
        return total;
    }
    private int getOffensiveQuoteCount(Category category) {
        int total = 0;
        if (this.allowOffensive && category.hasOffensive){
            total += (category.offensiveStop - category.offensiveStart + 1);
        }
        return total;
    }

    private Quote getErrorQuote (){
        Quote q = new Quote(0, "Please enable some categories", "Error", false);

        return q;
    }

    public int getQuoteCount(Category category) {
        int total = category.stop - category.start + 1;
        if (total == 1) total = 0;
        if (this.allowOffensive){
            total += (category.offensiveStop - category.offensiveStart + 1);
        }

        return total;
    }


    public boolean isCategoryOffensive(String category){
        if (this.categories.size() < 1){
            this.obtainQuoteIndexes();
            return isCategoryOffensive(category);
        }
        for (Category c : categories){
            if (c.name.equals(category)){
                return c.hasOffensive;
            }
        }
        return false;
    }

    public void obtainQuoteIndexes(){
        Cursor cursor = sql_db.rawQuery("SELECT min(__id), max(__id) from quote", null);
        cursor.moveToFirst();
        this.quoteIndexStart = cursor.getInt(0);
        this.quoteIndexStop = cursor.getInt(1);
        cursor.close();

        cursor = sql_db.rawQuery("SELECT * from Category", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (cursor.getInt(cursor.getColumnIndex("hasOffensive")) == 0) {
                categories.add(new Category(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("indexStart")),
                    cursor.getInt(cursor.getColumnIndex("indexStop"))
                ));
            } else {
                categories.add(new Category(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("indexStart")),
                    cursor.getInt(cursor.getColumnIndex("indexStop")),
                    cursor.getInt(cursor.getColumnIndex("indexOffensiveStart")),
                    cursor.getInt(cursor.getColumnIndex("indexOffensiveStop"))
                ));
            }
            cursor.moveToNext();
        }
    }

    public int getNumberOfQuotes(String category) {
        for (Category cat: categories){
            if (cat.name.equals(category)){
                return this.getQuoteCount(cat);
            }
        }
        return -1;
    }
    //TODO enabled categories
}
