package com.groceryreminder.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreLocationManagerInterface;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.shadows.ShadowLocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowLocationManager.class})
public class GroceryLocatorServiceTest extends RobolectricTestBase {

    private GroceryLocatorService groceryLocatorService;
    private Location defaultLocation;
    private GroceryStoreManagerInterface groceryStoreManagerMock;
    private GroceryStoreLocationManagerInterface groceryStoreLocationManagerMock;

    @Before
    public void setUp() {
        super.setUp();
        groceryStoreManagerMock = getTestReminderModule().getGroceryStoreManager();
        groceryStoreLocationManagerMock = getTestReminderModule().getGroceryStoreLocationManager();
        this.defaultLocation = createDefaultLocation(LocationManager.GPS_PROVIDER);
        groceryLocatorService = new GroceryLocatorService();
        groceryLocatorService.onCreate();
    }


    private Location createDefaultLocation(String provider) {
        Location location = new Location(provider);
        location.setLatitude(1);
        location.setLongitude(2);
        return location;
    }

    @Test
    public void givenALastKnownLocationWhenTheIntentIsHandledThenALocationUpdateIsHandled() {
        when(groceryStoreLocationManagerMock.getLastKnownLocation()).thenReturn(defaultLocation);
        when(groceryStoreManagerMock.isBetterThanCurrentLocation(defaultLocation)).thenReturn(true);

        groceryLocatorService.onStartCommand(new Intent(), 0, 0);

        verify(groceryStoreLocationManagerMock).getLastKnownLocation();
        verify(groceryStoreManagerMock).isBetterThanCurrentLocation(defaultLocation);
        verify(groceryStoreManagerMock).handleLocationUpdated(defaultLocation);
    }

    @Test
    public void givenAnIntentWithTheListenForGPSExtraWhenTheIntentIsHandledThenTheGroceryStoreManagerListensForGPSUpdates() {
        Intent intent = new Intent();
        intent.putExtra(GroceryReminderConstants.LISTEN_FOR_GPS_EXTRA, true);

        when(groceryStoreLocationManagerMock.getLastKnownLocation()).thenReturn(defaultLocation);
        when(groceryStoreManagerMock.isBetterThanCurrentLocation(defaultLocation)).thenReturn(true);

        groceryLocatorService.onStartCommand(intent, 0, 0);

        verify(groceryStoreManagerMock).listenForLocationUpdates(true);
        verify(groceryStoreLocationManagerMock).getLastKnownLocation();
        verify(groceryStoreManagerMock).isBetterThanCurrentLocation(defaultLocation);
        verify(groceryStoreManagerMock).handleLocationUpdated(defaultLocation);
    }

    @Test
    public void givenALastKnownLocationWhichIsNotBetterWhenTheIntentIsHandledThenALocationUpdateIsNotHandled() {
        defaultLocation.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS + 1);

        when(groceryStoreLocationManagerMock.getLastKnownLocation()).thenReturn(defaultLocation);
        when(groceryStoreManagerMock.isBetterThanCurrentLocation(defaultLocation)).thenReturn(false);
        groceryLocatorService.onStartCommand(new Intent(), 0, 0);

        verify(groceryStoreLocationManagerMock).getLastKnownLocation();
        verify(groceryStoreManagerMock).isBetterThanCurrentLocation(defaultLocation);
        verify(groceryStoreManagerMock, times(0)).handleLocationUpdated(defaultLocation);
    }

    @Test
    public void whenNoProviderIsAvailableThenNoStoresAreUpdated() {
        when(groceryStoreLocationManagerMock.getLastKnownLocation()).thenReturn(null);

        groceryLocatorService.onStartCommand(new Intent(), 0, 0);

        verify(groceryStoreLocationManagerMock).getLastKnownLocation();
        verify(groceryStoreManagerMock).listenForLocationUpdates(false);
        verifyNoMoreInteractions(groceryStoreManagerMock);
    }

    @Test
    public void whenTheServiceIsRestartedAndTheIntentIsNullThenNoUpdatesAreHandled() {
        assertEquals(Service.START_STICKY, groceryLocatorService.onStartCommand(null, 0, 0));

        verifyNoMoreInteractions(groceryStoreManagerMock);
        verifyNoMoreInteractions(groceryStoreLocationManagerMock);
    }
}
