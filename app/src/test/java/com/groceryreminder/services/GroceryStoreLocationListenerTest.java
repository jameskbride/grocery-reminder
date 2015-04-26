package com.groceryreminder.services;

import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoreLocationListenerTest extends RobolectricTestBase {

    private GroceryStoreLocationListener groceryStoreLocationListener;
    private LocationUpdater locationUpdaterMock;

    @Before
    public void setUp() {
        super.setUp();
        this.locationUpdaterMock = mock(LocationUpdater.class);
        groceryStoreLocationListener = new GroceryStoreLocationListener(locationUpdaterMock);
    }

    @Test
    public void whenALocationIsUpdatedThenTheLocationUpdaterHandlesTheUpdate() {
        Location location = new Location(LocationManager.GPS_PROVIDER);

        groceryStoreLocationListener.onLocationChanged(location);

        verify(locationUpdaterMock).handleLocationUpdated(location);
    }

}
