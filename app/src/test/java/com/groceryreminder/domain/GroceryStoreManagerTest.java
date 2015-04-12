package com.groceryreminder.domain;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContentProvider;
import com.groceryreminder.data.ReminderContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryStoreManagerTest extends RobolectricTestBase {

    private GroceryStoreManager groceryStoreManager;
    private ReminderContentProvider reminderProvider;
    private ShadowContentResolver shadowContentResolver;

    @Before
    public void setUp() {
        super.setUp();

        groceryStoreManager = new GroceryStoreManager(getTestAndroidModule().getApplicationContext(),
                getTestReminderModule().getGooglePlaces());
        setupReminderContentProvider();
    }

    private void setupReminderContentProvider() {
        reminderProvider = new ReminderContentProvider();
        reminderProvider.onCreate();
        shadowContentResolver = Robolectric.shadowOf(getTestAndroidModule().getApplicationContext().getContentResolver());
        shadowContentResolver.registerProvider(ReminderContract.AUTHORITY, reminderProvider);
    }

    @Test
    public void whenStoresAreClearedThenTheStoresShouldBeRemovedFromTheDatabase() {
        ContentValues values = new ContentValues();
        values.put(ReminderContract.Locations.NAME, "test");
        values.put(ReminderContract.Locations.PLACES_ID, "test");
        values.put(ReminderContract.Locations.LATITUDE, 1);
        values.put(ReminderContract.Locations.LONGITUDE, 2);

        shadowContentResolver.insert(ReminderContract.Locations.CONTENT_URI, values);

        Cursor cursor = shadowContentResolver.query(ReminderContract.Locations.CONTENT_URI,
                ReminderContract.Locations.PROJECT_ALL, null, null, ReminderContract.Locations.SORT_ORDER_DEFAULT);

        groceryStoreManager.clearAllStores();

        assertEquals(0, cursor.getCount());
    }
}
