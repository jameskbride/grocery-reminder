package com.groceryreminder.services;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.RobolectricTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import java.util.ArrayList;
import java.util.Arrays;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.TypeParam;
import se.walkercrou.places.Types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryLocatorServiceTest extends RobolectricTestBase {

    private static final String ARBITRARY_SERVICE_NAME = "test";
    private GroceryLocatorService groceryLocatorService;
    private GooglePlacesInterface googlePlacesMock;
    private LocationManager locationManager;
    private ShadowLocationManager shadowLocationManager;
    private Location defaultLastKnownLocation;

    @Before
    public void setUp() {
        groceryLocatorService = new GroceryLocatorService(ARBITRARY_SERVICE_NAME);
        groceryLocatorService.onCreate();

        this.googlePlacesMock = getTestReminderModule().getGooglePlaces();
        this.locationManager = getTestAndroidModule().getLocationManager();
        this.shadowLocationManager = Robolectric.shadowOf(locationManager);
        this.defaultLastKnownLocation = createDefaultLocation();
        shadowLocationManager.setLastKnownLocation("provider", defaultLastKnownLocation);
    }

    @Test
    public void givenAnIntentWhenTheIntentIsHandledThenARadarSearchIsPerformed() {
        groceryLocatorService.onHandleIntent(new Intent());

        verify(googlePlacesMock).getPlacesByRadar(anyDouble(), anyDouble(), anyDouble(), anyInt(), any(Param[].class));
    }

    @Test
    public void givenAnIntentWhenTheIntentIsHandledThenARadarSearchIsLimitedToFiveMiles() {
        groceryLocatorService.onHandleIntent(new Intent());

        verify(googlePlacesMock).getPlacesByRadar(anyDouble(), anyDouble(), eq(GroceryLocatorService.FIVE_MILES_IN_METERS), anyInt(), any(Param[].class));
    }

    @Test
    public void givenAnIntentWhenTheIntentIsHandledThenARadarSearchForGroceryStoresIsPerformed() {
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        ArgumentCaptor<Param> paramsCaptor = ArgumentCaptor.forClass(Param.class);

        groceryLocatorService.onHandleIntent(new Intent());

        verify(googlePlacesMock).getPlacesByRadar(anyDouble(), anyDouble(), anyDouble(), anyInt(), paramsCaptor.capture());

        Param actualParams = paramsCaptor.getValue();
        assertEquals(actualParams, groceryStoreType);
    }

    @Test
    public void givenAnIntentWhenTheIntentIsHandledThenTheCurrentLocationShouldBePassedToTheGooglePlacesSearch() {
        groceryLocatorService.onHandleIntent(new Intent());

        verify(googlePlacesMock).getPlacesByRadar(eq(defaultLastKnownLocation.getLatitude()), eq(defaultLastKnownLocation.getLongitude()), anyDouble(), anyInt(), any(Param[].class));
    }

    private Location createDefaultLocation() {
        Location location = new Location("provider");
        location.setLatitude(1);
        location.setLongitude(2);
        return location;
    }
}
