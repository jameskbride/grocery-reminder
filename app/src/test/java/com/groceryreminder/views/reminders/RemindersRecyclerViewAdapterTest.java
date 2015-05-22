package com.groceryreminder.views.reminders;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.models.Reminder;
import com.groceryreminder.testUtils.ReminderValuesBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RemindersRecyclerViewAdapterTest {

    private static final String ARBITRARY_REMINDER_TEXT = "test";
    private RemindersActivity activity;
    private List<Reminder> reminders;

    @Before
    public void setUp() {
        this.activity = Robolectric.buildActivity(RemindersActivity.class).create().start().get();
        this.reminders = new ArrayList<Reminder>();
    }

    private RemindersRecyclerViewAdapter createAdapter(List<Reminder> reminders) {
        return new RemindersRecyclerViewAdapter(reminders, activity);
    }

    private RecyclerView getRecyclerView() {
        RecyclerView viewGroup = (RecyclerView)activity.findViewById(R.id.reminders_recycler_view);
        viewGroup.setLayoutManager(new LinearLayoutManager(activity));

        return viewGroup;
    }

    @Test
    public void whenTheAdapterIsCreatedWithRemindersThenTheItemCountIsSet() {
        Reminder reminder = new Reminder(0, ARBITRARY_REMINDER_TEXT);
        reminders.add(reminder);
        RemindersRecyclerViewAdapter adapter = createAdapter(reminders);

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void whenTheViewHolderIsCreatedThenTheReminderListViewHolderIsNotNull() {
        RecyclerView viewGroup = getRecyclerView();
        RemindersRecyclerViewAdapter adapter = new RemindersRecyclerViewAdapter(reminders, activity);

        ReminderListViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, -1);

        assertNotNull(viewHolder);
    }

    @Test
    public void givenAReminderWhenTheViewHolderIsBoundThenTheTextViewIsSet() {
        Reminder reminder = new Reminder(0, ARBITRARY_REMINDER_TEXT);
        reminders.add(reminder);
        RemindersRecyclerViewAdapter adapter = createAdapter(reminders);
        RecyclerView recyclerView = getRecyclerView();
        ReminderListViewHolder viewHolder = adapter.onCreateViewHolder(recyclerView, -1);

        adapter.onBindViewHolder(viewHolder, 0);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.reminders_text_view);
        assertEquals(reminderText.getText(), ARBITRARY_REMINDER_TEXT);
    }

    @Test
    public void givenMultipleRemindersWhenViewHolderIsBoundWithAnArbitraryPositionThenTheTextViewIsSet() {
        Reminder reminder = new Reminder(0, ARBITRARY_REMINDER_TEXT);
        reminders.add(reminder);

        String expectedReminder = ARBITRARY_REMINDER_TEXT + 1;
        Reminder secondReminder = new Reminder(1, expectedReminder);
        reminders.add(secondReminder);

        RemindersRecyclerViewAdapter adapter = new RemindersRecyclerViewAdapter(reminders, activity);
        RecyclerView recyclerView = getRecyclerView();
        ReminderListViewHolder viewHolder = adapter.onCreateViewHolder(recyclerView, -1);
        adapter.onBindViewHolder(viewHolder, 1);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.reminders_text_view);
        assertEquals(reminderText.getText(), ARBITRARY_REMINDER_TEXT + 1);
    }

    @Test
    public void whenTheRemindersAreSetThenObserversAreNotified() {
        Reminder reminder = new Reminder(0, ARBITRARY_REMINDER_TEXT);
        reminders.add(reminder);
        RemindersRecyclerViewAdapter adapter = createAdapter(reminders);

        List<Reminder> updatedReminders = new ArrayList<Reminder>();
        Reminder updatedReminder = new Reminder(0, ARBITRARY_REMINDER_TEXT + 1);
        updatedReminders.add(updatedReminder);

        RemindersRecyclerViewAdapter adapterSpy = spy(adapter);
        adapterSpy.setReminders(updatedReminders);

        verify(adapterSpy).notifyDataSetChanged();
    }

    @Test
    public void whenTheRemindersAreSetThenTheItemCountIsUpdated() {
        Reminder store = new Reminder(0, ARBITRARY_REMINDER_TEXT);
        reminders.add(store);
        RemindersRecyclerViewAdapter adapter = createAdapter(reminders);

        List<Reminder> updatedReminders = new ArrayList<Reminder>();
        Reminder updatedReminder1 = new Reminder(1, ARBITRARY_REMINDER_TEXT + 1);
        Reminder updatedReminder2 = new Reminder(2, ARBITRARY_REMINDER_TEXT + 2);
        updatedReminders.add(updatedReminder1);
        updatedReminders.add(updatedReminder2);

        adapter.setReminders(updatedReminders);

        assertEquals(updatedReminders.size(), adapter.getItemCount());
    }

    @Test
    public void whenAReminderIsRemovedThenItIsRemovedFromTheDatabase() {
        String description = "test";
        ContentValues reminderValues = new ReminderValuesBuilder().createDefaultReminderValues()
                .withDescription(description).build();
        Uri insertUri = activity.getContentResolver().insert(ReminderContract.Reminders.CONTENT_URI, reminderValues);

        reminders.add(new Reminder(Long.parseLong(insertUri.getLastPathSegment()), description));
        RemindersRecyclerViewAdapter adapter = createAdapter(reminders);

        adapter.removeReminders(new int[]{0});

        Cursor cursor = activity.getContentResolver()
                .query(ReminderContract.Reminders.CONTENT_URI, ReminderContract.Reminders.PROJECT_ALL, "", null, null);

        assertEquals(0, cursor.getCount());

        cursor.close();
        cursor = null;
    }
}
