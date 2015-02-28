package com.groceryreminder.views;

import android.support.v7.widget.RecyclerView;

import com.groceryreminder.models.Reminder;

import java.util.List;

import brnunes.swipeablecardview.SwipeableRecyclerViewTouchListener;

public class ReminderSwipeListener implements SwipeableRecyclerViewTouchListener.SwipeListener {

    private final List<Reminder> reminders;
    private final RecyclerView.Adapter adapter;

    public ReminderSwipeListener(List<Reminder> reminders, RecyclerView.Adapter adapter) {
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
        for (int position : reverseSortedPositions) {
            reminders.remove(position);
            adapter.notifyItemRemoved(position);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
        dismissReminder(reverseSortedPositions);
    }
}
