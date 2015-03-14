package com.groceryreminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ReminderContentProvider extends ContentProvider {

    private ReminderDBHelper reminderDBHelper;

    @Override
    public boolean onCreate() {
        reminderDBHelper = new ReminderDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
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

        String id = uri.getLastPathSegment();
        String whereCaluse = ReminderContract.Locations._ID + " = " + id;

        int deletedCount = writableDatabase.delete(DBSchema.LOCATIONS, whereCaluse, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return deletedCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
