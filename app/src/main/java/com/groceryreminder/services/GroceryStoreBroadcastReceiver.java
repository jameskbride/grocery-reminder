package com.groceryreminder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreNotificationManager;

public class GroceryStoreBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GroceryStoreNotificationManager groceryStoreNotificationManager = new GroceryStoreNotificationManager(context);
        if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {

            if (groceryStoreNotificationManager.remindersExist())
            {
                String currentStoreName = intent.getStringExtra(ReminderContract.Locations.NAME);
                long currentTime = System.currentTimeMillis();

                if (!groceryStoreNotificationManager.noticeCanBeSent(currentStoreName, currentTime)) {
                    return;
                }

                groceryStoreNotificationManager.sendNotification(intent);
                groceryStoreNotificationManager.saveNoticeDetails(currentStoreName, currentTime);
            }
        }
    }
}
