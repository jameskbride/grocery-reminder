package com.groceryreminder.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreLocationManagerInterface;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

public class GroceryLocatorService extends IntentService {

    private static final String TAG = "GroceryLocatorService";

    @Inject
    GroceryStoreLocationManagerInterface groceryStoreLocationManager;

    @Inject
    GroceryStoreManagerInterface groceryStoreManager;

    public GroceryLocatorService() {
        super("GroceryLocatorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ReminderApplication)getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "In onHandleIntent");
        if (intent.getBooleanExtra(GroceryReminderConstants.LISTEN_FOR_GPS_EXTRA, false)) {
            groceryStoreManager.listenForLocationUpdates(true);
        } else {
            groceryStoreManager.listenForLocationUpdates(false);
        }
        Location location = groceryStoreLocationManager.getLastKnownLocation();
        if (location != null && groceryStoreManager.isBetterThanCurrentLocation(location)) {
            groceryStoreManager.handleLocationUpdated(location);
        }
    }
}
