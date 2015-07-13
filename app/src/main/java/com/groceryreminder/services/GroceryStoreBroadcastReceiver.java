package com.groceryreminder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreNotificationManager;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;

public class GroceryStoreBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerInterface = new GroceryStoreNotificationManager(context);
        if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {

            if (groceryStoreNotificationManagerInterface.remindersExist())
            {
                String currentStoreName = intent.getStringExtra(ReminderContract.Locations.NAME);
                long currentTime = System.currentTimeMillis();

                if (!groceryStoreNotificationManagerInterface.noticeCanBeSent(currentStoreName, currentTime)) {
                    return;
                }

                groceryStoreNotificationManagerInterface.sendNotification(intent);
                groceryStoreNotificationManagerInterface.saveNoticeDetails(currentStoreName, currentTime);
            }
        }
    }
}
