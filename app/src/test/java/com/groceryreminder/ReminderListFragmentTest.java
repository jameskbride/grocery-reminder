package com.groceryreminder;

import android.widget.ListView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ReminderListFragmentTest {

    @Test
    public void givenNoRemindersWhenTheFragmentIsCreatedThenTheViewShouldBePopulated() {
        Reminder reminder = new Reminder("test");
        List<Reminder> reminders = new ArrayList<Reminder>();
        reminders.add(reminder);

        ReminderListFragment reminderListFragment = ReminderListFragment.newInstance(reminders);
        FragmentTestUtil.startFragment(reminderListFragment);

        ListView reminderListView = reminderListFragment.getListView();
        Reminder actualReminder = (Reminder)reminderListView.getAdapter().getItem(0);
        assertEquals("test", actualReminder.getText());
    }
}
