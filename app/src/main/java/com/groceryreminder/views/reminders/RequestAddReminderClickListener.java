package com.groceryreminder.views.reminders;

import android.util.Log;
import android.view.View;

class RequestAddReminderClickListener implements View.OnClickListener {
    private final OnAddReminderRequestListener onAddReminderRequestListener;

    public RequestAddReminderClickListener(OnAddReminderRequestListener onAddReminderRequestListener) {
        this.onAddReminderRequestListener = onAddReminderRequestListener;
    }

    @Override
    public void onClick(View v) {
        Log.d("RequestAddReminderClickListener", "In the click listener: " + onAddReminderRequestListener.toString());
        onAddReminderRequestListener.requestNewReminder();
    }
}
