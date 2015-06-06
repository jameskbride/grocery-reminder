package com.groceryreminder.views.reminders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.groceryreminder.R;
import com.groceryreminder.models.Reminder;

public class ReminderListViewHolder extends RecyclerView.ViewHolder{
    private TextView reminderText;
    private ImageView clearImageView;
    private RemindersRecyclerViewAdapter adapter;

    public ReminderListViewHolder(View itemView, RemindersRecyclerViewAdapter adapter) {
        super(itemView);
        this.reminderText = (TextView)itemView.findViewById(R.id.reminders_text_view);
        this.clearImageView = (ImageView)itemView.findViewById(R.id.clear_icon);
        this.adapter = adapter;
    }

    public void bind(Reminder reminder, final int index) {
        reminderText.setText(reminder.getText());
        clearImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.removeReminders(new int[] {index});
            }
        });
    }
}
