package com.groceryreminder.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.groceryreminder.domain.GroceryReminderConstants;

public class GroceryStoreLocationListener implements LocationListener {

    private static final String TAG = "StoreLocationListener";

    private final LocationUpdater locationUpdater;

    public GroceryStoreLocationListener(LocationUpdater locationUpdater) {
        this.locationUpdater = locationUpdater;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Updated: " + location.getLatitude() + ",  " + location.getLongitude());
        Log.d(TAG, "Location Provider: " + location.getProvider());
        Log.d(TAG, "Location Accuracy: " + location.getAccuracy());
        if (location.getAccuracy() <= GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS) {
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
