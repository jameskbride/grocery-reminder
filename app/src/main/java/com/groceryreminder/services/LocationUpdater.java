package com.groceryreminder.services;

import android.location.Location;

public interface LocationUpdater {
    int SIGNIFICANT_LOCATION_TIME_DELTA = 60000;

    void handleLocationUpdated(Location location);

    boolean isBetterThanCurrentLocation(Location location);
}
