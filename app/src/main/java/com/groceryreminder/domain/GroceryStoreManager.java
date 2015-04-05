package com.groceryreminder.domain;

import android.app.Application;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.location.Location;
import android.os.RemoteException;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.injection.ForApplication;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;

public class GroceryStoreManager implements GroceryStoreManagerInterface {

    private static final String TAG = "StoreManager";
    @Inject
    GooglePlacesInterface googlePlaces;

    @Inject
    @ForApplication
    Application context;

    @Override
    public List<Place> findStoresByLocation(Location location) {
        Log.d(TAG, "Location: " + location);
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        List<Place> places = googlePlaces.getNearbyPlacesRankedByDistance(location.getLatitude(), location.getLongitude(), groceryStoreType);
        googlePlaces.setDebugModeEnabled(true);
        return places;
    }

    @Override
    public void persistGroceryStores(List<Place> places) {
        List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
        for (Place place : places) {
            Log.d(TAG, "Found places");
            ContentValues values = BuildLocationContentValues(place);
            contentValuesList.add(values);
        }

        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        for (ContentValues contentValues : contentValuesList) {
            operations.add(ContentProviderOperation.newInsert(ReminderContract.Locations.CONTENT_URI)
                    .withValues(contentValues).build()
            );
        }
        try {
            context.getContentResolver().applyBatch(ReminderContract.AUTHORITY, operations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    private ContentValues BuildLocationContentValues(Place place) {
        ContentValues values = new ContentValues();
        values.put(ReminderContract.Locations.NAME, place.getName());
        Log.d(TAG, "Place from service call: " + place.getName());
        values.put(ReminderContract.Locations.PLACES_ID, place.getPlaceId());
        values.put(ReminderContract.Locations.LATITUDE, place.getLatitude());
        values.put(ReminderContract.Locations.LONGITUDE, place.getLongitude());

        return values;
    }

}
