package com.groceryreminder.views.reminders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groceryreminder.R;
import com.groceryreminder.models.Reminder;

import java.util.List;

public class RemindersRecyclerViewAdapter extends RecyclerView.Adapter<ReminderListViewHolder> {

    private List<Reminder> reminders;
    private OnReminderDataChangeListener onReminderDataChangeListener;

    public RemindersRecyclerViewAdapter(List<Reminder> reminders, OnReminderDataChangeListener onReminderDataChangeListener) {
        this.reminders = reminders;
        this.onReminderDataChangeListener = onReminderDataChangeListener;
    }

    @Override
    public ReminderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_viewholder, parent, false);
        return new ReminderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReminderListViewHolder holder, int position) {
        holder.bind(reminders.get(position));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public void setReminders(List<Reminder> updatedReminders) {
        reminders.clear();
        reminders.addAll(updatedReminders);
        notifyDataSetChanged();
    }

    public void removeReminders(int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            Reminder reminder = reminders.remove(position);
            onReminderDataChangeListener.removeReminder(reminder);
            notifyItemRemoved(position);
        }
        notifyDataSetChanged();
    }
}
