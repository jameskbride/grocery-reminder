package com.groceryreminder.services;

import android.location.Location;

public interface LocationUpdater {
    void handleLocationUpdated(Location location);
}
