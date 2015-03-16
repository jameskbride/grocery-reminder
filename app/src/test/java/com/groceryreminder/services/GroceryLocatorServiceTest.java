package com.groceryreminder.services;

import android.content.Intent;
import android.database.Cursor;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryLocatorServiceTest extends RobolectricTestBase {

    private static final String ARBITRARY_SERVICE_NAME = "test";
    private GroceryLocatorService groceryLocatorService;
    private GooglePlacesInterface googlePlacesMock;
    private LocationManager locationManager;
    private ShadowLocationManager shadowLocationManager;
    private Location defaultLastKnownLocation;
    private ReminderContentProvider reminderProvider;
    private ShadowContentResolver shadowContentResolver;

    @Before
    public void setUp() {
        super.setUp();
        groceryLocatorService = new GroceryLocatorService(ARBITRARY_SERVICE_NAME);
        groceryLocatorService.onCreate();

        this.googlePlacesMock = getTestReminderModule().getGooglePlaces();
        this.locationManager = getTestAndroidModule().getLocationManager();
        this.shadowLocationManager = Robolectric.shadowOf(locationManager);
        this.defaultLastKnownLocation = createDefaultLocation();
        shadowLocationManager.setLastKnownLocation("provider", defaultLastKnownLocation);
        reminderProvider = new ReminderContentProvider();
        reminderProvider.onCreate();
        shadowContentResolver = Robolectric.shadowOf(groceryLocatorService.getContentResolver());
        shadowContentResolver.registerProvider(ReminderContract.AUTHORITY, reminderProvider);

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

    @Test
    public void whenPlaceSearchResultsAreFoundThenTheyArePersisted() {
        Place place = createDefaultGooglePlace();
        List<Place> places = new ArrayList<Place>();
        places.add(place);

        doReturn(places).when(googlePlacesMock).getPlacesByRadar(anyDouble(), anyDouble(), anyDouble(), anyInt(), any(Param[].class));

        groceryLocatorService.onHandleIntent(new Intent());
        verify(googlePlacesMock).getPlacesByRadar(anyDouble(), anyDouble(), anyDouble(), anyInt(), (Param[])anyVararg());

        Cursor cursor = reminderProvider.query(ReminderContract.Locations.CONTENT_URI, ReminderContract.Locations.PROJECT_ALL, "", null, null);
        assertEquals(1, cursor.getCount());
    }

    private Place createDefaultGooglePlace() {
        Place place = new Place();
        place.setName("test");
        place.setLatitude(0.0);
        place.setLongitude(1.1);
        place.setPlaceId("test_id");
        return place;
    }

    private Location createDefaultLocation() {
        Location location = new Location("provider");
        location.setLatitude(1);
        location.setLongitude(2);
        return location;
    }
}
