package com.groceryreminder.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.testUtils.LocationValuesBuilder;
import com.groceryreminder.testUtils.ReminderValuesBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReminderContentProviderTest extends RobolectricTestBase {

    private ReminderContentProvider provider;

    @Before
    public void setUp() {
        super.setUp();
        provider = new ReminderContentProvider();
        provider.onCreate();
    }

    @Test
    public void whenTheProviderIsCreatedThenItShouldBeInitialized() {
        ReminderContentProvider provider = new ReminderContentProvider();
        assertTrue(provider.onCreate());
    }

    @Test
    public void givenAReminderContentTypeAndContentValuesWhenARecordIsInsertedThenAURIWithRecordIDShouldBeReturned()
    {
        ContentValues values = createDefaultReminderValues();
        Uri expectedUri = provider.insert(ReminderContract.Reminders.CONTENT_URI, values);

        assertNotNull(expectedUri);
        assertEquals(1, ContentUris.parseId(expectedUri));
    }

    private ContentValues createDefaultReminderValues() {
        return new ReminderValuesBuilder().createDefaultReminderValues().build();
    }

    @Test
    public void whenADuplicateReminderIsInsertedThenItThereIsNoConflict() {
        ContentValues initialValues = createDefaultReminderValues();
        provider.insert(ReminderContract.Reminders.CONTENT_URI, initialValues);
        ContentValues duplicateValues = createDefaultReminderValues();
        duplicateValues.put(ReminderContract.Reminders.DESCRIPTION, initialValues.getAsString(ReminderContract.Reminders.DESCRIPTION));

        Uri expectedUri = provider.insert(ReminderContract.Reminders.CONTENT_URI, duplicateValues);

        Cursor cursor = provider.query(expectedUri, ReminderContract.Reminders.PROJECT_ALL, "", null, null);
        assertEquals(2, cursor.getCount());

        cursor.close();
        cursor = null;
    }

    @Test
    public void givenAReminderContentTypeAndContentValuesWhenARecordIsInsertedThenObserversAreNotified()
    {
        ContentValues values = createDefaultReminderValues();
        ShadowContentResolver contentResolver = Shadows.shadowOf(provider.getContext().getContentResolver());
        Uri expectedUri = provider.insert(ReminderContract.Reminders.CONTENT_URI, values);

        List<ShadowContentResolver.NotifiedUri> notifiedUriList = contentResolver.getNotifiedUris();
        assertThat(notifiedUriList.get(0).uri, is(expectedUri));
    }

    @Test
    public void givenAReminderExistsWhenTheReminderIsDeletedThenOneDeletionWillOccur() {
        ContentValues values = createDefaultReminderValues();
        Uri expectedUri = provider.insert(ReminderContract.Reminders.CONTENT_URI, values);

        int count = provider.delete(expectedUri, "", null);

        assertEquals(1, count);
    }

    @Test
    public void whenAReminderIsDeletedThenObserversAreNotified() {
        ContentValues values = createDefaultReminderValues();
        Uri expectedUri = provider.insert(ReminderContract.Reminders.CONTENT_URI, values);

        ShadowContentResolver contentResolver = Shadows.shadowOf(provider.getContext().getContentResolver());
        provider.delete(expectedUri, "", null);

        List<ShadowContentResolver.NotifiedUri> notifiedUriList = contentResolver.getNotifiedUris();
        assertThat(notifiedUriList.get(1).uri, is(expectedUri));
    }

    @Test
    public void givenASelectionIsProvidedWhenAReminderIsDeletedThenADeletionWillOccur() {
        String testDescription = "test";
        ContentValues values = new ReminderValuesBuilder().createDefaultReminderValues().withDescription(testDescription).build();

        provider.insert(ReminderContract.Reminders.CONTENT_URI, values);

        String selection = ReminderContract.Reminders.DESCRIPTION + " = ? ";
        String[] selectionArgs = new String[] {testDescription};
        int count = provider.delete(ReminderContract.Reminders.CONTENT_URI, selection, selectionArgs);

        assertEquals(1, count);
    }

    @Test
    public void whenNoRemindersAreDeletedThenNoObserversAreNotified() {
        String testDescription = "test";
        ContentValues values = new ReminderValuesBuilder().createDefaultReminderValues().withDescription(testDescription).build();

        ShadowContentResolver contentResolver = Shadows.shadowOf(provider.getContext().getContentResolver());
        provider.insert(ReminderContract.Reminders.CONTENT_URI, values);
        List<ShadowContentResolver.NotifiedUri> notifiedUriList = contentResolver.getNotifiedUris();
        assertEquals(1, notifiedUriList.size());

        String selection = ReminderContract.Reminders.DESCRIPTION + " = ? ";
        String[] selectionArgs = new String[] {"wrong name"};
        provider.delete(ReminderContract.Reminders.CONTENT_URI, selection, selectionArgs);

        notifiedUriList = contentResolver.getNotifiedUris();
        assertEquals(1, notifiedUriList.size());
    }

    @Test
    public void whenMultipleRemindersAreDeletedThenMultipleDeletionsShouldHaveOccurred() {
        ContentValues values = createDefaultReminderValues();
        ContentValues secondValues = createDefaultReminderValues();

        provider.insert(ReminderContract.Reminders.CONTENT_URI, values);
        provider.insert(ReminderContract.Reminders.CONTENT_URI, secondValues);

        int count = provider.delete(ReminderContract.Reminders.CONTENT_URI, "", null);

        assertEquals(2, count);
    }

    @Test
    public void whenRemindersAreQueriedThenTheRequestedProjectsShouldBeReturned() {
        ContentValues values = createDefaultReminderValues();
        provider.insert(ReminderContract.Reminders.CONTENT_URI, values);

        Cursor cursor = provider.query(ReminderContract.Reminders.CONTENT_URI, new String[]{ReminderContract.Reminders._ID}, "", null, null);

        assertEquals(0, cursor.getColumnIndex(ReminderContract.Reminders._ID));

        cursor.close();
        cursor = null;
    }

    @Test
    public void whenRemindersAreQueriedThenTheRequestedSelectionShouldBeUsed() {
        ContentValues otherRecord = new ReminderValuesBuilder().createDefaultReminderValues().build();
        String expectedDescription = "test";
        ContentValues recordToQuery = new ReminderValuesBuilder().createDefaultReminderValues().withDescription(expectedDescription).build();
        provider.insert(ReminderContract.Reminders.CONTENT_URI, otherRecord);
        provider.insert(ReminderContract.Reminders.CONTENT_URI, recordToQuery);

        String selection = ReminderContract.Reminders.DESCRIPTION + " = 'test'";
        Cursor cursor = provider.query(ReminderContract.Reminders.CONTENT_URI, ReminderContract.Reminders.PROJECT_ALL, selection, null, null);

        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToNext());
        assertEquals(expectedDescription, cursor.getString(1));

        cursor.close();
        cursor = null;
    }

    @Test
    public void whenRemindersAreQueriedThenTheRequestedSelectionArgsShouldBeUsed() {
        ContentValues otherRecord = new ReminderValuesBuilder().createDefaultReminderValues().build();
        String expectedDescription = "test";
        ContentValues recordToQuery = new ReminderValuesBuilder().createDefaultReminderValues().withDescription(expectedDescription).build();
        provider.insert(ReminderContract.Reminders.CONTENT_URI, otherRecord);
        provider.insert(ReminderContract.Reminders.CONTENT_URI, recordToQuery);

        String selection = ReminderContract.Reminders.DESCRIPTION + " = ?";
        String[] selectionArgs = new String[] {expectedDescription};
        Cursor cursor = provider.query(ReminderContract.Reminders.CONTENT_URI, ReminderContract.Reminders.PROJECT_ALL, selection, selectionArgs, null);

        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToNext());
        assertEquals(expectedDescription, cursor.getString(1));

        cursor.close();
        cursor = null;
    }

    @Test
    public void whenRemindersAreQueriedThenTheRequestedSortOrderShouldBeUsed() {
        String secondExpectedDescription = "bananas";
        ContentValues secondRecord = new ReminderValuesBuilder().createDefaultReminderValues().withDescription(secondExpectedDescription).build();
        String firstExpectedDescription = "apples";
        ContentValues recordToQuery = new ReminderValuesBuilder().createDefaultReminderValues().withDescription(firstExpectedDescription).build();
        provider.insert(ReminderContract.Reminders.CONTENT_URI, secondRecord);
        provider.insert(ReminderContract.Reminders.CONTENT_URI, recordToQuery);

        Cursor cursor = provider.query(ReminderContract.Reminders.CONTENT_URI, ReminderContract.Reminders.PROJECT_ALL, null, null, ReminderContract.Reminders.DESCRIPTION + " asc");

        assertTrue(cursor.moveToNext());
        assertEquals(firstExpectedDescription, cursor.getString(1));
        assertTrue(cursor.moveToNext());
        assertEquals(secondExpectedDescription, cursor.getString(1));

        cursor.close();
        cursor = null;
    }
}
