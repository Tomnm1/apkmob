package com.edu.apkmob

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyDBHandler(context: Context, name: String?,
                  factory: SQLiteDatabase. CursorFactory?, version: Int): SQLiteOpenHelper(context,
    DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "trackDB.db"
        val TABLE_NAME = "tracks"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "name"
        val COLUMN_DISTANCE= "distance"
        val COLUMN_LEVEL= "level"
        val COLUMN_DESC= "desc"
        val TIMES_TABLE_NAME = "times"
        val TIMES_COLUMN_ID = "_id"
        val TIMES_COLUMN_TRACK_ID = "track_id"
        val TIMES_COLUMN_TIME= "time"
        val TIMES_COLUMN_DATE= "dat"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME +" TEXT, "+ COLUMN_DISTANCE +" REAL," + COLUMN_LEVEL+" TEXT,"+  COLUMN_DESC +" TEXT)")
        db.execSQL(CREATE_PRODUCTS_TABLE)
        val CREATE_PRODUCTS_TABLE_2 = ("CREATE TABLE " + TIMES_TABLE_NAME + "(" + TIMES_COLUMN_ID + " INTEGER PRIMARY KEY," + TIMES_COLUMN_TRACK_ID +" INTEGER, "+ TIMES_COLUMN_TIME +" TEXT, "+  TIMES_COLUMN_DATE +" TEXT)")
        db.execSQL(CREATE_PRODUCTS_TABLE_2)
    }
    override fun onUpgrade (db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME)
        db.execSQL( "DROP TABLE IF EXISTS " + TIMES_TABLE_NAME)
        onCreate(db)
    }
    fun clear () {
        val db = this.writableDatabase
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME)
        db.execSQL( "DROP TABLE IF EXISTS " + TIMES_TABLE_NAME)
        onCreate(db)
        db.close()
    }
    /*
    fun addGame (title: String,year:Int,bggid:String,isGame:Boolean,desc:String="Brak") {
        val values = ContentValues()
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_YEAR, year)
        values.put(COLUMN_BGGID, bggid)
        values.put(COLUMN_ISGAME, isGame)
        values.put(COLUMN_DESC, desc)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun addDescription (id: Int,desc:String="Brak") {
        val values = ContentValues()
        values.put(COLUMN_DESC, desc)
        val db = this.writableDatabase
        Log.d("myApp", "where bggid = \"$id\"");
        db.update(TABLE_NAME,  values,"bggid = \"$id\"",null)
        db.close()
    }*/
    fun findTrackByLevel (level: String): ArrayList<Trail> {
        Log.d("myApp", " -- $level --");
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_LEVEL = \"$level\" "
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,  null)
        val tracks = ArrayList<Trail>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val id = Integer.parseInt(cursor.getString(0))
            val name = cursor.getString(1)
            val distance = cursor.getFloat(2)
            //val desc = cursor.getString(3)
            tracks.add(Trail (id, name, distance.toString(), level,""))
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return tracks
    }
    fun findTrackByName (nameT: String): ArrayList<Trail> {
        if(nameT=="") return ArrayList<Trail>() // Tu była zmiana
        Log.d("myApp", " -- $nameT --");
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME LIKE \"%$nameT%\" "
        Log.d("myApp", " -; $query ;-");
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,  null)
        val tracks = ArrayList<Trail>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val id = Integer.parseInt(cursor.getString(0))
            val name = cursor.getString(1)
            val distance = cursor.getFloat(2)
            val level = cursor.getString(3)
            //val desc = cursor.getString(3)
            tracks.add(Trail (id, name, distance.toString(),level,""))
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return tracks
    }
    /*
    fun findTrackByID (bggid: Int): Track? { //sprawdzić to
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_BGGID = \"$bggid\" "
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,  null)
        var game: Game? = null
        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            val title = cursor.getString(1)
            val year = cursor.getInt(2)
            val bggid = cursor.getString(3)
            val isGame = cursor.getInt(4)
            val isGameB = if(isGame==1) true else false
            game = Game (id, title, year,bggid,isGameB)
            cursor.close()
        }
        db.close()
        return game
    }*/
    fun getTrackByID (id: Int): Trail? {
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = \"$id\" "
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,  null)
        var track: Trail? = null
        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            val name = cursor.getString(1)
            val distance = cursor.getFloat(2)
            val level = cursor.getString(3)
            val desc = cursor.getString(4)
            track = Trail (id, name, distance.toString(),level,desc)
            cursor.close()
        }
        db.close()
        return track
    }
    fun addTime (trackID:Int,time:String,date:String) {
        val values = ContentValues()
        values.put(TIMES_COLUMN_TRACK_ID, trackID)
        values.put(TIMES_COLUMN_TIME, time)
        values.put(TIMES_COLUMN_DATE, date)
        val db = this.writableDatabase
        db.insert(TIMES_TABLE_NAME, null, values)
        db.close()
    }
//    fun getTimesForTrack (id: Int): ArrayList<TimeEle> {
//        val query = "SELECT * FROM $TIMES_TABLE_NAME WHERE $TIMES_COLUMN_TRACK_ID = \"$id\" "
//        val db = this.writableDatabase
//        val cursor = db.rawQuery(query,  null)
//        val times = ArrayList<TimeEle>()
//        cursor.moveToFirst()
//        while (!cursor.isAfterLast()) {
//            val id = cursor.getInt(0)
//            val time = cursor.getString(2)
//            val date = cursor.getString(3)
//            //val desc = cursor.getString(3)
//            times.add(TimeEle(id,time,date))
//            cursor.moveToNext()
//        }
//        Log.i("size - data",times.size.toString())
//        cursor.close()
//        db.close()
//        return times
//    }
    fun getLastTimeId():Int{
        val query = "SELECT $TIMES_COLUMN_ID FROM $TIMES_TABLE_NAME ORDER BY $TIMES_COLUMN_ID DESC "
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,  null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
            cursor.close()
        }
        db.close()
        return count+1
    }
    fun delTime(id:Int){
        val db = this.writableDatabase
        db.execSQL( "DELETE FROM $TIMES_TABLE_NAME WHERE $TIMES_COLUMN_ID = " + id)
        db.close()
    }
    /*
    fun findHowMany (isGame: Boolean): Int { //sprawdzić to
        var x:Int
        if(isGame) x = 1
        else x = 0
        val query = "SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_ISGAME = $x "
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,  null)
        var number:Int = 0
        if (cursor.moveToFirst()) {
            number = cursor.getInt(0)
            cursor.close()
        }
        db.close()
        return number
    }
    fun getDescription (id:Int): String { //sprawdzić to
        val query = "SELECT $COLUMN_DESC FROM $TABLE_NAME WHERE $COLUMN_BGGID =  \"$id\" "
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,  null)
        var str:String = ""
        if (cursor.moveToFirst()) {
            str = cursor.getString(0)
            cursor.close()
        }
        db.close()
        return str
    }*/
}