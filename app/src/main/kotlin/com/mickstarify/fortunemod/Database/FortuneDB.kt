package com.mickstarify.fortunemod.Database

/**
 * Copyright Michael Johnston 2014
 * Created by Michael on 11/10/2014.
 */

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.mickstarify.fortunemod.Quote
import java.util.*

internal class Category {
    var name: String
    var enabled = true
    var start: Int = 0
    var stop: Int = 0

    var hasOffensive: Boolean = false

    var offensiveStart: Int = 0
    var offensiveStop: Int = 0

    constructor(name: String, start: Int, stop: Int) {
        this.name = name
        this.start = start
        this.stop = stop
        this.hasOffensive = false

    }

    constructor(name: String, start: Int, stop: Int, offensiveStart: Int, offensiveStop: Int) {
        this.name = name
        this.start = start
        this.stop = stop
        this.hasOffensive = true
        this.offensiveStart = offensiveStart
        this.offensiveStop = offensiveStop
    }

    override fun toString(): String {
        if (this.hasOffensive) {
            return String.format("%d -> %d, %d -> %d", start, stop, offensiveStart, offensiveStop)

        }
        return String.format("%d %d", start, stop)
    }
}

class FortuneDB(context: Context) {
    private val sql_db: SQLiteDatabase
    internal var preferences: PreferencesDB

    var allowOffensive: Boolean = false

    internal var quoteIndexStart = 1
    internal var quoteIndexStop = 21089

    internal var categories: MutableList<Category>
    private var nQuotesEnabled: Int = 0

    init {
        this.categories = LinkedList<Category>()
        this.preferences = PreferencesDB(context)
        this.sql_db = QuoteDatabase(context).readableDatabase

        this.obtainQuoteIndexes()
        this.updatePreferences()
    }


    fun updatePreferences() {
        this.allowOffensive = preferences.offensiveQuotesEnabled()
        this.nQuotesEnabled = 0
        for (category in categories) {
            category.enabled = preferences.isCategoryEnabled(category.name)
            if (category.enabled) {
                this.nQuotesEnabled += this.getQuoteCount(category)
            }
        }
    }

    fun getCategories(): List<String> {
        val categories = ArrayList<String>()
        val cursor = sql_db.query("category", arrayOf("name"), null, null, null, null, null)
        cursor.moveToFirst()
        for (i in 0..cursor.count - 1) {
            categories.add(cursor.getString(0))
            cursor.moveToNext()
        }
        cursor.close()
        return categories
    }

    fun getQuote(id: Int): Quote {
        val cursor = sql_db.query("quote", arrayOf("quote", "isOffensive", "category"),
                "__id=" + Integer.toString(id), null, null, null, null)
        cursor.moveToFirst()

        val isOffensive = if (cursor.getInt(cursor.getColumnIndex("isOffensive")) == 0) false else true

        val quote = Quote(
                id,
                cursor.getString(cursor.getColumnIndex("quote")),
                cursor.getString(cursor.getColumnIndex("category")),
                isOffensive)

        Log.v("Fortune", String.format("return %d/%s", id, quote.category))

        return quote
    }

    fun getCategoryOfQuote(id: Int): String {
        val cursor = sql_db.rawQuery("SELECT category FROM quote WHERE id=" + Integer.toString(id), null)
        cursor.moveToFirst()
        return cursor.getString(0)
    }

    val randomQuote: Quote
        get() {
            if (this.nQuotesEnabled == 0) {
                return this.errorQuote
            }
            val r = Random()
            val id = r.nextInt(this.nQuotesEnabled)
            Log.v("Fortune", String.format("After %d in %d", id, this.nQuotesEnabled))

            var new_id = id
            for (category in categories) {
                if (category.enabled) {
                    if (new_id < getNonOffensiveQuoteCount(category)) {
                        new_id = category.start + new_id
                        return getQuote(new_id)
                    } else {
                        new_id -= getNonOffensiveQuoteCount(category)
                    }
                }
            }
            for (category in categories) {
                if (category.enabled) {
                    if (new_id < getOffensiveQuoteCount(category)) {
                        Log.v("Fortune", String.format("Approaching %s nid=%d s=%d, e=%d", category.name, new_id, category.offensiveStart, category.offensiveStop))
                        new_id = category.offensiveStart + new_id
                        break
                    } else {
                        new_id -= getOffensiveQuoteCount(category)
                    }
                }
            }

            return getQuote(new_id)
        }

    private fun getNonOffensiveQuoteCount(category: Category): Int {
        var total = category.stop - category.start + 1
        if (total == 1) total = 0
        return total
    }

    private fun getOffensiveQuoteCount(category: Category): Int {
        var total = 0
        if (this.allowOffensive && category.hasOffensive) {
            total += category.offensiveStop - category.offensiveStart + 1
        }
        return total
    }

    private val errorQuote: Quote
        get() {
            val q = Quote(0, "Please enable some categories", "Error", false)

            return q
        }

    internal fun getQuoteCount(category: Category): Int {
        var total = category.stop - category.start + 1
        if (total == 1) total = 0
        if (this.allowOffensive) {
            total += category.offensiveStop - category.offensiveStart + 1
        }

        return total
    }


    fun isCategoryOffensive(category: String): Boolean {
        if (this.categories.size < 1) {
            this.obtainQuoteIndexes()
            return isCategoryOffensive(category)
        }
        for (c in categories) {
            if (c.name == category) {
                return c.hasOffensive
            }
        }
        return false
    }

    fun obtainQuoteIndexes() {
        var cursor = sql_db.rawQuery("SELECT min(__id), max(__id) from quote", null)
        cursor.moveToFirst()
        this.quoteIndexStart = cursor.getInt(0)
        this.quoteIndexStop = cursor.getInt(1)
        cursor.close()

        cursor = sql_db.rawQuery("SELECT * from Category", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            if (cursor.getInt(cursor.getColumnIndex("hasOffensive")) == 0) {
                categories.add(Category(
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("indexStart")),
                        cursor.getInt(cursor.getColumnIndex("indexStop"))))
            } else {
                categories.add(Category(
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("indexStart")),
                        cursor.getInt(cursor.getColumnIndex("indexStop")),
                        cursor.getInt(cursor.getColumnIndex("indexOffensiveStart")),
                        cursor.getInt(cursor.getColumnIndex("indexOffensiveStop"))))
            }
            cursor.moveToNext()
        }
    }

    fun getNumberOfQuotes(category: String): Int {
        for (cat in categories) {
            if (cat.name == category) {
                return this.getQuoteCount(cat)
            }
        }
        return -1
    }
}
