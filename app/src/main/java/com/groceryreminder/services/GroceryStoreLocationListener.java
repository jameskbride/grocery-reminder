package com.groceryreminder.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class GroceryStoreLocationListener implements LocationListener {

    private static final String TAG = "StoreLocationListener";
    private final LocationUpdater locationUpdater;

    public GroceryStoreLocationListener(LocationUpdater locationUpdater) {
        this.locationUpdater = locationUpdater;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Updated");
        locationUpdater.handleLocationUpdated(location);
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
