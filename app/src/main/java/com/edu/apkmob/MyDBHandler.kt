package com.example.myapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.edu.apkmob.Trail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    private val dbPath: String = context.getDatabasePath(DATABASE_NAME).absolutePath
    private val appContext: Context = context

    init {
        Log.d("Database Path", "DB Path: $dbPath")
        if (!checkDatabase()) {
            this.writableDatabase.close()
            try {
                copyDatabase()
            } catch (e: IOException) {
                throw Error("Error copying database: ${e.message}")
            }
        }
    }

    private fun checkDatabase(): Boolean {
        var checkDB: SQLiteDatabase? = null
        try {
            val path = dbPath
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: SQLiteException) {
            Log.e("Database Error", "Database does not exist yet: ${e.message}")
        }
        checkDB?.close()
        return checkDB != null
    }

    @Throws(IOException::class)
    private fun copyDatabase() {
        Log.d("Database Copy", "Copying database from assets...")
        val input: InputStream = appContext.assets.open(DATABASE_NAME)
        val output: OutputStream = FileOutputStream(dbPath)

        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }

        output.flush()
        output.close()
        input.close()
        Log.d("Database Copy", "Database copied successfully!")
    }

    @Synchronized
    override fun close() {
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Do nothing here
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrade if needed
    }

    suspend fun loadTrailsFromDB(): List<Trail> {
        val trails = mutableListOf<Trail>()
        withContext(Dispatchers.IO) {
            val cursor = readableDatabase.rawQuery("SELECT * FROM Trail", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val distance = cursor.getInt(cursor.getColumnIndexOrThrow("distance"))
                    val level = cursor.getString(cursor.getColumnIndexOrThrow("level"))
                    val desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"))
                    trails.add(Trail(id, name, distance, level, desc))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return trails
    }

    fun findTrailById(id: Int): Trail? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Trail WHERE id = ?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            val trail = Trail(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                distance = cursor.getInt(cursor.getColumnIndexOrThrow("distance")),
                level = cursor.getString(cursor.getColumnIndexOrThrow("level")),
                desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"))
            )
            cursor.close()
            trail
        } else {
            cursor.close()
            null
        }
    }
    fun getTrailsByDifficulty(difficulty: String): List<Trail> {
        val trails = mutableListOf<Trail>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM Trail WHERE level = ?", arrayOf(difficulty))
        if (cursor.moveToFirst()) {
            do {
                val trail = Trail(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    distance = cursor.getInt(cursor.getColumnIndexOrThrow("distance")),
                    level = cursor.getString(cursor.getColumnIndexOrThrow("level")),
                    desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"))
                )
                trails.add(trail)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return trails
    }


    fun saveTrailTime(trailId: Int, time: Long) {
        val values = ContentValues().apply {
            put("trail_id", trailId)
            put("time", time)
        }
        val db = this.writableDatabase
        db.insert("TrailTimes", null, values)
        db.close()
    }

    fun getTrailTimes(trailId: Int): List<Long> {
        val times = mutableListOf<Long>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT time FROM TrailTimes WHERE trail_id = ?", arrayOf(trailId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val time = cursor.getLong(cursor.getColumnIndexOrThrow("time"))
                times.add(time)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return times
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "trails.db"
    }
}
