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
import com.groceryreminder.shadows.ShadowLocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocation;

import java.util.ArrayList;

import se.walkercrou.places.Place;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

    @Test
    public void givenALastKnownLocationWhenTheIntentIsHandledThenALocationUpdateIsHandled() {
        groceryLocatorService.onHandleIntent(new Intent());

        verify(groceryStoreManagerMock).handleLocationUpdated(defaultGPSLocation);
    }

    @Test
    public void givenALastKnownLocationWhichIsTooInaccurateWhenTheIntentIsHandledThenALocationUpdateIsNotHandled() {
        defaultGPSLocation.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS + 1);
        groceryLocatorService.onHandleIntent(new Intent());

        verify(groceryStoreManagerMock, times(0)).handleLocationUpdated(defaultGPSLocation);
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
        verify(groceryStoreManagerMock).listenForLocationUpdates();
        verifyNoMoreInteractions(groceryStoreManagerMock);
    }
}