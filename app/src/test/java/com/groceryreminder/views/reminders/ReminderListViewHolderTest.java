package com.groceryreminder.views.reminders;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.models.Reminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReminderListViewHolderTest {

    private RemindersActivity activity;

    @Before
    public void setUp() {
        this.activity = Robolectric.buildActivity(RemindersActivity.class).create().start().get();
    }

    @Test
    public void givenRemindersListViewWhenAReminderIsBoundThenTheReminderTextIsSet() {
        RecyclerView recyclerView = getRecyclerView();
        View view = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.reminders_swipelist, recyclerView, false);
        Reminder reminder = new Reminder(0, "test");

        ReminderListViewHolder viewHolder = new ReminderListViewHolder(view);
        viewHolder.bind(reminder);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.reminders_text_view);
        assertEquals("test", reminderText.getText());
    }

    private RecyclerView getRecyclerView() {
        RecyclerView viewGroup = (RecyclerView)activity.findViewById(R.id.reminders_recycler_view);
        viewGroup.setLayoutManager(new LinearLayoutManager(activity));

        return viewGroup;
    }
}
