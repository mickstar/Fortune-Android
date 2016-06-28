package com.mickstarify.fortunemod.Database

import android.content.Context
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

/**
 * Created by michael on 12/02/16.
 */

class QuoteDatabase(val context: Context) : SQLiteAssetHelper(context, "quotes.db", null, 1) {

}