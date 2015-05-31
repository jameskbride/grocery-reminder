package com.groceryreminder.domain;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.groceryreminder.services.GroceryLocatorService;

import javax.inject.Inject;

public class GroceryStoreLocationManager implements GroceryStoreLocationManagerInterface {

    private static final String TAG = "GroceryStoreLocManager";

    LocationManager locationManager;

    @Inject
    public GroceryStoreLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    @Override
    public Location getLastKnownLocation() {
        Criteria expectedCriteria = buildLocationSearchCriteria();
        String provider = locationManager.getBestProvider(expectedCriteria, true);
        Log.d(TAG, "Best provider is: " + provider);
        if (provider == null) {
            Log.d(TAG, "No providers found");
            return null;
        }

        return locationManager.getLastKnownLocation(provider);
    }

    private Criteria buildLocationSearchCriteria() {
        Criteria expectedCriteria = new Criteria();
        expectedCriteria.setCostAllowed(true);
        expectedCriteria.setSpeedRequired(true);
        expectedCriteria.setAltitudeRequired(false);
        expectedCriteria.setBearingRequired(false);
        expectedCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        expectedCriteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        expectedCriteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
        expectedCriteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        expectedCriteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);

        return expectedCriteria;
    }
}