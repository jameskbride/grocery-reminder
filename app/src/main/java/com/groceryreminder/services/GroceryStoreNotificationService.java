package com.groceryreminder.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.LocationManager;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreNotificationManager;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;
import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

public class GroceryStoreNotificationService extends IntentService {

    @Inject
    GroceryStoreNotificationManagerInterface groceryStoreNotificationManager;


    public GroceryStoreNotificationService() {
        super("GroceryNotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ReminderApplication)getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
            String currentStoreName = intent.getStringExtra(ReminderContract.Locations.NAME);
            long currentTime = System.currentTimeMillis();

            if (groceryStoreNotificationManager.noticeCanBeSent(currentStoreName, currentTime)) {
                groceryStoreNotificationManager.sendNotification(intent);
                groceryStoreNotificationManager.saveNoticeDetails(currentStoreName, currentTime);
            }
        }
    }
}
