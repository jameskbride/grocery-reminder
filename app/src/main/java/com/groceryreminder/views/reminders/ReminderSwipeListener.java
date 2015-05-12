package com.groceryreminder.views.reminders;

import android.support.v7.widget.RecyclerView;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.groceryreminder.models.Reminder;

import java.util.List;

public class ReminderSwipeListener implements SwipeableRecyclerViewTouchListener.SwipeListener {

    private final List<Reminder> reminders;
    private final RemindersRecyclerViewAdapter adapter;

    public ReminderSwipeListener(List<Reminder> reminders, RemindersRecyclerViewAdapter adapter) {
        this.reminders = reminders;
        this.adapter = adapter;
    }

    @Override
    public boolean canSwipe(int position) {
        return true;
    }

    @Override
    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
        dismissReminder(reverseSortedPositions);
    }

    private void dismissReminder(int[] reverseSortedPositions) {
        adapter.removeReminders(reverseSortedPositions);
    }

    @Override
    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
        dismissReminder(reverseSortedPositions);
    }
}
