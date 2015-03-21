package com.groceryreminder.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.injection.ReminderApplication;
import com.groceryreminder.models.Reminder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;

public class GroceryLocatorService extends IntentService {

    public static final double FIVE_MILES_IN_METERS = 8046.72;

    @Inject
    GooglePlacesInterface googlePlaces;

    @Inject
    LocationManager locationManager;

    public GroceryLocatorService(String name) {
        super(name);
        ((ReminderApplication)getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Criteria expectedCriteria = createLocationSearchCriteria();

        String provider = locationManager.getBestProvider(expectedCriteria, true);

        Location location = locationManager.getLastKnownLocation("provider");

        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        List<Place> places = googlePlaces.getPlacesByRadar(location.getLatitude(), location.getLongitude(), FIVE_MILES_IN_METERS, 50, groceryStoreType);

        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();

        for (Place place : places) {
            Log.d("GroceryLocatorService", "Found places");
            ContentValues values = new ContentValues();
            values.put(ReminderContract.Locations.NAME, place.getName());
            values.put(ReminderContract.Locations.PLACES_ID, place.getPlaceId());
            values.put(ReminderContract.Locations.LATITUDE, place.getLatitude());
            values.put(ReminderContract.Locations.LONGITUDE, place.getLongitude());
            contentValuesList.add(values);
        }

        getContentResolver().bulkInsert(ReminderContract.Locations.CONTENT_URI, contentValuesList.toArray(new ContentValues[contentValuesList.size()]));
    }

    private Criteria createLocationSearchCriteria() {
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
