package com.groceryreminder.services;

import android.content.ContentValues;
import android.content.Intent;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
        broadcastReceiver.onReceive(RuntimeEnvironment.application, buildIntentToListenFor());

        Intent serviceIntent = Shadows.shadowOf(RuntimeEnvironment.application).peekNextStartedService();
        assertEquals(GroceryStoreNotificationService.class.getCanonicalName(), serviceIntent.getComponent().getClassName());
    }

    @Test
    public void whenAnIntentIsReceivedThenTheKeyProximityEnteringIsAddedToTheIntent() {
        broadcastReceiver.onReceive(RuntimeEnvironment.application, buildIntentToListenFor());

        Intent serviceIntent = Shadows.shadowOf(RuntimeEnvironment.application).peekNextStartedService();
        assertTrue(serviceIntent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false));
    }
}
