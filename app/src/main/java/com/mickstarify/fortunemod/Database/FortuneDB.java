package com.mickstarify.fortunemod.Database;

/**
 * Created by Michael on 11/10/2014.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class quoteRange {
    public int start;
    public int stop;

    public boolean hasOffensive;

    public int offensiveStart;
    public int offensiveStop;

    public quoteRange (int start, int stop){
        this.start = start;
        this.stop = stop;
        this.hasOffensive = false;

    }

    public quoteRange(int start, int stop, int offensiveStart, int offensiveStop){
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

    public static boolean allowOffensive;

    int quoteIndexStart = 1;
    int quoteIndexStop = 21089;

    Map<String, quoteRange> quoteRanges;


    public FortuneDB(Context context){
        this.quoteRanges = new HashMap<>();

        FortuneDBHelper myDBHelper;
        myDBHelper = new FortuneDBHelper(context);
        try{
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

        this.recalculateQuoteIndexes();
    }

    public List<String> getCategories(){
        List<String> categories = new ArrayList<>();
        Cursor cursor = sql_db.query("category", new String [] {"name"}, null, null, null, null, null);
        cursor.moveToFirst();
        for (int i=0;i<cursor.getCount();++i){
//            Log.v("fortune", Integer.toString(i)+cursor.toString());
            categories.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return categories;
    }

    public String getQuote (int id){
        Cursor cursor = sql_db.query("category", new String [] {"quote", "isOffensive"},
                "__id="+Integer.toString(id), null, null, null, null);
        cursor.moveToFirst();

//        if (cursor.getString(cursor.getColumnIndex("isOffensive")).equals("1") && !this.allowOffensive){
//            Log.e("fortune", "giving offensive quote when its disabled!");
//        }

        return cursor.getString(0);
    }

    public String getRandomQuote(){
        return "TODO";
    }


    public boolean isCategoryOffensive(String category){
        if (!this.quoteRanges.containsKey(category)){
            this.obtainQuoteIndexes();
        }
        return this.quoteRanges.get(category).hasOffensive;
    }

//    private boolean isCategoryOffensive (String category){
//        Cursor cursor = sql_db.rawQuery("SELECT EXISTS(SELECT * FROM quote WHERE category='"
//                + category + "' and isOffensive=1)", null);
//        cursor.moveToFirst();
//        if (cursor.getInt(0) == 1) {
//            return true;
//        }
//        return false;
//    }

    public void obtainQuoteIndexes(){
        Cursor cursor = sql_db.rawQuery("SELECT * from Category", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            if (cursor.getInt(cursor.getColumnIndex("isOffensive")) == 0){
                this.quoteRanges.put(cursor.getString(cursor.getColumnIndex("name")),
                        new quoteRange (
                                cursor.getInt(cursor.getColumnIndex("indexStart")),
                                cursor.getInt(cursor.getColumnIndex("indexStart"))
                        ));
            }
            else{
                this.quoteRanges.put(cursor.getString(cursor.getColumnIndex("name")),
                        new quoteRange (
                                cursor.getInt(cursor.getColumnIndex("indexStart")),
                                cursor.getInt(cursor.getColumnIndex("indexStop")),
                                cursor.getInt(cursor.getColumnIndex("indexOffensiveStart")),
                                cursor.getInt(cursor.getColumnIndex("indexOffensiveStop"))
                        ));
            }

            cursor.moveToNext();
        }
    }


    public void recalculateQuoteIndexes(){
        Cursor cursor = sql_db.rawQuery("SELECT min(__id), max(__id) from quote", null);
        cursor.moveToFirst();
        this.quoteIndexStart = cursor.getInt(0);
        this.quoteIndexStop = cursor.getInt(1);
        cursor.close();

        cursor = sql_db.rawQuery("SELECT min(__id), max(__id) from quote where isOffensive=1", null);
        cursor.moveToFirst();
        this.quoteRanges.put ("quotes", new quoteRange(cursor.getInt(0), cursor.getInt(1)));
        cursor.close();

        cursor = sql_db.rawQuery("SELECT min(__id), max(__id) from quote where isOffensive=0", null);
        cursor.moveToFirst();
        this.quoteRanges.get("quotes").hasOffensive = true;
        this.quoteRanges.get("quotes").offensiveStart = cursor.getInt(0);
        this.quoteRanges.get("quotes").offensiveStop = cursor.getInt(1);
        cursor.close();

        for (String category: this.getCategories()){
            cursor = sql_db.rawQuery("SELECT min(__id), max(__id) FROM quote WHERE category='"
                    + category + "' and isOffensive=0", null);
            cursor.moveToFirst();
            this.quoteRanges.put(category, new quoteRange(cursor.getInt(0), cursor.getInt(1)));
            cursor.close();
            if (this.isCategoryOffensive(category)){
                this.quoteRanges.get(category).hasOffensive = true;
                cursor = sql_db.rawQuery("SELECT min(__id), max(__id) from quote where category='"
                        + category + "' and isOffensive=1", null);
                cursor.moveToFirst();
                this.quoteRanges.get(category).offensiveStart = cursor.getInt(0);
                this.quoteRanges.get(category).offensiveStop = cursor.getInt(1);
            }

            Log.v("fortune", category + " " + this.quoteRanges.get(category).toString());
        }
    }

    //TODO load ranges
    //TODO enabled categories
}
