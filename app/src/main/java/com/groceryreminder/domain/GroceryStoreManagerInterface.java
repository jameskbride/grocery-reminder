package com.groceryreminder.domain;

import android.location.Location;

import java.util.List;

import se.walkercrou.places.Place;

public interface GroceryStoreManagerInterface {

    List<Place> findStoresByLocation(Location location);

    List<Place> filterPlacesByDistance(Location location, List<Place> places, double distanceInMeters);

    void persistGroceryStores(List<Place> places);

    void deleteStoresByLocation(Location location);

    void addProximityAlerts(List<Place> places);
}
