package com.groceryreminder.services;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryReminderConstants;

public class GroceryStoreLocationListener implements LocationListener {

    private static final String TAG = "StoreLocationListener";
    public static final String PROVIDER = "provider";

    private Context context;
    private final LocationUpdater locationUpdater;

    public GroceryStoreLocationListener(Context context, LocationUpdater locationUpdater) {
        this.context = context;
        this.locationUpdater = locationUpdater;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Updated: " + location.getLatitude() + ",  " + location.getLongitude());
        Log.d(TAG, "Location Provider: " + location.getProvider());
        Log.d(TAG, "Location Accuracy: " + location.getAccuracy());
        if (locationUpdater.isAccurate(location)) {
            Intent serviceIntent = new Intent(context, GroceryStoreNotificationService.class);
            serviceIntent.putExtra(ReminderContract.Locations.LATITUDE, location.getLatitude());
            serviceIntent.putExtra(ReminderContract.Locations.LONGITUDE, location.getLongitude());
            serviceIntent.putExtra(PROVIDER, location.getProvider());
            context.startService(serviceIntent);
        }

        if (locationUpdater.isBetterThanCurrentLocation(location)) {
            locationUpdater.handleLocationUpdated(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
