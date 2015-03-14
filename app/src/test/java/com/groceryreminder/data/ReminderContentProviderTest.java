package com.groceryreminder.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;

import com.groceryreminder.models.Reminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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

    @Test
    public void givenALocationContentTypeAndContentValuesWhenARecordIsInsertedThenObserversAreNotified()
    {
        ContentValues values = createDefaultLocationValues();
        ShadowContentResolver contentResolver = Robolectric.shadowOf(provider.getContext().getContentResolver());
        Uri expectedUri = provider.insert(ReminderContract.Locations.CONTENT_URI, values);

        List<ShadowContentResolver.NotifiedUri> notifiedUriList = contentResolver.getNotifiedUris();
        assertThat(notifiedUriList.get(0).uri, is(expectedUri));
    }

    @Test
    public void givenALocationExistsWhenTheLocationIsDeletedThenOneDeletionShouldHaveOccurred() {
        ContentValues values = createDefaultLocationValues();
        Uri expectedUri = provider.insert(ReminderContract.Locations.CONTENT_URI, values);

        int count = provider.delete(expectedUri, "", null);

        assertEquals(1, count);
    }

    @Test
    public void whenALocationIsDeletedThenObserversAreNotified() {
        ContentValues values = createDefaultLocationValues();
        Uri expectedUri = provider.insert(ReminderContract.Locations.CONTENT_URI, values);

        ShadowContentResolver contentResolver = Robolectric.shadowOf(provider.getContext().getContentResolver());
        provider.delete(expectedUri, "", null);

        List<ShadowContentResolver.NotifiedUri> notifiedUriList = contentResolver.getNotifiedUris();
        assertThat(notifiedUriList.get(1).uri, is(expectedUri));
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
