package com.groceryreminder.views.reminders;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.models.Reminder;
import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.stores.GroceryStoresActivity;
import com.melnykov.fab.FloatingActionButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowCursorWrapper;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RemindersActivityTest extends RobolectricTestBase {

    private ActivityController<RemindersActivity> activityController;
    private RemindersActivity activity;

    @Before
    public void setUp() {
        activityController = Robolectric.buildActivity(RemindersActivity.class);
        activity = activityController.setup().get();
    }

    @After
    public void tearDown() {
        activityController.pause().stop().destroy();
    }

    private ReminderListFragment getReminderListFragment() {
        return (ReminderListFragment)activity.getSupportFragmentManager()
                .findFragmentById(R.id.reminder_fragment_container);
    }

    private AddReminderFragment getAddReminderFragment() {
        return (AddReminderFragment)activity.getSupportFragmentManager()
                .findFragmentById(R.id.reminder_fragment_container);
    }

    private void clickAddReminderButton(AddReminderFragment addReminderFragment) {
        Button addButton = (Button)addReminderFragment.getView().findViewById(R.id.add_reminder_button);
        addButton.performClick();
    }

    private void clickAddReminderRequestButton() {
        FloatingActionButton addReminderRequestButton = (FloatingActionButton)activity.findViewById(R.id.fab);
        addReminderRequestButton.performClick();
    }

    private void loadReminderListFragment() {
        Reminder reminder = new Reminder(0, "test");
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mockCursor.getString(1)).thenReturn(reminder.getText());
        ShadowCursorWrapper wrapper = new ShadowCursorWrapper();
        wrapper.__constructor__(mockCursor);

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoadFinished(cursorLoader, wrapper);
    }

    @Test
    public void whenTheActivityIsCreatedThenItShouldBeStarted() {
        assertFalse(activity.isFinishing());
    }

    @Test
    public void whenTheActivityIsCreatedThenTheReminderListShouldBeDisplayed() {
        ReminderListFragment reminderListFragment = getReminderListFragment();

        RecyclerView listView = (RecyclerView)reminderListFragment.getView().findViewById(R.id.reminders_recycler_view);
        assertEquals(View.VISIBLE, listView.getVisibility());
    }

    @Test
    public void whenTheAddReminderRequestButtonIsTappedThenTheAddReminderFragmentShouldBeDisplayed() {
        clickAddReminderRequestButton();
        AddReminderFragment addReminderFragment = getAddReminderFragment();
        EditText addReminderEditText = (EditText)addReminderFragment.getView().findViewById(R.id.add_reminder_edit);

        assertEquals(View.VISIBLE, addReminderEditText.getVisibility());
    }

    @Test
    public void whenAReminderIsEnteredThenItIsPersisted() {
        clickAddReminderRequestButton();
        AddReminderFragment addReminderFragment = getAddReminderFragment();
        EditText addReminderEditText = (EditText)addReminderFragment.getView().findViewById(R.id.add_reminder_edit);
        String expectedText = "a reminder";
        addReminderEditText.setText(expectedText);

        clickAddReminderButton(addReminderFragment);

        Cursor cursor = activity.getContentResolver().query(ReminderContract.Reminders.CONTENT_URI, ReminderContract.Reminders.PROJECT_ALL, "", null, null);
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToNext());
        assertEquals(expectedText, cursor.getString(1));

        cursor.close();
        cursor = null;
    }

    @Test
    public void whenTheStoresActionBarButtonIsPressedThenTheGroceryStoresActivityIsStarted() {
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);

        shadowActivity.clickMenuItem(R.id.action_find_stores);

        Intent startedIntent = shadowActivity.peekNextStartedActivity();
        assertEquals(GroceryStoresActivity.class.getName(), startedIntent.getComponent().getClassName());
    }

    @Test
    public void whenTheStoresActionBarButtonIsPressedThenTheGroceryLocatorServiceIsStarted() {
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);

        shadowActivity.clickMenuItem(R.id.action_find_stores);

        Intent startedIntent = shadowActivity.peekNextStartedService();
        assertEquals(GroceryLocatorService.class.getName(), startedIntent.getComponent().getClassName());
    }

    @Test
    public void whenTheActivityIsCreatedThenTheCursorLoaderShouldBeInitialized() {
        assertNotNull(activity.getSupportLoaderManager().getLoader(1));
    }

    @Test
    public void whenTheLoaderIsCreatedThenTheCursorLoaderShouldBeConfigured() {
        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);

        assertNotNull(cursorLoader);
        assertEquals(ReminderContract.Reminders.CONTENT_URI, cursorLoader.getUri());
        assertArrayEquals(ReminderContract.Reminders.PROJECT_ALL, cursorLoader.getProjection());
        assertEquals(ReminderContract.Reminders.SORT_ORDER_DEFAULT, cursorLoader.getSortOrder());
        assertNull(cursorLoader.getSelection());
        assertNull(cursorLoader.getSelectionArgs());
    }

    @Test
    public void whenTheLoaderIsFinishedThenTheRemindersListIsUpdated() {
        Reminder reminder = new Reminder(0, "test");
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mockCursor.getLong(0)).thenReturn(reminder.getId());
        when(mockCursor.getString(1)).thenReturn(reminder.getText());
        ShadowCursorWrapper wrapper = new ShadowCursorWrapper();
        wrapper.__constructor__(mockCursor);

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoadFinished(cursorLoader, wrapper);
        ReminderListFragment reminderListFragment = getReminderListFragment();
        assertNotNull(reminderListFragment);

        RecyclerView listView = getRecyclerView(reminderListFragment, R.id.reminders_recycler_view);
        TextView reminderDescriptionText = (TextView)listView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.reminders_text_view);
        assertEquals(View.VISIBLE, reminderDescriptionText.getVisibility());
        assertEquals(reminder.getText(), reminderDescriptionText.getText());
    }

    @Test
    public void whenTheCursorIsResetThenTheReminderListFragmentShouldContainNoStores() {
        loadReminderListFragment();

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoaderReset(cursorLoader);

        ReminderListFragment reminderListFragment = getReminderListFragment();
        assertNotNull(reminderListFragment);

        RecyclerView listView = getRecyclerView(reminderListFragment, R.id.reminders_recycler_view);
        assertEquals(0, listView.getAdapter().getItemCount());
    }
}