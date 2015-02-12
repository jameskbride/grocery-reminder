package com.groceryreminder;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(MainActivity.class).create().start().get();
    }

    @Test
    public void whenTheActivityIsCreatedThenItShouldBeStarted() {
        assertFalse(activity.isFinishing());
    }

    @Test
    public void whenTheActivityIsCreatedThenTheReminderListShouldBeDisplayed() {
        ReminderListFragment reminderListFragment =
                (ReminderListFragment)activity.getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);

        ListView listView = (ListView)reminderListFragment.getView().findViewById(android.R.id.list);
        assertEquals(View.VISIBLE, listView.getVisibility());
    }

    @Test
    public void whenTheAddReminderButtonIsTappedThenTheAddReminderFragmentShouldBeDisplayed() {
        FloatingActionButton floatingActionButton = (FloatingActionButton)activity.findViewById(R.id.fab);
        floatingActionButton.performClick();

        AddReminderFragment addReminderFragment = (AddReminderFragment)activity.getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        EditText addReminderEditText = (EditText)addReminderFragment.getView().findViewById(R.id.add_reminder_edit);

        assertEquals(View.VISIBLE, addReminderEditText.getVisibility());
    }
}