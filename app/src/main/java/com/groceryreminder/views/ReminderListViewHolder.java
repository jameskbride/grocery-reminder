package com.groceryreminder.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.groceryreminder.R;
import com.groceryreminder.models.Reminder;

public class ReminderListViewHolder extends RecyclerView.ViewHolder{
    private final TextView reminderText;

    public ReminderListViewHolder(View itemView) {
        super(itemView);
        this.reminderText = (TextView)itemView.findViewById(R.id.reminders_text_view);
    }

    public void bind(Reminder reminder) {
        reminderText.setText(reminder.getText());
    }
}
