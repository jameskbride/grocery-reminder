package com.groceryreminder.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

public class GroceryLocatorService extends IntentService {


    private static final String TAG = "GroceryLocatorService";

    @Inject
    LocationManager locationManager;

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
        groceryStoreManager.listenForLocationUpdates();
        Location location = getLastKnownLocation();
        if (location != null && location.getAccuracy() <= GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS) {
            groceryStoreManager.handleLocationUpdated(location);
        }
    }

    private Location getLastKnownLocation() {
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
