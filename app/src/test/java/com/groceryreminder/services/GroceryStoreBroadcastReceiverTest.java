package com.groceryreminder.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.domain.GroceryReminderConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowNotificationManager;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryStoreBroadcastReceiverTest extends RobolectricTestBase {

    private GroceryStoreBroadcastReceiver broadcastReceiver;

    @Before
    public void setUp() {
        super.setUp();
        broadcastReceiver = new GroceryStoreBroadcastReceiver();
    }

    private Intent BuildIntentToListenFor() {
        return new Intent(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT);
    }

    @Test
    public void whenTheProximityEventIntentIsSentThenTheBroadcastReceiverListensForIt() {
        Intent intent = BuildIntentToListenFor();

        ShadowApplication shadowApplication = Robolectric.getShadowApplication();
        assertTrue(shadowApplication.hasReceiverForIntent(intent));
    }

    @Test
    public void givenAnIntentWithTheProximityEnteringKeyWhenTheIntentIsReceivedThenANotificationIsSent() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(Robolectric.application, intent);

        ShadowNotificationManager shadowNotificationManager = Robolectric.shadowOf((NotificationManager)
                Robolectric.application.getSystemService(Context.NOTIFICATION_SERVICE));

        assertEquals(1, shadowNotificationManager.size());
    }

    @Test
    public void givenAnIntentWithoutTheProximityEnteringKeyWhenTheIntentIsReceivedThenNoNotificationIsSent() {
        Intent intent = BuildIntentToListenFor();

        broadcastReceiver.onReceive(Robolectric.application, intent);

        ShadowNotificationManager shadowNotificationManager = Robolectric.shadowOf((NotificationManager)
                Robolectric.application.getSystemService(Context.NOTIFICATION_SERVICE));

        assertEquals(0, shadowNotificationManager.size());
    }
}
