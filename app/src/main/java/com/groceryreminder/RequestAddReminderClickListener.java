package com.groceryreminder;

import android.util.Log;
import android.view.View;

/**
* Created by jim on 2/14/15.
*/
class RequestAddReminderClickListener implements View.OnClickListener {
    private final OnAddReminderRequestListener onAddReminderRequestListener;

    public RequestAddReminderClickListener(OnAddReminderRequestListener onAddReminderRequestListener) {
        this.onAddReminderRequestListener = onAddReminderRequestListener;
    }

    @Override
    public void onClick(View v) {
        Log.d("FAB", "In the click listener: " + onAddReminderRequestListener.toString());
        onAddReminderRequestListener.requestNewReminder();
    }
}
