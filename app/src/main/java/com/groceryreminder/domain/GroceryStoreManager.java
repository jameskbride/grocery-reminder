package com.groceryreminder.domain;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;

public class GroceryStoreManager implements GroceryStoreManagerInterface {

    @Inject
    GooglePlacesInterface googlePlaces;

    @Inject
    LocationManager locationManager;

    @Override
    public List<Place> findStoresByLocation(Location location) {
        Log.d("GroceryStoreManager", "Location: " + location);
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        List<Place> places = googlePlaces.getPlacesByRadar(location.getLatitude(), location.getLongitude(), GroceryReminderConstants.FIVE_MILES_IN_METERS, 50, groceryStoreType);

        return places;
    }
}
