package com.groceryreminder.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ReminderContract {


    private static final String AUTHORITY = "com.groceryreminder.reminders";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Locations implements BaseColumns {

        public static final String NAME = "location_name";
        public static final String PLACES_ID = "places_id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String[] PROJECT_ALL = {_ID, NAME, PLACES_ID, LATITUDE, LONGITUDE};
        public static final String SORT_ORDER_DEFAULT = "";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ReminderContract.CONTENT_URI, "locations");
    }
}
