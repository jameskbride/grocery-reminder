package com.groceryreminder.domain;

import android.location.Location;

public class GroceryStoreLocationManagerFake implements GroceryStoreLocationManagerInterface{

    private Location locationResponse;

    public void setLocationResponse(Location location) {
        locationResponse = location;
    }

    @Override
    public Location getLastKnownLocation() {
        return locationResponse;
    }
}
