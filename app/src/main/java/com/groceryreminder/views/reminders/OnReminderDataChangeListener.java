package com.groceryreminder.views.reminders;

import com.groceryreminder.models.Reminder;

public interface OnReminderDataChangeListener {

    public void addReminder(String reminder);
    public int removeReminder(Reminder reminder);
}
