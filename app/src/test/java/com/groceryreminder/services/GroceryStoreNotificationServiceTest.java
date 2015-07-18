package com.groceryreminder.services;

import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;
import com.groceryreminder.testUtils.LocationValuesBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoreNotificationServiceTest extends RobolectricTestBase {

    private GroceryStoreNotificationService groceryStoreNotificationService;
    private static final String ARBITRARY_STORE_NAME = "store name";

    @Before
    public void setUp() {
        super.setUp();
        groceryStoreNotificationService = new GroceryStoreNotificationService();
        groceryStoreNotificationService.onCreate();
        ContentValues storeValues = new LocationValuesBuilder().createDefaultLocationValues().withName(ARBITRARY_STORE_NAME).build();
        groceryStoreNotificationService.getContentResolver().insert(ReminderContract.Locations.CONTENT_URI, storeValues);
    }

    private Intent buildIntentToListenFor() {
        Intent intentToListenFor =  new Intent();
        intentToListenFor.putExtra(ReminderContract.Locations.LATITUDE, "1.0");
        intentToListenFor.putExtra(ReminderContract.Locations.LONGITUDE, "2.0");
        intentToListenFor.putExtra(GroceryStoreLocationListener.PROVIDER, LocationManager.GPS_PROVIDER);

        return intentToListenFor;
    }

    @Test
    public void givenAValidRequestThenAPotentialNotificationIsSent() {
        Intent intent = buildIntentToListenFor();

        GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerMock = getTestReminderModule().getGroceryStoreNotificationManager();

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(1.0);
        location.setLongitude(2.0);
        groceryStoreNotificationService.onHandleIntent(intent);

        verify(groceryStoreNotificationManagerMock).sendPotentialNotification(any(Location.class), anyLong());
    }
}
