package com.groceryreminder.services;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoreLocationListenerTest extends RobolectricTestBase {

    private GroceryStoreLocationListener groceryStoreLocationListener;
    private LocationUpdater locationUpdaterMock;

    @Before
    public void setUp() {
        super.setUp();
        this.locationUpdaterMock = mock(LocationUpdater.class);
        groceryStoreLocationListener = new GroceryStoreLocationListener(RuntimeEnvironment.application, locationUpdaterMock);
    }

    @Test
    public void givenALocationIsBetterWhenALocationIsUpdatedThenTheLocationUpdaterHandlesTheUpdate() {
        Location location = new Location(LocationManager.GPS_PROVIDER);

        when(locationUpdaterMock.isBetterThanCurrentLocation(location)).thenReturn(true);

        groceryStoreLocationListener.onLocationChanged(location);

        verify(locationUpdaterMock).isBetterThanCurrentLocation(location);
        verify(locationUpdaterMock).handleLocationUpdated(location);
    }

    @Test
    public void givenALocationIsNotBetterWhenTheLocationIsChangedThenTheLocationIsNotHandled() {
        Location location = new Location(LocationManager.GPS_PROVIDER);

        when(locationUpdaterMock.isBetterThanCurrentLocation(location)).thenReturn(false);

        groceryStoreLocationListener.onLocationChanged(location);

        verify(locationUpdaterMock, times(0)).handleLocationUpdated(location);
    }

    @Test
    public void givenALocationIsAccurateWhenTheLocationIsChangedThenTheGroceryStoreNotificationServiceIsStarted() {
        Location location = new Location(LocationManager.GPS_PROVIDER);

        when(locationUpdaterMock.isAccurate(location)).thenReturn(true);
        when(locationUpdaterMock.isBetterThanCurrentLocation(location)).thenReturn(false);

        groceryStoreLocationListener.onLocationChanged(location);

        ShadowApplication context = (ShadowApplication)Shadows.shadowOf(RuntimeEnvironment.application);
        Intent serviceIntent = context.peekNextStartedService();

        assertEquals(GroceryStoreNotificationService.class.getName(), serviceIntent.getComponent().getClassName());
    }

    @Test
    public void givenALocationIsAccurateWhenTheLocationIsChangedThenTheCoordinatesOfTheLocationArePassedToTheNotificationService() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(1.0);
        location.setLongitude(2.0);

        when(locationUpdaterMock.isAccurate(location)).thenReturn(true);
        when(locationUpdaterMock.isBetterThanCurrentLocation(location)).thenReturn(false);

        groceryStoreLocationListener.onLocationChanged(location);

        ShadowApplication context = (ShadowApplication)Shadows.shadowOf(RuntimeEnvironment.application);
        Intent serviceIntent = context.peekNextStartedService();

        assertEquals(location.getLatitude(), serviceIntent.getDoubleExtra(ReminderContract.Locations.LATITUDE, 0), 0.01);
        assertEquals(location.getLongitude(), serviceIntent.getDoubleExtra(ReminderContract.Locations.LONGITUDE, 0), 0.01);
    }

    @Test
    public void givenALocationIsAccurateWhenTheLocationIsChangedThenTheProviderOfTheLocationIsPassedToTheNotificationService() {
        Location location = new Location(LocationManager.GPS_PROVIDER);

        when(locationUpdaterMock.isAccurate(location)).thenReturn(true);
        when(locationUpdaterMock.isBetterThanCurrentLocation(location)).thenReturn(false);

        groceryStoreLocationListener.onLocationChanged(location);

        ShadowApplication context = (ShadowApplication)Shadows.shadowOf(RuntimeEnvironment.application);
        Intent serviceIntent = context.peekNextStartedService();

        assertEquals(LocationManager.GPS_PROVIDER, serviceIntent.getStringExtra(GroceryStoreLocationListener.PROVIDER));
    }

}
