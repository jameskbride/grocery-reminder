package com.groceryreminder.domain;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContentProvider;
import com.groceryreminder.data.ReminderContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowLocation;
import org.robolectric.shadows.ShadowLocationManager;

import java.util.ArrayList;
import java.util.List;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryStoreManagerTest extends RobolectricTestBase {

    private GroceryStoreManager groceryStoreManager;
    private ReminderContentProvider reminderProvider;
    private ShadowContentResolver shadowContentResolver;
    private GooglePlacesInterface googlePlacesMock;
    private LocationManager locationManager;
    private ShadowLocationManager shadowLocationManager;

    private Location defaultGPSLocation;

    @Before
    public void setUp() {
        super.setUp();

        googlePlacesMock = getTestReminderModule().getGooglePlaces();
        groceryStoreManager = new GroceryStoreManager(getTestAndroidModule().getApplicationContext(),
                googlePlacesMock);
        setupReminderContentProvider();
        setupLocationManager();
    }

    private void setupLocationManager() {
        this.locationManager = getTestAndroidModule().getLocationManager();
        this.shadowLocationManager = Robolectric.shadowOf(locationManager);
        try {
            assertTrue(shadowLocationManager.setBestProvider(LocationManager.GPS_PROVIDER, true, new ArrayList<Criteria>()));
        } catch (Exception e) {
            fail("Unable to set the best provider.");
        }

        this.defaultGPSLocation = createDefaultLocation(LocationManager.GPS_PROVIDER);
        ShadowLocation.setDistanceBetween(new float[] {(float) GroceryReminderConstants.FIVE_MILES_IN_METERS});
        shadowLocationManager.setLastKnownLocation(LocationManager.GPS_PROVIDER, defaultGPSLocation);
    }

    private void setupReminderContentProvider() {
        reminderProvider = new ReminderContentProvider();
        reminderProvider.onCreate();
        shadowContentResolver = Robolectric.shadowOf(getTestAndroidModule().getApplicationContext().getContentResolver());
        shadowContentResolver.registerProvider(ReminderContract.AUTHORITY, reminderProvider);
    }

    private Location createDefaultLocation(String provider) {
        Location location = new Location(provider);
        location.setLatitude(1);
        location.setLongitude(2);
        return location;
    }

    private Place createDefaultGooglePlace() {
        Place place = new Place();
        place.setName("test");
        place.setLatitude(0.0);
        place.setLongitude(1.1);
        place.setPlaceId("test_id");

        return place;
    }

    private void setCurrentDistanceGreaterThanFiveMiles() {
        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.FIVE_MILES_IN_METERS + 1});
    }

    @Test
    public void whenPlacesAreRequestedByLocationThenANearbySearchIsPerformed() {
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        ArgumentCaptor<Param> paramsCaptor = ArgumentCaptor.forClass(Param.class);

        groceryStoreManager.findStoresByLocation(defaultGPSLocation);

        verify(googlePlacesMock).getNearbyPlacesRankedByDistance(anyDouble(), anyDouble(), paramsCaptor.capture());

        Param actualParams = paramsCaptor.getValue();
        assertEquals(actualParams, groceryStoreType);
    }

    @Test
    public void givenDistanceIsGreaterThanFiveMilesWhenPlacesAreFilteredByLocationThenPlacesOutsideOfFiveMilesAreNotReturned() {
        setCurrentDistanceGreaterThanFiveMiles();
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        List<Place> actualPlaces = groceryStoreManager.filterPlacesByDistance(defaultGPSLocation, places, GroceryReminderConstants.FIVE_MILES_IN_METERS);
        assertTrue(actualPlaces.isEmpty());
    }

    @Test
    public void givenPlacesWhenTheyArePersistedThenTheyCanBeRetrieved() {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        groceryStoreManager.persistGroceryStores(places);

        Cursor cursor = reminderProvider.query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, "", null, null);
        assertEquals(1, cursor.getCount());
    }

    @Test
    public void givenPersistedPlacesWhichAreMoreThanFiveMilesDistanceWhenTheIntentIsHandledThenTheDistancePlacesAreDeleted() {
        ShadowLocation.setDistanceBetween(new float[] {(float)GroceryReminderConstants.FIVE_MILES_IN_METERS + 1});

        ContentValues values = new ContentValues();
        values.put(ReminderContract.Locations.NAME, "test");
        values.put(ReminderContract.Locations.PLACES_ID, "test");
        values.put(ReminderContract.Locations.LATITUDE, 1);
        values.put(ReminderContract.Locations.LONGITUDE, 2);
        shadowContentResolver.insert(ReminderContract.Locations.CONTENT_URI, values);

        values.put(ReminderContract.Locations.PLACES_ID, "test2");
        shadowContentResolver.insert(ReminderContract.Locations.CONTENT_URI, values);

        groceryStoreManager.deleteStoresByLocation(defaultGPSLocation);

        Cursor cursor = reminderProvider.query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, "", null, null);
        assertEquals(0, cursor.getCount());
    }
}
