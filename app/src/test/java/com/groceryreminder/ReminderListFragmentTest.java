package com.groceryreminder;

import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ReminderListFragmentTest extends RobolectricTestBase {

    private MainActivity activity;
    private MainActivity mainActivitySpy;

    @Before
    public void setUp() {
        super.setUp();
        this.activity = Robolectric.buildActivity(MainActivity.class).create().start().visible().get();
        this.mainActivitySpy = spy(activity);
    }

    @Test
    public void givenNoRemindersWhenTheFragmentIsCreatedThenTheViewShouldBePopulated() {
        Reminder reminder = new Reminder("test");
        List<Reminder> reminders = new ArrayList<Reminder>();
        reminders.add(reminder);

        ReminderListFragment reminderListFragment = ReminderListFragment.newInstance(reminders);
        startFragment(activity, reminderListFragment);

        ListView reminderListView = reminderListFragment.getListView();
        Reminder actualReminder = (Reminder)reminderListView.getAdapter().getItem(0);
        assertEquals("test", actualReminder.getText());
    }

    @Test
    public void whenTheFragmentIsAttachedThenTheOnAddReminderRequestListenerShouldBeSet() {
        List<Reminder> reminders = new ArrayList<Reminder>();
        ReminderListFragment reminderListFragment = ReminderListFragment.newInstance(reminders);
        startFragment(activity, reminderListFragment);
        reminderListFragment.onAttach(activity);

        OnAddReminderRequestListener onAddReminderRequestListener = reminderListFragment.getOnAddReminderRequestListener();
        assertNotNull(onAddReminderRequestListener);
    }

    @Test
    public void whenAReminderIsAddedThenTheReminderListIsUpdated() {
        List<Reminder> reminders = new ArrayList<Reminder>();
        ReminderListFragment reminderListFragment = ReminderListFragment.newInstance(reminders);
        startFragment(activity, reminderListFragment);

        reminderListFragment.addReminder(new Reminder("new reminder"));

        ListView reminderListView = reminderListFragment.getListView();
        Reminder actualReminder = (Reminder)reminderListView.getAdapter().getItem(0);
        assertEquals("new reminder", actualReminder.getText());
    }
}
