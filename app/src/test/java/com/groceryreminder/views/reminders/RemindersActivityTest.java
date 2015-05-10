package com.groceryreminder.views.reminders;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.stores.GroceryStoresActivity;
import com.melnykov.fab.FloatingActionButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RemindersActivityTest extends RobolectricTestBase {

    private RemindersActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.setupActivity(RemindersActivity.class);
    }

    private ReminderListFragment getReminderListFragment() {
        return (ReminderListFragment)activity.getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
    }

    private AddReminderFragment getAddReminderFragment() {
        return (AddReminderFragment)activity.getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
    }

    private void clickAddReminderButton(AddReminderFragment addReminderFragment) {
        Button addButton = (Button)addReminderFragment.getView().findViewById(R.id.add_reminder_button);
        addButton.performClick();
    }

    private void clickAddReminderRequestButton() {
        FloatingActionButton addReminderRequestButton = (FloatingActionButton)activity.findViewById(R.id.fab);
        addReminderRequestButton.performClick();
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
    public void givenTheAddReminderFragmentIsVisibleWhenAReminderIsEnteredThenTheReminderListIsUpdated() {
        clickAddReminderRequestButton();
        AddReminderFragment addReminderFragment = getAddReminderFragment();
        EditText addReminderEditText = (EditText)addReminderFragment.getView().findViewById(R.id.add_reminder_edit);
        String expectedText = "a reminder";
        addReminderEditText.setText(expectedText);

        clickAddReminderButton(addReminderFragment);

        ReminderListFragment reminderListFragment = getReminderListFragment();

        RecyclerView listView = getRecyclerView(reminderListFragment, R.id.reminders_recycler_view);
        TextView reminderText = (TextView)listView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.reminders_text_view);
        assertEquals(reminderText.getText(), expectedText);
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
}