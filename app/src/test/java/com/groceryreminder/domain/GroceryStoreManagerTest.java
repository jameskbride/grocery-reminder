package com.groceryreminder.domain;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContentProvider;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.shadows.ShadowLocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowLocation;
import org.robolectric.shadows.ShadowPendingIntent;

import java.util.ArrayList;
import java.util.List;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowLocationManager.class})
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
        googlePlacesMock = getTestRemoteResourcesModule().getGooglePlaces();
        setupLocationManager();
        groceryStoreManager = new GroceryStoreManager(getTestAndroidModule().getApplicationContext(),
                locationManager,
                googlePlacesMock);
        setupReminderContentProvider();
    }

    private void setupLocationManager() {
        this.locationManager = getTestAndroidModule().getLocationManager();
        this.shadowLocationManager = (com.groceryreminder.shadows.ShadowLocationManager)Shadows.shadowOf(locationManager);
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
        shadowContentResolver = Shadows.shadowOf(getTestAndroidModule().getApplicationContext().getContentResolver());
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

    @Test
    public void whenProximityAlertIsAddedThenTheRadiusIsSetToFiftyFeetInMeters() {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        groceryStoreManager.addProximityAlerts(places);

        com.groceryreminder.shadows.ShadowLocationManager.ProximityAlert proximityAlert = shadowLocationManager.getProximityAlert(place.getLatitude(), place.getLongitude());

        assertEquals(GroceryReminderConstants.FIFTEEN_FEET_IN_METERS, proximityAlert.getRadius(), 0.001);
    }

    @Test
    public void whenProximityAlertIsAddedThenTheExpirationDoesNotExpire() {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        groceryStoreManager.addProximityAlerts(places);

        com.groceryreminder.shadows.ShadowLocationManager.ProximityAlert proximityAlert = shadowLocationManager.getProximityAlert(place.getLatitude(), place.getLongitude());

        assertEquals(GroceryReminderConstants.PROXIMITY_ALERT_EXPIRATION, proximityAlert.getExpiration());
    }

    @Test
    public void whenProximityAlertIsAddedThenThePendingIntentIsSetToBroadcast() {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        groceryStoreManager.addProximityAlerts(places);

        com.groceryreminder.shadows.ShadowLocationManager.ProximityAlert proximityAlert = shadowLocationManager.getProximityAlert(place.getLatitude(), place.getLongitude());
        ShadowPendingIntent shadowPendingIntent = Shadows.shadowOf(proximityAlert.getPendingIntent());

        assertTrue(shadowPendingIntent.isBroadcastIntent());
    }

    @Test
    public void whenProximityAlertIsAddedThenTheStorePendingIntentIsSet() {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        groceryStoreManager.addProximityAlerts(places);

        com.groceryreminder.shadows.ShadowLocationManager.ProximityAlert proximityAlert = shadowLocationManager.getProximityAlert(place.getLatitude(), place.getLongitude());
        ShadowPendingIntent shadowPendingIntent = Shadows.shadowOf(proximityAlert.getPendingIntent());
        ShadowIntent shadowIntent = Shadows.shadowOf(shadowPendingIntent.getSavedIntent());

        assertEquals(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT, shadowIntent.getAction());
    }

    @Test
    public void whenProximityAlertIsAddedThenTheStorePendingIntentCancelsTheCurrentRequest() {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        groceryStoreManager.addProximityAlerts(places);

        com.groceryreminder.shadows.ShadowLocationManager.ProximityAlert proximityAlert = shadowLocationManager.getProximityAlert(place.getLatitude(), place.getLongitude());
        ShadowPendingIntent shadowPendingIntent = Shadows.shadowOf(proximityAlert.getPendingIntent());

        assertEquals(PendingIntent.FLAG_CANCEL_CURRENT, shadowPendingIntent.getFlags());
    }

    @Test
    public void givenPlacesWhenProximityAlertIsAddedThenTheStorePendingIntentRequestCodeIsUnique() {
        Place place = createDefaultGooglePlace();
        Place place2 = new Place();
        place2.setLatitude(place.getLatitude()+1);
        place2.setLongitude(place.getLongitude());
        place2.setName("test 2");
        place2.setPlaceId("test_id2");
        List<Place> places = new ArrayList<Place>();
        places.add(place);
        places.add(place2);

        groceryStoreManager.addProximityAlerts(places);

        List<com.groceryreminder.shadows.ShadowLocationManager.ProximityAlert> proximityAlerts = shadowLocationManager.getProximityAlerts();
        assertEquals(places.size(), proximityAlerts.size());

        ShadowPendingIntent shadowPendingIntent1 = Shadows.shadowOf(proximityAlerts.get(0).getPendingIntent());
        ShadowPendingIntent shadowPendingIntent2 = Shadows.shadowOf(proximityAlerts.get(1).getPendingIntent());
        assertNotEquals(shadowPendingIntent1.getRequestCode(), shadowPendingIntent2.getRequestCode());
    }
}
