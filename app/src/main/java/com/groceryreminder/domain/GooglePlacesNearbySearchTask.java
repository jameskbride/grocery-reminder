package com.groceryreminder.domain;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;
import se.walkercrou.places.exception.GooglePlacesException;

class GooglePlacesNearbySearchTask extends AsyncTask<Location, Integer, List<Place>> {

    private GooglePlacesInterface googlePlaces;
    private GroceryStoreManagerInterface groceryStoreManager;

    private static final String TAG = "GooglePlacesTask";

    public GooglePlacesNearbySearchTask(GooglePlacesInterface googlePlaces, GroceryStoreManagerInterface groceryStoreManager) {
        this.googlePlaces = googlePlaces;
        this.groceryStoreManager = groceryStoreManager;
    }

    @Override
    protected List<Place> doInBackground(Location... params) {
        Location location = params[0];
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        googlePlaces.setDebugModeEnabled(true);
        List<Place> places =  new ArrayList<Place>();
        try {
            places = googlePlaces.getNearbyPlacesRankedByDistance(location.getLatitude(), location.getLongitude(), GroceryStoreManagerInterface.GOOGLE_PLACES_MAX_RESULTS, groceryStoreType);

        } catch (GooglePlacesException e) {
            Log.e(TAG, "An error occurred when searching for stores.", e);
        }

        Log.d("PlacesSearchTask", "Executed search: " + places.size());
        groceryStoreManager.onStoreLocationsUpdated(location, places);

        return places;
    }
}
