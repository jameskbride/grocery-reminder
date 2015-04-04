package com.groceryreminder.domain;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.injection.ForApplication;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;

public class GroceryStoreManager implements GroceryStoreManagerInterface {

    private static final String TAG = "GroceryStoreManager";
    @Inject
    GooglePlacesInterface googlePlaces;

    @Inject
    @ForApplication
    Application context;

    @Override
    public List<Place> findStoresByLocation(Location location) {
        Log.d(TAG, "Location: " + location);
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        List<Place> places = googlePlaces.getPlacesByRadar(location.getLatitude(), location.getLongitude(), GroceryReminderConstants.FIVE_MILES_IN_METERS, 50, groceryStoreType);

        return places;
    }

    @Override
    public void persistGroceryStores(List<Place> places) {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        for (Place place : places) {
            Log.d(TAG, "Found places");
            ContentValues values = BuildLocationContentValues(place);
            contentValuesList.add(values);
        }

        context.getContentResolver().bulkInsert(ReminderContract.Locations.CONTENT_URI, contentValuesList.toArray(new ContentValues[contentValuesList.size()]));
    }

    private ContentValues BuildLocationContentValues(Place place) {
        ContentValues values = new ContentValues();
        values.put(ReminderContract.Locations.NAME, place.getName());
        values.put(ReminderContract.Locations.PLACES_ID, place.getPlaceId());
        values.put(ReminderContract.Locations.LATITUDE, place.getLatitude());
        values.put(ReminderContract.Locations.LONGITUDE, place.getLongitude());

        return values;
    }

}
