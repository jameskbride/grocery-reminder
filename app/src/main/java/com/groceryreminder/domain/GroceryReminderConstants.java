package com.groceryreminder.domain;

public class GroceryReminderConstants {

    public static final double LOCATION_SEARCH_RADIUS_METERS = 8046.72;

    public static final String ACTION_STORE_PROXIMITY_EVENT = "com.groceryreminder.STORE_PROXIMITY_EVENT";

    public static final int NOTIFICATION_PROXIMITY_ALERT = 1;
    public static final float LOCATION_GEOFENCE_RADIUS_METERS = 200f;
    public static final int MAXIMUM_ACCURACY_IN_METERS = 100;

    public static final long[] PROXIMITY_VIBRATION_PATTERN = {1000, 1000};

    public static final long MIN_LOCATION_UPDATE_TIME_MILLIS = 300000l;

    public static final String LAST_NOTIFIED_STORE_KEY = "LAST_STORE_ALERT_KEY";
    public static final String LAST_NOTIFICATION_TIME = "LAST_NOTIFICATION_TIME";
    public static final String LISTEN_FOR_GPS_EXTRA = "LISTEN_FOR_GPS_EXTRA";
    public static final String LAST_GOOGLE_PLACES_POLL_TIME = "LAST_GOOGLE_PLACES_POLL_TIME";
    public static final int NETWORK_MIN_UPDATE_TIME = 60000;
    public static final int PASSIVE_MIN_UPDATE_TIME = 0;
}
