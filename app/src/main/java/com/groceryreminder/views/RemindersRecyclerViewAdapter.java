package com.groceryreminder.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groceryreminder.R;
import com.groceryreminder.models.Reminder;

import java.util.List;

public class RemindersRecyclerViewAdapter extends RecyclerView.Adapter<ReminderListViewHolder> {

    private List<Reminder> reminders;

    public RemindersRecyclerViewAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    @Override
    public ReminderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipelist_view, parent, false);
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
}
