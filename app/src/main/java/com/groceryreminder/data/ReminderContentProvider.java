package com.groceryreminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ReminderContentProvider extends ContentProvider {

    private static final int LOCATION_LIST = 1;
    private static final int LOCATION_ITEM_ID = 2;
    private static final UriMatcher URI_MATCHER;
    private static final String LOCATIONS_URI_LIST_PATH = "locations";

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ReminderContract.AUTHORITY, LOCATIONS_URI_LIST_PATH, LOCATION_LIST);
        URI_MATCHER.addURI(ReminderContract.AUTHORITY, "locations/#", LOCATION_ITEM_ID);
    }

    private ReminderDBHelper reminderDBHelper;

    @Override
    public boolean onCreate() {
        reminderDBHelper = new ReminderDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

//        switch (URI_MATCHER.match(uri)) {
//            case MEAL_LIST:
//                queryBuilder.setTables(DBSchema.MEALS);
//                break;
//            case MEAL_ITEM_ID:
//                queryBuilder.setTables(DBSchema.MEALS);
//                queryBuilder.appendWhere(SkwPlanContract.Meals._ID + " = " + uri.getLastPathSegment());
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported URI: " + uri);
//        }
//
//        Cursor cursor = queryBuilder.query(planDBHelper.getReadableDatabase(), projection,
//                selection, selectionArgs, null, null, sortOrder);
//        cursor.setNotificationUri(getContext().getContentResolver(), SkwPlanContract.Meals.CONTENT_URI);
        queryBuilder.setTables(DBSchema.LOCATIONS);
        Cursor cursor = queryBuilder.query(reminderDBHelper.getReadableDatabase(), projection, null, null, null, null, null);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase writableDatabase = reminderDBHelper.getWritableDatabase();
        long id = writableDatabase.insert(DBSchema.LOCATIONS, null, values);

        Uri insertedUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(insertedUri, null);

        return insertedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase writableDatabase = reminderDBHelper.getWritableDatabase();
        int deletedCount = 0;

        switch(URI_MATCHER.match(uri))
        {
            case LOCATION_LIST:
                deletedCount = writableDatabase.delete(DBSchema.LOCATIONS, selection, selectionArgs);
                break;

            case LOCATION_ITEM_ID:
                String id = uri.getLastPathSegment();
                String whereClause = ReminderContract.Locations._ID + " = " + id;

                deletedCount = writableDatabase.delete(DBSchema.LOCATIONS, whereClause, selectionArgs);
                break;
        }

        if (deletedCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return deletedCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
