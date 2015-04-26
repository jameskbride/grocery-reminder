package com.groceryreminder.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GroceryStoreLocationListener implements LocationListener {

    private final LocationUpdater locationUpdater;

    public GroceryStoreLocationListener(LocationUpdater locationUpdater) {
        this.locationUpdater = locationUpdater;
    }

    @Override
    public void onLocationChanged(Location location) {
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
