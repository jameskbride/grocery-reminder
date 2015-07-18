package com.groceryreminder.domain;

import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.GroceryStoreLocationContentProvider;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.services.LocationUpdater;
import com.groceryreminder.shadows.ShadowLocationManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowLocationManager.class})
public class GroceryStoreManagerTest extends RobolectricTestBase {

    public static final int NETWORK_PROVIDER_COUNT = 1;
    public static final int PASSIVE_PROVIDER_COUNT = 1;
    private GroceryStoreManager groceryStoreManager;
    private GroceryStoreLocationContentProvider reminderProvider;
    private ShadowContentResolver shadowContentResolver;
    private GooglePlacesInterface googlePlacesMock;
    private LocationManager locationManager;
    private ShadowLocationManager shadowLocationManager;

    private Location defaultLocation;

    private static final double DEFAULT_LATITUDE = 39.9732997;
    private static final double DEFAULT_LONGITUDE = -82.99788610000002;

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
        this.locationManager = spy(getTestAndroidModule().getLocationManager());
        this.shadowLocationManager = (com.groceryreminder.shadows.ShadowLocationManager)Shadows.shadowOf(locationManager);
        try {
            assertTrue(shadowLocationManager.setBestProvider(LocationManager.NETWORK_PROVIDER, true, new ArrayList<Criteria>()));
        } catch (Exception e) {
            fail("Unable to set the best provider.");
        }

        defaultLocation = createDefaultLocation(LocationManager.NETWORK_PROVIDER);
        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS});
        shadowLocationManager.setLastKnownLocation(LocationManager.NETWORK_PROVIDER, defaultLocation);
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);
        shadowLocationManager.setProviderEnabled(LocationManager.PASSIVE_PROVIDER, true);
    }

    private void setupReminderContentProvider() {
        reminderProvider = new GroceryStoreLocationContentProvider();
        reminderProvider.onCreate();
        shadowContentResolver = Shadows.shadowOf(getTestAndroidModule().getApplicationContext().getContentResolver());
        shadowContentResolver.registerProvider(ReminderContract.REMINDER_AUTHORITY, reminderProvider);
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
        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS + 1});
    }

    private void setLocationUpdatableTimestamp(Location location) {
        //Faking out the time per the minTime param of LocationManager.requestLocationUpdates() method
        location.setTime(System.currentTimeMillis() + GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME_MILLIS + 1);
    }

    private void performMultipleLocationUpdates(Location location, Location updatedLocation, GroceryStoreManager groceryStoreManagerSpy) {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);


        groceryStoreManagerSpy.listenForLocationUpdates(false);

        when(groceryStoreManagerSpy.filterPlacesByDistance(location, places, GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS)).thenReturn(places);
        when(googlePlacesMock.getNearbyPlacesRankedByDistance(anyDouble(), anyDouble(), anyInt(), (Param[])anyVararg())).thenReturn(places);

        groceryStoreManagerSpy.handleLocationUpdated(location);
        Robolectric.flushBackgroundScheduler();
        groceryStoreManagerSpy.handleLocationUpdated(updatedLocation);
        Robolectric.flushBackgroundScheduler();
    }

    @Test
    public void whenPlacesAreRequestedByLocationThenANearbySearchIsPerformed() {
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        ArgumentCaptor<Param> paramsCaptor = ArgumentCaptor.forClass(Param.class);

        groceryStoreManager.findStoresByLocation(defaultLocation);

        verify(googlePlacesMock).getNearbyPlacesRankedByDistance(anyDouble(), anyDouble(), eq(GroceryStoreManagerInterface.GOOGLE_PLACES_MAX_RESULTS), paramsCaptor.capture());

        Param actualParams = paramsCaptor.getValue();
        assertEquals(actualParams, groceryStoreType);
    }

    @Test
    public void whenPlacesAreRequestedUnderTheMinimumUpdateTimeThenANearbySearchIsNotPerformed() {
        SharedPreferences sharedPreferences = RuntimeEnvironment.application.getSharedPreferences(RuntimeEnvironment.application.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(GroceryReminderConstants.LAST_GOOGLE_PLACES_POLL_TIME, System.currentTimeMillis()).commit();

        groceryStoreManager.findStoresByLocation(defaultLocation);

        verifyNoMoreInteractions(googlePlacesMock);
    }

    @Test
    public void givenDistanceIsGreaterThanFiveMilesWhenPlacesAreFilteredByLocationThenPlacesOutsideOfFiveMilesAreNotReturned() {
        setCurrentDistanceGreaterThanFiveMiles();
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        List<Place> actualPlaces = groceryStoreManager.filterPlacesByDistance(defaultLocation, places, GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS);
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
        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS + 1});

        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        groceryStoreManager.persistGroceryStores(places);

        Cursor cursor = reminderProvider.query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, "", null, null);
        assertEquals(1, cursor.getCount());

        groceryStoreManager.deleteStoresByLocation(defaultLocation);

        cursor = reminderProvider.query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, "", null, null);
        assertEquals(0, cursor.getCount());
    }

    @Test
    public void whenLocationUpdatesAreRequestedThenLocationListenersAreAddedToTheLocationManager() {
        groceryStoreManager.listenForLocationUpdates(false);

        List<LocationListener> locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertFalse(locationListeners.isEmpty());
    }

    @Test
    public void whenLocationUpdatesAreRequestedThenAGPSListenerIsAddedToTheLocationManager() {
        groceryStoreManager.listenForLocationUpdates(false);

        List<LocationListener> locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertFalse(shadowLocationManager.getProvidersForListener(locationListeners.get(0)).contains(LocationManager.GPS_PROVIDER));
    }

    @Test
    public void whenLocationUpdatesAreRequestedWithGPSUpdatesThenAGPSListenerIsAddedToTheLocationManager() {
        groceryStoreManager.listenForLocationUpdates(true);

        List<LocationListener> locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertTrue(shadowLocationManager.getProvidersForListener(locationListeners.get(0)).contains(LocationManager.GPS_PROVIDER));
    }

    @Test
    public void whenLocationUpdatesAreRequestedThenTheMinTimeForLocationUpdatesIsFiveMinutes() {
        ArgumentCaptor<Long> minTimeCaptor = ArgumentCaptor.forClass(Long.class);

        groceryStoreManager.listenForLocationUpdates(false);

        verify(locationManager, times(NETWORK_PROVIDER_COUNT + PASSIVE_PROVIDER_COUNT)).requestLocationUpdates(anyString(), minTimeCaptor.capture(), anyFloat(), any(LocationListener.class));

        List<Long> capturedMinTimes = minTimeCaptor.getAllValues();
        assertEquals(GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME_MILLIS, capturedMinTimes.get(0).longValue());
    }

    @Test
    public void whenLocationUpdatesAreRequestedThenTheMinDistanceForLocationUpdatesIsFiveMilesInMeters() {
        ArgumentCaptor<Float> minDistanceCaptor = ArgumentCaptor.forClass(Float.class);

        groceryStoreManager.listenForLocationUpdates(false);

        verify(locationManager, times(NETWORK_PROVIDER_COUNT + PASSIVE_PROVIDER_COUNT)).requestLocationUpdates(anyString(), anyLong(), minDistanceCaptor.capture(), any(LocationListener.class));

        List<Float> capturedMinDistances = minDistanceCaptor.getAllValues();
        assertEquals(GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS, capturedMinDistances.get(0).floatValue(), 0.001);
    }

    @Test
    public void whenLocationUpdatesAreRequestedThenANetworkListenerIsAddedToTheLocationManager() {
        groceryStoreManager.listenForLocationUpdates(false);

        List<LocationListener> locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertTrue(shadowLocationManager.getProvidersForListener(locationListeners.get(0)).contains(LocationManager.NETWORK_PROVIDER));
    }

    @Test
    public void whenLocationUpdatesAreRequestedThenAPassiveListenerIsAddedToTheLocationManager() {
        groceryStoreManager.listenForLocationUpdates(false);

        List<LocationListener> locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertTrue(shadowLocationManager.getProvidersForListener(locationListeners.get(0)).contains(LocationManager.PASSIVE_PROVIDER));
    }

    @Test
    public void givenLocationUpdatesHaveAlreadyBeenRequestedWhenUpdatesAreRequestedAgainThenAdditionalListenersAreNotAdded() {
        groceryStoreManager.listenForLocationUpdates(false);
        groceryStoreManager.listenForLocationUpdates(false);

        List<LocationListener> locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertEquals(NETWORK_PROVIDER_COUNT + PASSIVE_PROVIDER_COUNT, locationListeners.size());
    }

    @Test
    public void whenLocationUpdatesAreRequestedThenTheMinTimeForNetworkUpdatesIsFiveMinutes() {
        ArgumentCaptor<Long> minTimeCaptor = ArgumentCaptor.forClass(Long.class);

        groceryStoreManager.listenForLocationUpdates(false);

        verify(locationManager, times(NETWORK_PROVIDER_COUNT + PASSIVE_PROVIDER_COUNT)).requestLocationUpdates(anyString(), minTimeCaptor.capture(), anyFloat(), any(LocationListener.class));

        List<Long> capturedMinTimes = minTimeCaptor.getAllValues();
        assertEquals(GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME_MILLIS, capturedMinTimes.get(0).longValue());
    }

    @Test
    public void whenLocationUpdatesAreRequestedThenTheMinTimeForPassiveUpdatesIsZero() {
        ArgumentCaptor<Long> minTimeCaptor = ArgumentCaptor.forClass(Long.class);

        groceryStoreManager.listenForLocationUpdates(false);

        verify(locationManager, times(NETWORK_PROVIDER_COUNT + PASSIVE_PROVIDER_COUNT)).requestLocationUpdates(anyString(), minTimeCaptor.capture(), anyFloat(), any(LocationListener.class));

        List<Long> capturedMinTimes = minTimeCaptor.getAllValues();
        assertEquals(0, capturedMinTimes.get(1).longValue());
    }

    @Test
    public void whenLocationUpdatesAreRequestedThenTheMinDistanceForNetworkUpdatesIsFiveMilesInMeters() {
        ArgumentCaptor<Float> minDistanceCaptor = ArgumentCaptor.forClass(Float.class);

        groceryStoreManager.listenForLocationUpdates(false);

        verify(locationManager, times(NETWORK_PROVIDER_COUNT + PASSIVE_PROVIDER_COUNT)).requestLocationUpdates(anyString(), anyLong(), minDistanceCaptor.capture(), any(LocationListener.class));

        List<Float> capturedMinDistances = minDistanceCaptor.getAllValues();
        assertEquals(GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS, capturedMinDistances.get(1).floatValue(), 0.001);
    }

    @Test
    public void givenTheGPSProviderIsNotEnabledWhenLocationUpdatesAreRequestedThenAGPSListenerIsNotAddedToTheLocationManager() {
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, false);

        groceryStoreManager.listenForLocationUpdates(false);

        List<LocationListener> locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertFalse(shadowLocationManager.getProvidersForListener(locationListeners.get(0)).contains(LocationManager.GPS_PROVIDER));
    }

    @Test
    public void givenTheNetworkProviderIsNotEnabledWhenLocationUpdatesAreRequestedThenANetworkListenerIsNotAddedToTheLocationManager() {
        shadowLocationManager.setProviderEnabled(LocationManager.NETWORK_PROVIDER, false);

        groceryStoreManager.listenForLocationUpdates(false);

        List<LocationListener> locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertFalse(shadowLocationManager.getProvidersForListener(locationListeners.get(0)).contains(LocationManager.NETWORK_PROVIDER));
    }

    @Test
    @Ignore
    /*
        It appears there is a bug in the ShadowLocationManager.  When updates are removed there is a
        null check against the map of of listeners.  Removing updates does not null the list of providers;
        therefore when listeners are added there are not added to the map.  See line 228 of ShadowLocationManager (Robolectric 3.0-rc2)
     */
    public void whenGPSUpdatesAreNoLongerRequiredThenTheGPSListenerIsRemovedFromTheLocationManager() {
        groceryStoreManager.listenForLocationUpdates(true);

        List<LocationListener> locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertTrue(shadowLocationManager.getProvidersForListener(locationListeners.get(0)).contains(LocationManager.GPS_PROVIDER));

        groceryStoreManager.removeGPSListener();

        locationListeners = shadowLocationManager.getRequestLocationUpdateListeners();
        assertFalse(shadowLocationManager.getProvidersForListener(locationListeners.get(0)).contains(LocationManager.GPS_PROVIDER));
    }

    @Test
    public void givenNoLocationIsCurrentlySetWhenTheLocationIsUpdatedThenStoreLocationsAreUpdated() {
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        setLocationUpdatableTimestamp(location);

        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        GroceryStoreManager groceryStoreManagerSpy = spy(groceryStoreManager);
        groceryStoreManagerSpy.listenForLocationUpdates(false);

        when(googlePlacesMock.getNearbyPlacesRankedByDistance(anyDouble(), anyDouble(), anyInt(), (Param[]) anyVararg())).thenReturn(places);
        when(groceryStoreManagerSpy.filterPlacesByDistance(location, places, GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS)).thenReturn(places);
        shadowLocationManager.simulateLocation(location);
        Robolectric.flushBackgroundScheduler();

        verify(groceryStoreManagerSpy).deleteStoresByLocation(location);
        verify(groceryStoreManagerSpy).persistGroceryStores(places);
    }

    @Test
    public void givenALocationIsSetWhenALocationIsHandledWithinFiveMinutesOfTheOriginalLocationThenTheLocationIsNotUpdated() {

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        setLocationUpdatableTimestamp(location);
        SystemClock.setCurrentTimeMillis(System.currentTimeMillis() + GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME_MILLIS + 1);
        Location updatedLocation = new Location(LocationManager.GPS_PROVIDER);
        updatedLocation.setLatitude(DEFAULT_LATITUDE);
        updatedLocation.setLongitude(DEFAULT_LONGITUDE);
        updatedLocation.setTime(location.getTime() + 1);

        GroceryStoreManager groceryStoreManagerSpy = spy(groceryStoreManager);
        performMultipleLocationUpdates(location, updatedLocation, groceryStoreManagerSpy);

        verify(groceryStoreManagerSpy, times(1)).deleteStoresByLocation(location);
        verify(groceryStoreManagerSpy, times(1)).persistGroceryStores((List<Place>) anyCollection());
    }

    @Test
    public void givenCurrentIsNotSetWhenALocationWithAnAccuracyWorseThanTheMaximumAccuracyTheNewLocationIsNotBetter() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS + 1);

        assertFalse(groceryStoreManager.isBetterThanCurrentLocation(location));
    }

    @Test
    public void givenCurrentLocationIsNotSetWhenALocationWithTheMaximumAccuracyThenTheNewLocationIsBetter() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);

        assertTrue(groceryStoreManager.isBetterThanCurrentLocation(location));
    }

    @Test
    public void givenCurrentLocationIsNotSetWhenALocationWithLessThanMaximumAccuracyThenTheNewLocationIsBetter() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS - 1);

        assertTrue(groceryStoreManager.isBetterThanCurrentLocation(location));
    }

    @Test
    public void givenCurrentLocationIsSetWhenTheCurrentLocationIsRequestedThenItIsReturned() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);

        groceryStoreManager.setLocation(location);

        assertEquals(location, groceryStoreManager.getCurrentLocation());
    }

    @Test
    public void givenCurrentLocationIsSetWhenTheLocationNotMoreAccurateThenItIsNotBetter() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);

        groceryStoreManager.setLocation(location);

        assertFalse(groceryStoreManager.isBetterThanCurrentLocation(location));
    }

    @Test
    public void givenCurrentLocationIsSetWhenTheLocationIsSignificantlyMoreAccurateThenItIsBetter() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS - 1);

        groceryStoreManager.setLocation(location);

        Location updatedLocation = new Location(LocationManager.GPS_PROVIDER);
        updatedLocation.setLatitude(DEFAULT_LATITUDE);
        updatedLocation.setLongitude(DEFAULT_LONGITUDE);
        updatedLocation.setAccuracy(location.getAccuracy() / 2);

        assertTrue(groceryStoreManager.isBetterThanCurrentLocation(updatedLocation));
    }

    @Test
    public void givenCurrentLocationIsSetWhenTheLocationIsSignificantlyNewerThenItIsBetter() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);

        groceryStoreManager.setLocation(location);

        Location updatedLocation = new Location(LocationManager.GPS_PROVIDER);
        updatedLocation.setLatitude(DEFAULT_LATITUDE);
        updatedLocation.setLongitude(DEFAULT_LONGITUDE);
        updatedLocation.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);
        updatedLocation.setTime(location.getTime() + LocationUpdater.SIGNIFICANT_LOCATION_TIME_DELTA);

        assertTrue(groceryStoreManager.isBetterThanCurrentLocation(updatedLocation));
    }

    @Test
    public void givenCurrentLocationIsSetWhenTheLocationIsMoreThanSignificantlyNewerThenItIsBetter() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);

        groceryStoreManager.setLocation(location);

        Location updatedLocation = new Location(LocationManager.GPS_PROVIDER);
        updatedLocation.setLatitude(DEFAULT_LATITUDE);
        updatedLocation.setLongitude(DEFAULT_LONGITUDE);
        updatedLocation.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);
        updatedLocation.setTime(location.getTime() + LocationUpdater.SIGNIFICANT_LOCATION_TIME_DELTA + 1);

        assertTrue(groceryStoreManager.isBetterThanCurrentLocation(updatedLocation));
    }

    @Test
    public void givenCurrentLocationIsSetWhenTheLocationIsNotSignificantlyNewerThenItIsNotBetter() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);

        groceryStoreManager.setLocation(location);

        Location updatedLocation = new Location(LocationManager.GPS_PROVIDER);
        updatedLocation.setLatitude(DEFAULT_LATITUDE);
        updatedLocation.setLongitude(DEFAULT_LONGITUDE);
        updatedLocation.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);
        updatedLocation.setTime(location.getTime());

        assertFalse(groceryStoreManager.isBetterThanCurrentLocation(updatedLocation));
    }

    @Test
    public void givenCurrentLocationIsSetWhenTheLocationIsNotSignificantlyNewerAndNotSignificantlyMoreAccurateThenItIsNotBetter() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(DEFAULT_LATITUDE);
        location.setLongitude(DEFAULT_LONGITUDE);
        location.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS);

        groceryStoreManager.setLocation(location);

        Location updatedLocation = new Location(LocationManager.GPS_PROVIDER);
        updatedLocation.setLatitude(DEFAULT_LATITUDE);
        updatedLocation.setLongitude(DEFAULT_LONGITUDE);
        updatedLocation.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS - 1);
        updatedLocation.setTime(location.getTime());

        assertFalse(groceryStoreManager.isBetterThanCurrentLocation(updatedLocation));
    }

}