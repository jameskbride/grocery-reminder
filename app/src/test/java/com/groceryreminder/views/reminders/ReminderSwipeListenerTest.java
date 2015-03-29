package com.groceryreminder.views.reminders;

import android.support.v7.widget.RecyclerView;

import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.models.Reminder;
import com.groceryreminder.views.reminders.ReminderSwipeListener;
import com.groceryreminder.views.reminders.RemindersRecyclerViewAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ReminderSwipeListenerTest extends RobolectricTestBase {

    List<Reminder> defaultReminders;
    RecyclerView recyclerView;
    RemindersRecyclerViewAdapter adapter;
    RemindersRecyclerViewAdapter adapterSpy;

    @Before
    public void setUp() {
        defaultReminders = getDefaultReminderList();
        recyclerView = new RecyclerView(Robolectric.getShadowApplication().getApplicationContext());
        adapter = new RemindersRecyclerViewAdapter(defaultReminders);
        adapterSpy = spy(adapter);
    }

    @Test
    public void givenAReminderWhenTheReminderIsSwipedLeftThenItIsDismissed() {
        ReminderSwipeListener swipeListener = new ReminderSwipeListener(defaultReminders, adapter);

        swipeListener.onDismissedBySwipeLeft(recyclerView, new int[] {0});

        assertTrue(defaultReminders.isEmpty());
    }

    @Test
    public void givenAReminderWhenTheReminderIsSwipedLeftThenListenersAreNotifiedThatItHasBeenRemoved() {
        ReminderSwipeListener swipeListener = new ReminderSwipeListener(defaultReminders, adapterSpy);

        swipeListener.onDismissedBySwipeLeft(recyclerView, new int[] {0});

        verify(adapterSpy).notifyItemRemoved(0);
    }

    @Test
    public void givenAReminderWhenTheReminderIsSwipedLeftThenListenersAreNotifiedOfDatasetChange() {
        ReminderSwipeListener swipeListener = new ReminderSwipeListener(defaultReminders, adapterSpy);

        swipeListener.onDismissedBySwipeLeft(recyclerView, new int[] {0});

        verify(adapterSpy).notifyDataSetChanged();
    }

    @Test
    public void givenAReminderWhenTheReminderIsSwipedRightThenItIsDismissed() {
        ReminderSwipeListener swipeListener = new ReminderSwipeListener(defaultReminders, adapter);

        swipeListener.onDismissedBySwipeRight(recyclerView, new int[]{0});

        assertTrue(defaultReminders.isEmpty());
    }

    @Test
    public void givenAReminderWhenTheReminderIsSwipedRightThenListenersAreNotifiedThatItHasBeenRemoved() {
        ReminderSwipeListener swipeListener = new ReminderSwipeListener(defaultReminders, adapterSpy);

        swipeListener.onDismissedBySwipeRight(recyclerView, new int[] {0});

        verify(adapterSpy).notifyItemRemoved(0);
    }

    @Test
    public void givenAReminderWhenTheReminderIsSwipedRightThenListenersAreNotifiedOfDatasetChange() {
        ReminderSwipeListener swipeListener = new ReminderSwipeListener(defaultReminders, adapterSpy);

        swipeListener.onDismissedBySwipeRight(recyclerView, new int[] {0});

        verify(adapterSpy).notifyDataSetChanged();
    }

    private List<Reminder> getDefaultReminderList() {
        Reminder reminder = new Reminder("test");
        List<Reminder> reminders = new ArrayList<Reminder>();
        reminders.add(reminder);
        return reminders;
    }
}
