package com.groceryreminder.services;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreManagerInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowLocation;
import org.robolectric.shadows.ShadowPendingIntent;

import com.groceryreminder.shadows.ShadowLocationManager;

import java.util.ArrayList;
import java.util.List;

import se.walkercrou.places.Place;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowLocationManager.class}, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class GroceryLocatorServiceTest extends RobolectricTestBase {

    private static final double DEFAULT_LATITUDE = 39.9732997;
    private static final double DEFAULT_LONGITUDE = -82.99788610000002;
    private GroceryLocatorService groceryLocatorService;
    private LocationManager locationManager;
    private ShadowLocationManager shadowLocationManager;

    private Location defaultGPSLocation;

    private GroceryStoreManagerInterface groceryStoreManagerMock;

    @Before
    public void setUp() {
        super.setUp();
        groceryStoreManagerMock = getTestReminderModule().getGroceryStoreManager();
        groceryLocatorService = new GroceryLocatorService();
        groceryLocatorService.onCreate();
        setupLocationManager();
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
        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.FIVE_MILES_IN_METERS});
        shadowLocationManager.setLastKnownLocation(LocationManager.GPS_PROVIDER, defaultGPSLocation);
    }

    private Place createDefaultGooglePlace() {
        Place place = new Place();
        place.setName("test");
        place.setLatitude(DEFAULT_LATITUDE);
        place.setLongitude(DEFAULT_LONGITUDE);
        place.setPlaceId("test_id");

        return place;
    }

    private Location createDefaultLocation(String provider) {
        Location location = new Location(provider);
        location.setLatitude(1);
        location.setLongitude(2);
        return location;
    }

    private void updatePlaces(Place place) {
        List<Place> places = new ArrayList<Place>();
        places.add(place);
        when(groceryStoreManagerMock.findStoresByLocation(defaultGPSLocation)).thenReturn(places);
        when(groceryStoreManagerMock.filterPlacesByDistance(defaultGPSLocation, places,
                GroceryReminderConstants.FIVE_MILES_IN_METERS)).thenReturn(places);
        groceryLocatorService.onHandleIntent(new Intent());
    }

    @Test
    public void givenAnIntentWhenTheIntentIsHandledThenANearbySearchForGroceryStoresIsPerformed() {
        groceryLocatorService.onHandleIntent(new Intent());
        verify(groceryStoreManagerMock).findStoresByLocation(defaultGPSLocation);
    }

    @Test
    public void whenTheIntentIsHandledThenDistanceStoresAreDeleted() {
        groceryLocatorService.onHandleIntent(new Intent());
        verify(groceryStoreManagerMock).deleteStoresByLocation(defaultGPSLocation);
    }

    @Test
    public void givenPlaceSearchResultsAreFoundWhenTheyAreWithinFiveMilesDistanceThenTheyArePersisted() {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);
        when(groceryStoreManagerMock.findStoresByLocation(defaultGPSLocation)).thenReturn(places);
        when(groceryStoreManagerMock.filterPlacesByDistance(defaultGPSLocation, places,
                GroceryReminderConstants.FIVE_MILES_IN_METERS)).thenReturn(places);
        groceryLocatorService.onHandleIntent(new Intent());

        verify(groceryStoreManagerMock).persistGroceryStores(places);
    }

    @Test
    public void whenPlacesWithinFiveMilesArePersistedThenProximityAlertsAreAdded() {
        Place place = createDefaultGooglePlace();
        updatePlaces(place);

        assertTrue(shadowLocationManager.hasProximityAlert(place.getLatitude(), place.getLongitude()));
    }

    @Test
    public void whenProximityAlertIsAddedThenTheRadiusIsSetToFiftyFeetInMeters() {
        Place place = createDefaultGooglePlace();
        updatePlaces(place);

        ShadowLocationManager.ProximityAlert proximityAlert = shadowLocationManager.getProximityAlert(place.getLatitude(), place.getLongitude());

        assertEquals(GroceryReminderConstants.FIFTEEN_FEET_IN_METERS, proximityAlert.getRadius(), 0.001);
    }

    @Test
    public void whenProximityAlertIsAddedThenTheExpirationDoesNotExpire() {
        Place place = createDefaultGooglePlace();
        updatePlaces(place);

        ShadowLocationManager.ProximityAlert proximityAlert = shadowLocationManager.getProximityAlert(place.getLatitude(), place.getLongitude());

        assertEquals(GroceryReminderConstants.PROXIMITY_ALERT_EXPIRATION, proximityAlert.getExpiration());
    }

    @Test
    public void whenProximityAlertIsAddedThenThePendingIntentIsSetToBroadcast() {
        Place place = createDefaultGooglePlace();
        updatePlaces(place);

        ShadowLocationManager.ProximityAlert proximityAlert = shadowLocationManager.getProximityAlert(place.getLatitude(), place.getLongitude());
        ShadowPendingIntent shadowPendingIntent = Shadows.shadowOf(proximityAlert.getPendingIntent());

        assertTrue(shadowPendingIntent.isBroadcastIntent());
    }

    @Test
    public void whenProximityAlertIsAddedThenTheStorePendingIntentIsSet() {
        Place place = createDefaultGooglePlace();
        updatePlaces(place);

        ShadowLocationManager.ProximityAlert proximityAlert = shadowLocationManager.getProximityAlert(place.getLatitude(), place.getLongitude());
        ShadowPendingIntent shadowPendingIntent = Shadows.shadowOf(proximityAlert.getPendingIntent());
        ShadowIntent shadowIntent = Shadows.shadowOf(shadowPendingIntent.getSavedIntent());

        assertEquals(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT, shadowIntent.getAction());
    }

    @Test
    public void whenTheLastKnownLocationIsRequestedThenTheBestProviderIsDeterminedByCriteria() {
        groceryLocatorService.onHandleIntent(new Intent());
        Criteria actualCriteria = shadowLocationManager.getLastBestProviderCriteria();

        assertTrue(actualCriteria.isCostAllowed());
        assertTrue(actualCriteria.isSpeedRequired());
        assertFalse(actualCriteria.isAltitudeRequired());
        assertFalse(actualCriteria.isBearingRequired());
        assertEquals(Criteria.ACCURACY_FINE, actualCriteria.getAccuracy());
        assertEquals(Criteria.ACCURACY_HIGH, actualCriteria.getHorizontalAccuracy());
        assertEquals(Criteria.ACCURACY_LOW, actualCriteria.getSpeedAccuracy());
        assertEquals(Criteria.NO_REQUIREMENT, actualCriteria.getPowerRequirement());
        assertEquals(Criteria.NO_REQUIREMENT, actualCriteria.getVerticalAccuracy());
    }

    @Test
    public void whenNoProviderIsAvailableThenNoStoresAreUpdated() {
        try {
            shadowLocationManager.setLastKnownLocation(LocationManager.GPS_PROVIDER, null);
            shadowLocationManager.setBestProvider(LocationManager.GPS_PROVIDER, false, new ArrayList<Criteria>());
        } catch (Exception e) {
            fail("Unexpected exception");
        }

        groceryLocatorService.onHandleIntent(new Intent());

        verifyNoMoreInteractions(groceryStoreManagerMock);
    }
}