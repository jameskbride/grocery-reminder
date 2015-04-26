package com.groceryreminder.domain;

import android.app.Application;
import android.app.PendingIntent;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.injection.ForApplication;
import com.groceryreminder.services.GroceryStoreLocationListener;
import com.groceryreminder.services.LocationUpdater;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;

public class GroceryStoreManager implements GroceryStoreManagerInterface {

    private static final String TAG = "StoreManager";
    private final LocationManager locationManager;
    private GooglePlacesInterface googlePlaces;
    private Application context;
    private LocationListener locationListener;

    @Inject
    public GroceryStoreManager(@ForApplication Application applicationContext, LocationManager locationManager, GooglePlacesInterface googlePlaces) {
        this.context = applicationContext;
        this.locationManager = locationManager;
        this.googlePlaces = googlePlaces;
    }

    @Override
    public List<Place> findStoresByLocation(Location location) {
        Log.d(TAG, "Location: " + location);
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        googlePlaces.setDebugModeEnabled(true);
        List<Place> places = googlePlaces.getNearbyPlacesRankedByDistance(location.getLatitude(), location.getLongitude(), groceryStoreType);

        return places;
    }

    @Override
    public List<Place> filterPlacesByDistance(Location location, List<Place> places, double distanceInMeters) {
        List<Place> filteredPlaces = new ArrayList<Place>();
        for (Place place : places) {
            float[] distanceArray = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), place.getLatitude(), place.getLongitude(), distanceArray);
            if (distanceArray[0] <= (float) distanceInMeters) {
                filteredPlaces.add(place);
            }
        }
        return filteredPlaces;
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

        applyBatchOperations(operations);

    }

    @Override
    public void deleteStoresByLocation(Location location) {
        Cursor cursor = context.getContentResolver().query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, null, null, ReminderContract.Locations.SORT_ORDER_DEFAULT);

        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        while (cursor.moveToNext()) {
            float[] distanceArray = new float[1];
            double latitude = Double.parseDouble(cursor.getString((cursor.getColumnIndex(ReminderContract.Locations.LATITUDE))));
            double longitude = Double.parseDouble(cursor.getString((cursor.getColumnIndex(ReminderContract.Locations.LONGITUDE))));
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), latitude, longitude, distanceArray);

            if (distanceArray[0] > (float) GroceryReminderConstants.FIVE_MILES_IN_METERS) {
                Uri deletionUri = ContentUris.withAppendedId(ReminderContract.Locations.CONTENT_URI, cursor.getInt(0));
                operations.add(ContentProviderOperation.newDelete(deletionUri).build());
            }
        }

        applyBatchOperations(operations);
    }

    @Override
    public void addProximityAlerts(List<Place> places) {
        int requestCode = 0;
        for (Place place : places) {
            Intent proximityAlertIntent = new Intent(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT);
            locationManager.addProximityAlert(place.getLatitude(), place.getLongitude(),
                    GroceryReminderConstants.FIFTY_FEET_IN_METERS, GroceryReminderConstants.PROXIMITY_ALERT_EXPIRATION,
                    PendingIntent.getBroadcast(context, requestCode++, proximityAlertIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT));
        }
    }

    @Override
    public void listenForLocationUpdates() {
        if (this.locationListener == null) {
            this.locationListener = createLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME, (float)GroceryReminderConstants.FIVE_MILES_IN_METERS, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME, (float)GroceryReminderConstants.FIVE_MILES_IN_METERS, locationListener);
        }
    }

    private LocationListener createLocationListener() {
        return new GroceryStoreLocationListener(this);
    }

    private void applyBatchOperations(ArrayList<ContentProviderOperation> operations) {
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

    @Override
    public void handleLocationUpdated(Location location) {
        Log.d(TAG, "Hitting the handleLocationUpdated");
        deleteStoresByLocation(location);
        List<Place> updatedPlaces = findStoresByLocation(location);
        List<Place> places = filterPlacesByDistance(location, updatedPlaces, GroceryReminderConstants.FIVE_MILES_IN_METERS);

        Log.d(TAG, "Places count: " + places.size());
        persistGroceryStores(places);
        addProximityAlerts(places);
    }
}
