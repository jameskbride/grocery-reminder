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

    private static final String DROP_LOCATIONS_TABLE = "DROP TABLE IF EXISTS locations";
    private static final String TAG = "ReminderDBHelper";

    public ReminderDBHelper(Context applicationContext) {
        super(applicationContext, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating the locations table.");
        db.execSQL(CREATE_LOCATIONS_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Dropping the locations table: " + DROP_LOCATIONS_TABLE);
        db.execSQL(DROP_LOCATIONS_TABLE);

        Log.d(TAG, "Recreating the locations table: " + CREATE_LOCATIONS_TABLE_SQL);
        db.execSQL(CREATE_LOCATIONS_TABLE_SQL);
    }
}
