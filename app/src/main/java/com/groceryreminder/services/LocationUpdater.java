package com.groceryreminder.services;

import android.location.Location;

public interface LocationUpdater {
    int SIGNIFICANT_LOCATION_TIME_DELTA = 60000;
    float SIGNIFICANT_LOCATION_ACCURACY_RATIO = .50f;

    void handleLocationUpdated(Location location);

    boolean isBetterThanCurrentLocation(Location location);

    boolean isAccurate(Location location);

    void listenForLocationUpdates(boolean listenForGPSUpdates);

    void removeGPSListener();
}
