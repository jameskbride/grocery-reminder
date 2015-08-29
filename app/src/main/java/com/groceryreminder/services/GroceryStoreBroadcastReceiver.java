package com.groceryreminder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreLocationManagerInterface;
import com.groceryreminder.injection.ReminderObjectGraph;

import javax.inject.Inject;

public class GroceryStoreBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "StoreBroadcastReceiver";

    @Inject
    GroceryStoreLocationManagerInterface groceryStoreLocationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderObjectGraph.getInstance().inject(this);
        Log.d(TAG, "Receiving proximity alert.");
        Intent serviceIntent = new Intent(context, GroceryStoreNotificationService.class);

        Location lastKnownLocation = groceryStoreLocationManager.getLastKnownLocation();
        if (lastKnownLocation != null) {
            serviceIntent.putExtra(GroceryStoreLocationListener.PROVIDER, lastKnownLocation.getProvider());
            serviceIntent.putExtra(ReminderContract.Locations.LATITUDE, lastKnownLocation.getLatitude());
            serviceIntent.putExtra(ReminderContract.Locations.LONGITUDE, lastKnownLocation.getLongitude());

            context.startService(serviceIntent);
        }
    }
}
