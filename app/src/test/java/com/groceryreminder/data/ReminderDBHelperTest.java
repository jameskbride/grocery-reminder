package com.groceryreminder.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.testUtils.LocationValuesBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReminderDBHelperTest {

    private static final String REMINDER_DATABASE_NAME = "grocery_reminder.sqlite";
    private ReminderDBHelper dbHelper;

    @Before
    public void setUp() {
        ShadowApplication context = Shadows.shadowOf(RuntimeEnvironment.application);
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
        Cursor cursor = readableDatabase.query(DBSchema.LOCATIONS, ReminderContract.Locations.PROJECT_ALL, "", null, null, null, ReminderContract.Locations.SORT_ORDER_DEFAULT, null);

        assertTrue(cursor.moveToNext());
        assertEquals(1, cursor.getInt(0));
        assertEquals(ReminderContract.Locations.NAME, cursor.getString(1));
        assertTrue(cursor.getString(2).contains(ReminderContract.Locations.PLACES_ID));
        assertEquals(ReminderContract.Locations.LATITUDE, cursor.getString(3));
        assertEquals(ReminderContract.Locations.LONGITUDE, cursor.getString(4));
    }

    @Test
    public void whenTheDBHelperIsUpgradedThenTheLocationsTableIsRecreated() {
        ContentValues values = createDefaultLocationValues();
        SQLiteDatabase writableDatabase = insertValues(values);

        dbHelper.onUpgrade(writableDatabase, 0, 0);

        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.query(DBSchema.LOCATIONS, ReminderContract.Locations.PROJECT_ALL, "", null, null, null, ReminderContract.Locations.SORT_ORDER_DEFAULT, null);

        assertFalse(cursor.moveToNext());
    }

    private SQLiteDatabase insertValues(ContentValues values) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.insert(DBSchema.LOCATIONS, "", values);

        return writableDatabase;
    }

    private ContentValues createDefaultLocationValues() {
        return new LocationValuesBuilder().createDefaultLocationValues().build();
    }
}
