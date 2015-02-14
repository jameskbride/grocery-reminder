package com.groceryreminder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ReminderListFragmentTest {

    private MainActivity activity;
    private MainActivity mainActivitySpy;

    @Before
    public void setUp() {
        this.activity = Robolectric.buildActivity(MainActivity.class).create().start().visible().get();
        this.mainActivitySpy = spy(activity);
    }

    private void startFragment(FragmentActivity parentActivity, Fragment fragment) {
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add( fragment, null );
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
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
    public void givenTheAddReminderListenerIsSetWhenTheAddReminderButtonIsTappedThenTheOnAddReminderListenerIsCalled() {
        List<Reminder> reminders = new ArrayList<Reminder>();
        ReminderListFragment reminderListFragment = ReminderListFragment.newInstance(reminders);
        startFragment(activity, reminderListFragment);

        FragmentTestUtil.startVisibleFragment(reminderListFragment);
        FloatingActionButton floatingActionButton = (FloatingActionButton)activity.findViewById(R.id.fab);
        floatingActionButton.performClick();

        verify(mainActivitySpy, atLeastOnce()).requestNewReminder();
    }
}
