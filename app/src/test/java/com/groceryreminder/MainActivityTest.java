package com.groceryreminder;

import android.widget.ListView;

import com.groceryreminder.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
    }

    @Test
    public void givenNoRemindersExistThenTheReminderListIsEmpty() {
        ListView listView = (ListView)activity.findViewById(R.id.reminders_list);
        assertEquals(listView.getCount(), 0);
    }
}