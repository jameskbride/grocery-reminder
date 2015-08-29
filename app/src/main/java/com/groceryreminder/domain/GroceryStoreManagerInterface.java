package com.groceryreminder.domain;

import android.location.Location;

import com.groceryreminder.services.LocationUpdater;

import java.util.List;

import se.walkercrou.places.Place;

public interface GroceryStoreManagerInterface extends LocationUpdater{

    int GOOGLE_PLACES_MAX_RESULTS = 40;

    void findStoresByLocation(Location location);

    List<Place> filterPlacesByDistance(Location location, List<Place> places, double distanceInMeters);

    void persistGroceryStores(List<Place> places);

    void deleteStoresByLocation(Location location);

    void addProximityAlerts(List<Place> places);

    void listenForLocationUpdates(boolean listenForGPSUpdates);

    Location getCurrentLocation();

    void removeGPSListener();

    void onStoreLocationsUpdated(Location location, List<Place> updatedPlaces);
}
