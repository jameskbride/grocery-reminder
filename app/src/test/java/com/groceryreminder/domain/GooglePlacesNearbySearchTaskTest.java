package com.groceryreminder.domain;

import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.injection.TestAndroidModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.exception.GooglePlacesException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GooglePlacesNearbySearchTaskTest extends RobolectricTestBase {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void whenGooglePlacesCausesAnErrorThenReturnAnEmptyListOfPlaces() {
        GooglePlacesInterface googlePlacesMock = mock(GooglePlacesInterface.class);
        GroceryStoreManagerInterface groceryStoreManagerMock = mock(GroceryStoreManagerInterface.class);

        GooglePlacesNearbySearchTask task = new GooglePlacesNearbySearchTask(googlePlacesMock, groceryStoreManagerMock);

        when(googlePlacesMock.getNearbyPlacesRankedByDistance(anyDouble(), anyDouble(), anyInt(), (Param[])anyVararg())).thenThrow(GooglePlacesException.class);

        List<Place> places = task.doInBackground(new Location(LocationManager.GPS_PROVIDER));

        assertTrue(places.isEmpty());
    }
}
