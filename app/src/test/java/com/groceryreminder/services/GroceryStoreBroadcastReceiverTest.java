package com.groceryreminder.services;

import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreLocationManagerInterface;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;
import com.groceryreminder.injection.ReminderApplication;
import com.groceryreminder.injection.ReminderObjectGraph;
import com.groceryreminder.injection.TestReminderApplication;
import com.groceryreminder.testUtils.ReminderValuesBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowBroadcastReceiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoreBroadcastReceiverTest extends RobolectricTestBase {

    private static final String ARBITRARY_STORE_NAME = "store name";
    private GroceryStoreBroadcastReceiver broadcastReceiver;

    @Before
    public void setUp() {
        super.setUp();
        ReminderObjectGraph.getInstance().createObjectGraph(((TestReminderApplication) RuntimeEnvironment.application).getModules());
        broadcastReceiver = new GroceryStoreBroadcastReceiver();
    }

    private Intent buildIntentToListenFor() {
        Intent intentToListenFor =  new Intent(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT);
        intentToListenFor.putExtra(ReminderContract.Locations.NAME, ARBITRARY_STORE_NAME);
        intentToListenFor.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        return intentToListenFor;
    }

    @Test
    public void whenAnIntentIsReceivedThenTheGroceryStoreNotificationServiceIsStarted() {
        GroceryStoreLocationManagerInterface groceryStoreLocationManagerMock = getTestReminderModule().getGroceryStoreLocationManager();

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(1.0);
        location.setLongitude(2.0);
        when(groceryStoreLocationManagerMock.getLastKnownLocation()).thenReturn(location);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, buildIntentToListenFor());

        Intent serviceIntent = Shadows.shadowOf(RuntimeEnvironment.application).peekNextStartedService();
        assertEquals(GroceryStoreNotificationService.class.getCanonicalName(), serviceIntent.getComponent().getClassName());
    }

    @Test
    public void givenNoLocationIsAvailableWhenAnIntentIsReceivedThenTheGroceryStoreNotificationSerivceIsNotStarted() {
        GroceryStoreLocationManagerInterface groceryStoreLocationManagerMock = getTestReminderModule().getGroceryStoreLocationManager();

        when(groceryStoreLocationManagerMock.getLastKnownLocation()).thenReturn(null);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, buildIntentToListenFor());

        Intent serviceIntent = Shadows.shadowOf(RuntimeEnvironment.application).peekNextStartedService();
        assertNull(serviceIntent);
    }

    @Test
    public void whenAnIntentIsReceivedThenTheLastKnownLocationIsPassedToTheGroceryStoreNotificationService() {
        GroceryStoreLocationManagerInterface groceryStoreLocationManagerMock = getTestReminderModule().getGroceryStoreLocationManager();

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(1.0);
        location.setLongitude(2.0);
        when(groceryStoreLocationManagerMock.getLastKnownLocation()).thenReturn(location);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, buildIntentToListenFor());

        verify(groceryStoreLocationManagerMock).getLastKnownLocation();
        Intent serviceIntent = Shadows.shadowOf(RuntimeEnvironment.application).peekNextStartedService();

        assertEquals(location.getProvider(), serviceIntent.getStringExtra(GroceryStoreLocationListener.PROVIDER));
        assertEquals(location.getLatitude(), serviceIntent.getDoubleExtra(ReminderContract.Locations.LATITUDE, 0), 0.0000001);
        assertEquals(location.getLongitude(), serviceIntent.getDoubleExtra(ReminderContract.Locations.LONGITUDE, 0), 0.0000001);
    }
}
