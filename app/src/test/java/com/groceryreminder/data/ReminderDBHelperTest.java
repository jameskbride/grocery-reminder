package com.groceryreminder.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ReminderDBHelperTest {

    private static final String REMINDER_DATABASE_NAME = "grocery_reminder.sqlite";
    private static final String LOCATIONS_TABLE = "locations";
    private ReminderDBHelper dbHelper;

    @Before
    public void setUp() {
        ShadowApplication context = Robolectric.getShadowApplication();
        dbHelper = new ReminderDBHelper(context.getApplicationContext());
    }

    @Test
    public void whenTheDBHelperIsCreatedThenTheDatabaseNameShouldBeSet() {
        assertEquals(REMINDER_DATABASE_NAME, dbHelper.getDatabaseName());
    }

    @Test
    public void whenTheDBHelperIsCreatedThenTheLocationsTableShouldBeCreated() {
        ContentValues values = createDefaultLocationValues();
        insertValues(values);

        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.query(LOCATIONS_TABLE, ReminderContract.Locations.PROJECT_ALL, "", null, null, null, ReminderContract.Locations.SORT_ORDER_DEFAULT, null);

        assertTrue(cursor.moveToNext());
        assertEquals(1, cursor.getInt(0));
        assertEquals(ReminderContract.Locations.NAME, cursor.getString(1));
        assertEquals(ReminderContract.Locations.PLACES_ID, cursor.getString(2));
        assertEquals(ReminderContract.Locations.LATITUDE, cursor.getString(3));
        assertEquals(ReminderContract.Locations.LONGITUDE, cursor.getString(4));
    }

    @Test
    public void whenTheDBHelperIsUpgradedThenTheLocationsTableIsRecreated() {
        ContentValues values = createDefaultLocationValues();
        SQLiteDatabase writableDatabase = insertValues(values);

        dbHelper.onUpgrade(writableDatabase, 0, 0);

        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.query(LOCATIONS_TABLE, ReminderContract.Locations.PROJECT_ALL, "", null, null, null, ReminderContract.Locations.SORT_ORDER_DEFAULT, null);

        assertFalse(cursor.moveToNext());
    }

    private SQLiteDatabase insertValues(ContentValues values) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.insert(LOCATIONS_TABLE, "", values);

        return writableDatabase;
    }

    private ContentValues createDefaultLocationValues() {
        ContentValues values = new ContentValues();
        values.put(ReminderContract.Locations.NAME, "location_name");
        values.put(ReminderContract.Locations.PLACES_ID, "places_id");
        values.put(ReminderContract.Locations.LATITUDE, "latitude");
        values.put(ReminderContract.Locations.LONGITUDE, "longitude");
        return values;
    }
}
