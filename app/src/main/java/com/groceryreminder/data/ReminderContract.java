package com.groceryreminder.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ReminderContract {

    public static final String REMINDER_AUTHORITY = "com.groceryreminder.reminders";
    public static final Uri REMINDER_CONTENT_URI = Uri.parse("content://" + REMINDER_AUTHORITY);

    public static final String REMINDER_LOCATION_AUTHORITY = "com.groceryreminder.locations";
    public static final Uri REMINDER_LOCATIONS_CONTENT_URI = Uri.parse("content://" + REMINDER_LOCATION_AUTHORITY);

    public static final class Locations implements BaseColumns {

        public static final String NAME = "location_name";
        public static final String PLACES_ID = "places_id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String[] PROJECT_ALL = {_ID, NAME, PLACES_ID, LATITUDE, LONGITUDE};
        public static final String SORT_ORDER_DEFAULT = "";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ReminderContract.REMINDER_LOCATIONS_CONTENT_URI, "locations");
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/com.groceryreminder.reminders_locations";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/com.groceryreminder.reminders_locations";
    }

    public static final class Reminders implements BaseColumns {

        public static final String DESCRIPTION = "description";
        public static final String[] PROJECT_ALL = {_ID, DESCRIPTION};
        public static final String SORT_ORDER_DEFAULT = "";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ReminderContract.REMINDER_CONTENT_URI, "reminders");
    }
}
