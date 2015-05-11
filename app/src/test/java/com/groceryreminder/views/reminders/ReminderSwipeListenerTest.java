package com.groceryreminder.views.reminders;

import android.support.v7.widget.RecyclerView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.models.Reminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReminderSwipeListenerTest extends RobolectricTestBase {

    List<Reminder> defaultReminders;
    RecyclerView recyclerView;
    RemindersRecyclerViewAdapter adapter;
    RemindersRecyclerViewAdapter adapterSpy;
    OnReminderDataChangeListener onReminderDataChangeListenerMock;

    @Before
    public void setUp() {
        defaultReminders = getDefaultReminderList();
        recyclerView = new RecyclerView(Shadows.shadowOf(RuntimeEnvironment.application).getApplicationContext());
        onReminderDataChangeListenerMock = mock(OnReminderDataChangeListener.class);
        adapter = new RemindersRecyclerViewAdapter(defaultReminders, onReminderDataChangeListenerMock);
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
        Reminder reminder = new Reminder(0, "test");
        List<Reminder> reminders = new ArrayList<Reminder>();
        reminders.add(reminder);
        return reminders;
    }
}
