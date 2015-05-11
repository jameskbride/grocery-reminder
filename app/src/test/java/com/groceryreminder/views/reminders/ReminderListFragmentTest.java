package com.groceryreminder.views.reminders;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.models.Reminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReminderListFragmentTest extends RobolectricTestBase {

    private RemindersActivity activity;

    @Before
    public void setUp() {
        super.setUp();
        this.activity = Robolectric.buildActivity(RemindersActivity.class).create().start().visible().get();
    }

    @Test
    public void givenRemindersWhenTheFragmentIsCreatedThenTheViewShouldBePopulated() {
        Reminder reminder = new Reminder(0, "test");
        List<Reminder> reminders = new ArrayList<Reminder>();
        reminders.add(reminder);

        ReminderListFragment reminderListFragment = ReminderListFragment.newInstance(reminders);
        startFragment(activity, reminderListFragment);

        RecyclerView reminderRecyclerView = getRecyclerView(reminderListFragment, R.id.reminders_recycler_view);
        ReminderListViewHolder reminderListViewHolder = (ReminderListViewHolder)reminderRecyclerView.findViewHolderForAdapterPosition(0);

        TextView reminderText = (TextView)reminderListViewHolder.itemView.findViewById(R.id.reminders_text_view);
        assertEquals(reminderText.getText(), "test");
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
}
