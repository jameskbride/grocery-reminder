package com.groceryreminder.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;
import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

public class GroceryStoreNotificationService extends IntentService {

    public static final String TAG = "StoreNoticeService";
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
        Log.d(TAG, "Handling proximity alert.");
        long currentTime = System.currentTimeMillis();

        Location location = new Location(intent.getStringExtra(GroceryStoreLocationListener.PROVIDER));
        location.setLatitude(intent.getDoubleExtra(ReminderContract.Locations.LATITUDE, 0));
        location.setLongitude(intent.getDoubleExtra(ReminderContract.Locations.LONGITUDE, 0));

        groceryStoreNotificationManager.sendPotentialNotification(location, currentTime);
    }
}
