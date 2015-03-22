package com.groceryreminder.domain;

import android.location.Location;

import java.util.List;

import se.walkercrou.places.Place;

public interface GroceryStoreManagerInterface {

    List<Place> findStoresByLocation(Location location);

    void persistGroceryStores(List<Place> places);
}
