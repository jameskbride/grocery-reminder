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

    @Override
    public void onReceive(Context context, Intent intent) {
        GroceryStoreNotificationManager groceryStoreNotificationManager = new GroceryStoreNotificationManager(context);
        if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {

            if (groceryStoreNotificationManager.remindersExist())
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
