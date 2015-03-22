package com.groceryreminder.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.injection.ReminderApplication;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;

public class GroceryLocatorService extends IntentService {


    private static final String TAG = "GroceryLocatorService";

    @Inject
    GooglePlacesInterface googlePlaces;

    @Inject
    LocationManager locationManager;

    @Inject
    GroceryStoreManagerInterface groceryStoreManager;


    public GroceryLocatorService(String name) {
        super(name);
        ((ReminderApplication)getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Location location = getLastKnownLocation();
        List<Place> places = groceryStoreManager.findStoresByLocation(location);

        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        for (Place place : places) {
            Log.d(TAG, "Found places");
            ContentValues values = BuildLocationContentValues(place);
            contentValuesList.add(values);
        }

        getContentResolver().bulkInsert(ReminderContract.Locations.CONTENT_URI, contentValuesList.toArray(new ContentValues[contentValuesList.size()]));
    }

    private Location getLastKnownLocation() {
        Criteria expectedCriteria = buildLocationSearchCriteria();
        String provider = locationManager.getBestProvider(expectedCriteria, true);
        Log.d(TAG, "Best provider is: " + provider);
        return locationManager.getLastKnownLocation(provider);
    }

    private ContentValues BuildLocationContentValues(Place place) {
        ContentValues values = new ContentValues();
        values.put(ReminderContract.Locations.NAME, place.getName());
        values.put(ReminderContract.Locations.PLACES_ID, place.getPlaceId());
        values.put(ReminderContract.Locations.LATITUDE, place.getLatitude());
        values.put(ReminderContract.Locations.LONGITUDE, place.getLongitude());

        return values;
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
