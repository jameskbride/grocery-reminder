package com.groceryreminder.domain;

import android.location.Location;

import java.util.List;

import se.walkercrou.places.Place;

/**
 * Created by jim on 3/22/15.
 */
public interface GroceryStoreManagerInterface {
    List<Place> findStoresByLocation(Location location);
}
