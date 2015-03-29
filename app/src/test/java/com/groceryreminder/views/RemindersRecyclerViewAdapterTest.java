package com.groceryreminder.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.groceryreminder.R;
import com.groceryreminder.models.Reminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class RemindersRecyclerViewAdapterTest {

    private static final String ARBITRARY_REMINDER_TEXT = "test";
    private RemindersActivity activity;
    private List<Reminder> reminders;

    @Before
    public void setUp() {
        this.activity = Robolectric.buildActivity(RemindersActivity.class).create().start().get();
        this.reminders = new ArrayList<Reminder>();
    }

    @Test
    public void whenTheAdapterIsCreatedWithRemindersThenTheItemCountIsSet() {
        Reminder reminder = new Reminder(ARBITRARY_REMINDER_TEXT);
        reminders.add(reminder);
        RemindersRecyclerViewAdapter adapter = createAdapter(reminders);

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void whenTheViewHolderIsCreatedThenTheReminderListViewHolderIsNotNull() {
        RecyclerView viewGroup = getRecyclerView();
        RemindersRecyclerViewAdapter adapter = new RemindersRecyclerViewAdapter(reminders);

        ReminderListViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, -1);

        assertNotNull(viewHolder);
    }

    @Test
    public void givenAReminderWhenTheViewHolderIsBoundThenTheTextViewIsSet() {
        Reminder reminder = new Reminder(ARBITRARY_REMINDER_TEXT);
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
        Reminder reminder = new Reminder(ARBITRARY_REMINDER_TEXT);
        reminders.add(reminder);

        String expectedReminder = ARBITRARY_REMINDER_TEXT + 1;
        Reminder secondReminder = new Reminder(expectedReminder);
        reminders.add(secondReminder);

        RemindersRecyclerViewAdapter adapter = new RemindersRecyclerViewAdapter(reminders);
        RecyclerView recyclerView = getRecyclerView();
        ReminderListViewHolder viewHolder = adapter.onCreateViewHolder(recyclerView, -1);
        adapter.onBindViewHolder(viewHolder, 1);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.reminders_text_view);
        assertEquals(reminderText.getText(), ARBITRARY_REMINDER_TEXT + 1);
    }

    private RemindersRecyclerViewAdapter createAdapter(List<Reminder> reminders) {
        return new RemindersRecyclerViewAdapter(reminders);
    }

    private RecyclerView getRecyclerView() {
        RecyclerView viewGroup = (RecyclerView)activity.findViewById(R.id.reminders_recycler_view);
        viewGroup.setLayoutManager(new LinearLayoutManager(activity));

        return viewGroup;
    }

}
