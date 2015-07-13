package com.groceryreminder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;
import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

public class GroceryStoreBroadcastReceiver extends BroadcastReceiver {

    @Inject
    GroceryStoreNotificationManagerInterface groceryStoreNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((ReminderApplication)context.getApplicationContext()).inject(this);
        if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
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
