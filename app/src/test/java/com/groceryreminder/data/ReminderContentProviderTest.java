package com.groceryreminder.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.groceryreminder.models.Reminder;
import com.groceryreminder.testUtils.LocationValuesBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowCursorWrapper;

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
    private LocationValuesBuilder locationValuesBuilder;

    @Before
    public void setUp() {
        provider = new ReminderContentProvider();
        provider.onCreate();
        locationValuesBuilder = new LocationValuesBuilder();
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
    public void whenADuplicateLocationIsInsertedThenItReplacesTheExistingRecord() {
        ContentValues initialValues = createDefaultLocationValues();
        String placesId = initialValues.getAsString(ReminderContract.Locations.PLACES_ID);
        provider.insert(ReminderContract.Locations.CONTENT_URI, initialValues);
        ContentValues duplicateValues = createDefaultLocationValues();
        duplicateValues.put(ReminderContract.Locations.PLACES_ID, initialValues.getAsString(ReminderContract.Locations.PLACES_ID));
        String duplicateLocationName = "duplicate";
        duplicateValues.put(ReminderContract.Locations.NAME, duplicateLocationName);

        Uri expectedUri = provider.insert(ReminderContract.Locations.CONTENT_URI, duplicateValues);

        Cursor cursor = provider.query(expectedUri, ReminderContract.Locations.PROJECT_ALL, "", null, null);
        assertEquals(1, cursor.getCount());
        cursor.moveToNext();
        assertEquals(duplicateLocationName, cursor.getString(1));
        assertEquals(placesId, cursor.getString(2));
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
    public void givenALocationExistsWhenTheLocationIsDeletedThenOneDeletionWillOccur() {
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

    @Test
    public void givenASelectionIsProvidedWhenALocationIsDeletedThenADeletionWillOccur() {
        String testName = "test";
        ContentValues values = locationValuesBuilder.createDefaultLocationValues().withName(testName).build();

        provider.insert(ReminderContract.Locations.CONTENT_URI, values);

        String selection = ReminderContract.Locations.NAME + " = ? ";
        String[] selectionArgs = new String[] {testName};
        int count = provider.delete(ReminderContract.Locations.CONTENT_URI, selection, selectionArgs);

        assertEquals(1, count);
    }

    @Test
    public void whenNoLocationsAreDeletedThenNoObserversAreNotified() {
        String testName = "test";
        ContentValues values = locationValuesBuilder.createDefaultLocationValues().withName(testName).build();

        ShadowContentResolver contentResolver = Robolectric.shadowOf(provider.getContext().getContentResolver());
        provider.insert(ReminderContract.Locations.CONTENT_URI, values);
        List<ShadowContentResolver.NotifiedUri> notifiedUriList = contentResolver.getNotifiedUris();
        assertEquals(1, notifiedUriList.size());

        String selection = ReminderContract.Locations.NAME + " = ? ";
        String[] selectionArgs = new String[] {"wrong name"};
        provider.delete(ReminderContract.Locations.CONTENT_URI, selection, selectionArgs);

        notifiedUriList = contentResolver.getNotifiedUris();
        assertEquals(1, notifiedUriList.size());
    }

    @Test
    public void whenMultipleLocationsAreDeletedThenMultipleDeletionsShouldHaveOccurred() {
        ContentValues values = createDefaultLocationValues();
        ContentValues secondValues = createDefaultLocationValues();
        secondValues.put(ReminderContract.Locations.PLACES_ID, values.getAsString(ReminderContract.Locations.PLACES_ID + 1));

        provider.insert(ReminderContract.Locations.CONTENT_URI, values);
        provider.insert(ReminderContract.Locations.CONTENT_URI, secondValues);

        int count = provider.delete(ReminderContract.Locations.CONTENT_URI, "", null);

        assertEquals(2, count);
    }

    @Test
    public void whenLocationsAreQueriedThenTheRequestedProjectsShouldBeReturned() {
        ContentValues values = createDefaultLocationValues();
        provider.insert(ReminderContract.Locations.CONTENT_URI, values);

        Cursor cursor = provider.query(ReminderContract.Locations.CONTENT_URI, new String[] {ReminderContract.Locations._ID}, "", null, null);

        assertEquals(0, cursor.getColumnIndex(ReminderContract.Locations._ID));
    }

    @Test
    public void whenLocationsAreQueriedThenTheRequestedSelectionShouldBeUsed() {
        ContentValues otherRecord = new LocationValuesBuilder().createDefaultLocationValues().build();
        String expectedName = "test";
        ContentValues recordToQuery = new LocationValuesBuilder().createDefaultLocationValues().withName(expectedName).build();
        provider.insert(ReminderContract.Locations.CONTENT_URI, otherRecord);
        provider.insert(ReminderContract.Locations.CONTENT_URI, recordToQuery);

        String selection = ReminderContract.Locations.NAME + " = 'test'";
        Cursor cursor = provider.query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, selection, null, null);

        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToNext());
        assertEquals(expectedName, cursor.getString(1));
    }

    @Test
    public void whenLocationsAreQueriedThenTheRequestedSelectionArgsShouldBeUsed() {
        ContentValues otherRecord = new LocationValuesBuilder().createDefaultLocationValues().build();
        String expectedName = "test";
        ContentValues recordToQuery = new LocationValuesBuilder().createDefaultLocationValues().withName(expectedName).build();
        provider.insert(ReminderContract.Locations.CONTENT_URI, otherRecord);
        provider.insert(ReminderContract.Locations.CONTENT_URI, recordToQuery);

        String selection = ReminderContract.Locations.NAME + " = ?";
        String[] selectionArgs = new String[] {expectedName};
        Cursor cursor = provider.query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, selection, selectionArgs, null);

        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToNext());
        assertEquals(expectedName, cursor.getString(1));
    }

    @Test
    public void whenLocationsAreQueriedThenTheRequestedSortOrderShouldBeUsed() {
        String secondExpectedName = "bananas";
        ContentValues secondRecord = new LocationValuesBuilder().createDefaultLocationValues().withName(secondExpectedName).build();
        String firstExpectedName = "apples";
        ContentValues recordToQuery = new LocationValuesBuilder().createDefaultLocationValues().withName(firstExpectedName).build();
        provider.insert(ReminderContract.Locations.CONTENT_URI, secondRecord);
        provider.insert(ReminderContract.Locations.CONTENT_URI, recordToQuery);

        Cursor cursor = provider.query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, null, null, ReminderContract.Locations.NAME + " asc");

        assertTrue(cursor.moveToNext());
        assertEquals(firstExpectedName, cursor.getString(1));
        assertTrue(cursor.moveToNext());
        assertEquals(secondExpectedName, cursor.getString(1));
    }

    private ContentValues createDefaultLocationValues() {
        return new LocationValuesBuilder().createDefaultLocationValues().build();
    }
}
