package com.groceryreminder.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ReminderContentProviderTest {

    private ReminderContentProvider provider;

    @Before
    public void setUp() {
        provider = new ReminderContentProvider();
        provider.onCreate();
    }

    @Test
    public void whenTheProviderIsCreatedThenItShouldBeInitialized() {
        ReminderContentProvider provider = new ReminderContentProvider();
        assertTrue(provider.onCreate());
    }

    @Test
    public void givenALocationContentTypeAndContentValuesWhenARecordIsInsertedThenAURIWithRecordIDShouldBeReturned()
    {
        ContentValues values = createDefaultLocationValues();
        Uri expectedUri = provider.insert(ReminderContract.Locations.CONTENT_URI, values);

        assertNotNull(expectedUri);
        assertEquals(1, ContentUris.parseId(expectedUri));
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
