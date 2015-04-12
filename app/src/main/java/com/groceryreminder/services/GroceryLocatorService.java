package com.groceryreminder.services;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.injection.ReminderApplication;
import com.groceryreminder.models.Reminder;

import org.apache.http.client.utils.URIBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import se.walkercrou.places.Place;

public class GroceryLocatorService extends IntentService {


    private static final String TAG = "GroceryLocatorService";

    @Inject
    LocationManager locationManager;

    @Inject
    GroceryStoreManagerInterface groceryStoreManager;

    public GroceryLocatorService() {
        super("GroceryLocatorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ReminderApplication)getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "In onHandleIntent");
        Location location = getLastKnownLocation();
        Log.d(TAG, "Last know location is: " + location);
        groceryStoreManager.deleteStoresBeyondLocationRange(location);
        List<Place> places = groceryStoreManager.findStoresByLocation(location);
        Log.d(TAG, "Places count: " + places.size());
        groceryStoreManager.persistGroceryStores(places);
    }

    private Location getLastKnownLocation() {
        Criteria expectedCriteria = buildLocationSearchCriteria();
        String provider = locationManager.getBestProvider(expectedCriteria, true);
        Log.d(TAG, "Best provider is: " + provider);
        return locationManager.getLastKnownLocation(provider);
    }

    private Criteria buildLocationSearchCriteria() {
        Criteria expectedCriteria = new Criteria();
        expectedCriteria.setCostAllowed(true);
        expectedCriteria.setSpeedRequired(true);
        expectedCriteria.setAltitudeRequired(false);
        expectedCriteria.setBearingRequired(false);
        expectedCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        expectedCriteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        expectedCriteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
        expectedCriteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        expectedCriteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);

        return expectedCriteria;
    }
}
