package com.groceryreminder.views.reminders;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.models.Reminder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReminderListViewHolderTest extends RobolectricTestBase {

    private ActivityController<RemindersActivity> activityController;
    private RemindersActivity activity;
    private List<Reminder> reminders;
    private OnReminderDataChangeListener onReminderDataChangeListenerMock;
    RemindersRecyclerViewAdapter recyclerViewAdapter;

    @Before
    public void setUp() {
        super.setUp();
        this.activityController = Robolectric.buildActivity(RemindersActivity.class);
        this.activity = activityController.create().start().get();
        reminders = new ArrayList<Reminder>();
        reminders.add(new Reminder(0, "test"));
        onReminderDataChangeListenerMock = mock(OnReminderDataChangeListener.class);
        recyclerViewAdapter = new RemindersRecyclerViewAdapter(reminders, onReminderDataChangeListenerMock);
    }

    @After
    public void tearDown() {
        activityController.pause().stop().destroy();
    }

    private RecyclerView getRecyclerView() {
        RecyclerView viewGroup = (RecyclerView)activity.findViewById(R.id.reminders_recycler_view);
        viewGroup.setLayoutManager(new LinearLayoutManager(activity));

        return viewGroup;
    }

    @Test
    public void whenAReminderIsBoundThenTheReminderTextIsSet() {
        RecyclerView recyclerView = getRecyclerView();
        View view = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.reminder_viewholder, recyclerView, false);

        ReminderListViewHolder viewHolder = new ReminderListViewHolder(view, recyclerViewAdapter);
        recyclerViewAdapter.onBindViewHolder(viewHolder, 0);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.reminders_text_view);
        assertEquals("test", reminderText.getText());
    }

    @Test
    public void whenTheClearIconIsTappedThenTheReminderIsDismissed() {
        RecyclerView recyclerView = getRecyclerView();
        View view = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.reminder_viewholder, recyclerView, false);

        ReminderListViewHolder viewHolder = new ReminderListViewHolder(view, recyclerViewAdapter);
        recyclerViewAdapter.onBindViewHolder(viewHolder, 0);

        ImageView clearIcon = (ImageView)viewHolder.itemView.findViewById(R.id.clear_icon);
        Reminder reminder = reminders.get(0);
        clearIcon.performClick();

        verify(onReminderDataChangeListenerMock).removeReminder(reminder);
    }
}
