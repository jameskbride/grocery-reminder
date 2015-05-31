package com.groceryreminder.domain;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.shadows.ShadowLocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocation;

import java.util.ArrayList;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowLocationManager.class})
public class GroceryStoreLocationManagerTest extends RobolectricTestBase {

    private GroceryStoreLocationManager groceryStoreLocationManager;
    private LocationManager locationManager;
    private ShadowLocationManager shadowLocationManager;
    private Location defaultLocation;

    @Before
    public void setUp() {
        super.setUp();

        setupLocationManager();
        groceryStoreLocationManager = new GroceryStoreLocationManager(locationManager);
    }

    private void setupLocationManager() {
        this.locationManager = getTestAndroidModule().getLocationManager();
        this.shadowLocationManager = (com.groceryreminder.shadows.ShadowLocationManager) Shadows.shadowOf(locationManager);
        try {
            assertTrue(shadowLocationManager.setBestProvider(LocationManager.GPS_PROVIDER, true, new ArrayList<Criteria>()));
        } catch (Exception e) {
            fail("Unable to set the best provider.");
        }

        this.defaultLocation = createDefaultLocation(LocationManager.GPS_PROVIDER);
        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS});
        shadowLocationManager.setLastKnownLocation(LocationManager.GPS_PROVIDER, defaultLocation);
    }

    private Location createDefaultLocation(String provider) {
        Location location = new Location(provider);
        location.setLatitude(1);
        location.setLongitude(2);
        return location;
    }

    @Test
    public void whenNoProvidersAreAvailableTheNoLocationIsFound() {
        try {
            shadowLocationManager.setLastKnownLocation(LocationManager.GPS_PROVIDER, null);
            shadowLocationManager.setBestProvider(LocationManager.GPS_PROVIDER, false, new ArrayList<Criteria>());
        } catch (Exception e) {
            fail("Unexpected exception");
        }

        assertNull(groceryStoreLocationManager.getLastKnownLocation());
    }

    @Test
    public void whenTheLastKnownLocationIsRequestedThenTheBestProviderIsDeterminedByCriteria() {
        groceryStoreLocationManager.getLastKnownLocation();

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
