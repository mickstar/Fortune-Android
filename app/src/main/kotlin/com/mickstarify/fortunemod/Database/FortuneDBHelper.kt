package com.mickstarify.fortunemod.Database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.sql.SQLException

/**
 * This code was mostly copied from stackexchange
 * and as such is released with my modifications under the originating CC license
 * Created by michael on 29/11/14.
 */
class FortuneDBHelper
/**
 * Constructor
 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.

 * @param context
 */
(private val myContext: Context) : SQLiteOpenHelper(myContext, FortuneDBHelper.DB_NAME, null, 1) {

    private var myDataBase: SQLiteDatabase? = null

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    @Throws(IOException::class)
    fun createDataBase() {

        val dbExist = checkDataBase()

        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.readableDatabase

            try {
                copyDataBase()
            } catch (e: IOException) {
                throw Error("Error copying database")
            }

        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.

     * @return true if it exists, false if it doesn't
     */
    private fun checkDataBase(): Boolean {

        var checkDB: SQLiteDatabase? = null

        try {
            val myPath = DB_PATH + DB_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

        } catch (e: SQLiteException) {
            //database doesn't exist yet.
        }

        if (checkDB != null) {
            checkDB.close()
        }

        return if (checkDB != null) true else false
    }

    /**
     * Check database version
     */
    @Throws(SQLiteException::class)
    private fun checkDatabaseVersion(): Int {

        var checkDB: SQLiteDatabase? = null
        val myPath = DB_PATH + DB_NAME
        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)
            val cursor = checkDB!!.rawQuery("SELECT max(number) from Version", null)
            cursor.moveToFirst()
            return cursor.getInt(cursor.getColumnIndex("number"))
        } catch (e: Exception) {
            return -1
        }

    }

    fun performUpdateIfNeeded(bundledVersion: Int) {
        try {
            val v = this.checkDatabaseVersion()
            if (bundledVersion > v) {
                this.replaceDatabase()
            }
        } catch (e: SQLiteException) {
            this.replaceDatabase()
        }

    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    @Throws(IOException::class)
    private fun copyDataBase() {

        //Open your local db as the input stream
        val myInput = myContext.assets.open(DB_NAME)

        // Path to the just created empty db
        val outFileName = DB_PATH + DB_NAME

        //Open the empty db as the output stream
        val myOutput = FileOutputStream(outFileName)

        //transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length)
        }

        //Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()

    }

    fun replaceDatabase() {
        val dbExists = this.checkDataBase()
        if (dbExists) {
            val file = File(DB_PATH + DB_NAME)
            file.delete()
        }
        try {
            this.createDataBase()
        } catch (e: IOException) {
        }

    }

    @Throws(SQLException::class)
    fun openDataBase() {

        //Open the database
        val myPath = DB_PATH + DB_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

    }

    @Synchronized override fun close() {

        if (myDataBase != null)
            myDataBase!!.close()

        super.close()

    }

    override fun onCreate(db: SQLiteDatabase) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {

        //The Android's default system path of your application database.
        private val DB_PATH = "/data/data/com.mickstarify.fortunemod/databases/"

        private val DB_NAME = "quotes.db"
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}
