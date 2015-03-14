package com.groceryreminder.data;

import android.provider.BaseColumns;

public class ReminderContract {
    
    public static final class Locations implements BaseColumns {

        public static final String NAME = "location_name";
        public static final String PLACES_ID = "places_id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String[] PROJECT_ALL = {_ID, NAME, PLACES_ID, LATITUDE, LONGITUDE};
        public static final String SORT_ORDER_DEFAULT = "";
    }
}
