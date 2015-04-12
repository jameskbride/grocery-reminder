package com.groceryreminder.services;

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
import org.robolectric.shadows.ShadowLocationManager;

import java.util.ArrayList;
import java.util.List;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.Types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryLocatorServiceTest extends RobolectricTestBase {

    private GroceryLocatorService groceryLocatorService;
    private GooglePlacesInterface googlePlacesMock;
    private LocationManager locationManager;
    private ShadowLocationManager shadowLocationManager;

    private Location defaultGPSLocation;
    private ReminderContentProvider reminderProvider;
    private ShadowContentResolver shadowContentResolver;

    @Before
    public void setUp() {
        super.setUp();
        groceryLocatorService = new GroceryLocatorService();
        groceryLocatorService.onCreate();
        this.googlePlacesMock = getTestReminderModule().getGooglePlaces();
        setupLocationManager();
        setupReminderContentProvider();
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
        shadowLocationManager.setLastKnownLocation(LocationManager.GPS_PROVIDER, defaultGPSLocation);
    }

    private void setupReminderContentProvider() {
        reminderProvider = new ReminderContentProvider();
        reminderProvider.onCreate();
        shadowContentResolver = Robolectric.shadowOf(groceryLocatorService.getContentResolver());
        shadowContentResolver.registerProvider(ReminderContract.AUTHORITY, reminderProvider);
    }

    private Place createDefaultGooglePlace() {
        Place place = new Place();
        place.setName("test");
        place.setLatitude(0.0);
        place.setLongitude(1.1);
        place.setPlaceId("test_id");

        return place;
    }

    private Location createDefaultLocation(String provider) {
        Location location = new Location(provider);
        location.setLatitude(1);
        location.setLongitude(2);
        return location;
    }

    @Test
    public void givenAnIntentWhenTheIntentIsHandledThenANearbySearchForGroceryStoresIsPerformed() {
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        ArgumentCaptor<Param> paramsCaptor = ArgumentCaptor.forClass(Param.class);

        groceryLocatorService.onHandleIntent(new Intent());

        verify(googlePlacesMock).getNearbyPlacesRankedByDistance(anyDouble(), anyDouble(), paramsCaptor.capture());

        Param actualParams = paramsCaptor.getValue();
        assertEquals(actualParams, groceryStoreType);
    }

    @Test
    public void givenAnIntentWhenTheIntentIsHandledThenTheCurrentLocationShouldBePassedToTheGooglePlacesSearch() {
        groceryLocatorService.onHandleIntent(new Intent());

        verify(googlePlacesMock).getNearbyPlacesRankedByDistance(eq(defaultGPSLocation.getLatitude()), eq(defaultGPSLocation.getLongitude()), any(Param[].class));
    }

    @Test
    public void whenPlaceSearchResultsAreFoundThenTheyArePersisted() {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        doReturn(places).when(googlePlacesMock).getNearbyPlacesRankedByDistance(anyDouble(), anyDouble(), any(Param[].class));

        groceryLocatorService.onHandleIntent(new Intent());

        Cursor cursor = reminderProvider.query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, "", null, null);
        assertEquals(1, cursor.getCount());
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
}
