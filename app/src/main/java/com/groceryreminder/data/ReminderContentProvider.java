package com.groceryreminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ReminderContentProvider  extends ContentProvider{
    private ReminderDBHelper reminderDBHelper;

    private static final int REMINDER_LIST = 1;
    private static final int REMINDER_ITEM_ID = 2;
    private static final UriMatcher URI_MATCHER;
    private static final String REMINDERS_URI_LIST_PATH = "reminders";

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(ReminderContract.AUTHORITY, REMINDERS_URI_LIST_PATH, REMINDER_LIST);
        URI_MATCHER.addURI(ReminderContract.AUTHORITY, "reminders/#", REMINDER_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        reminderDBHelper = new ReminderDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(DBSchema.REMINDERS);
        Cursor cursor = queryBuilder.query(reminderDBHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);

        //Cannot currently test-drive this line: minSdk must be 19, currently set to 15
        cursor.setNotificationUri(getContext().getContentResolver(), ReminderContract.Locations.CONTENT_URI);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase writableDatabase = reminderDBHelper.getWritableDatabase();
        long id = writableDatabase.insertWithOnConflict(DBSchema.REMINDERS, null, values, SQLiteDatabase.CONFLICT_NONE);

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
            case REMINDER_LIST:
                deletedCount = writableDatabase.delete(DBSchema.REMINDERS, selection, selectionArgs);
                break;

            case REMINDER_ITEM_ID:
                String id = uri.getLastPathSegment();
                String whereClause = ReminderContract.Reminders._ID + " = " + id;

                deletedCount = writableDatabase.delete(DBSchema.REMINDERS, whereClause, selectionArgs);
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
