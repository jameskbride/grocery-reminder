package com.groceryreminder.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.injection.ReminderApplication;

import java.util.List;

import javax.inject.Inject;

import se.walkercrou.places.Place;

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
        Location location = getLastKnownLocation();
        if (location == null) {
            Log.d(TAG, "Last known location is null");
            return;
        }
        Log.d(TAG, "Last known location is: " + location);
        groceryStoreManager.deleteStoresByLocation(location);
        List<Place> updatedPlaces = groceryStoreManager.findStoresByLocation(location);
        List<Place> places = groceryStoreManager.filterPlacesByDistance(location, updatedPlaces, GroceryReminderConstants.FIVE_MILES_IN_METERS);

        Log.d(TAG, "Places count: " + places.size());
        groceryStoreManager.persistGroceryStores(places);
        addProximityAlerts(places);
    }

    private void addProximityAlerts(List<Place> places) {
        int requestCode = 0;
        for (Place place : places) {
            Intent proximityAlertIntent = new Intent(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT);
            locationManager.addProximityAlert(place.getLatitude(), place.getLongitude(),
                    GroceryReminderConstants.FIFTEEN_FEET_IN_METERS, GroceryReminderConstants.PROXIMITY_ALERT_EXPIRATION,
                    PendingIntent.getBroadcast(getApplicationContext(), requestCode++, proximityAlertIntent,
                            0));
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
