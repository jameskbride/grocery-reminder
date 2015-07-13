package com.groceryreminder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;

import com.groceryreminder.R;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreNotificationManager;

public class GroceryStoreBroadcastReceiver extends BroadcastReceiver {

    private final GroceryStoreNotificationManager groceryStoreNotificationManager = new GroceryStoreNotificationManager();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
            Cursor cursor = context.getContentResolver().query(ReminderContract.Reminders.CONTENT_URI, ReminderContract.Reminders.PROJECT_ALL, "", null, null);
            if (groceryStoreNotificationManager.remindersExist(cursor))
            {
                SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);
                String lastNotifiedStore = sharedPreferences.getString(GroceryReminderConstants.LAST_NOTIFIED_STORE_KEY, "");
                String currentStoreName = intent.getStringExtra(ReminderContract.Locations.NAME);

                long lastNotificationTime = sharedPreferences.getLong(GroceryReminderConstants.LAST_NOTIFICATION_TIME, 0);
                long currentTime = System.currentTimeMillis();

                if (!groceryStoreNotificationManager.noticeCanBeSent(lastNotifiedStore, currentStoreName, lastNotificationTime, currentTime)) {
                    return;
                }

                groceryStoreNotificationManager.sendNotification(context, intent);
                groceryStoreNotificationManager.saveNoticeDetails(sharedPreferences, currentStoreName, currentTime);
            }
        }
    }
}
