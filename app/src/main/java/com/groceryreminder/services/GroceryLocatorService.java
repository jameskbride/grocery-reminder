package com.groceryreminder.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.TypeParam;
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
        Location location = locationManager.getLastKnownLocation("provider");

        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        Param[] params = new Param[] {groceryStoreType};
        googlePlaces.getPlacesByRadar(location.getLatitude(), location.getLongitude(), FIVE_MILES_IN_METERS, 50, params);
    }
}
