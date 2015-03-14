package com.groceryreminder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReminderDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "grocery_reminder.sqlite";
    private static final String CREATE_LOCATIONS_TABLE_SQL = "CREATE TABLE locations (" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " places_id varchar(200) UNIQUE, " +
            " location_name varchar(200), " +
            " latitude varchar(200), " +
            " longitude varchar(200) " +
            " )";

    public ReminderDBHelper(Context applicationContext) {
        super(applicationContext, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("ReminderDBHelper", "Creating the locations table.");
        db.execSQL(CREATE_LOCATIONS_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
