package com.groceryreminder.domain;

import android.location.Location;

public interface GroceryStoreNotificationManagerInterface {
    void sendPotentialNotification(Location location, long currentTime);
}
