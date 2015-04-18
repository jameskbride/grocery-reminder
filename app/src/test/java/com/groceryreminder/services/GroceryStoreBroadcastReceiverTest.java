package com.groceryreminder.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.domain.GroceryReminderConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        assertEquals(1, shadowNotificationManager.size());
    }

    private ShadowNotificationManager getShadowNotificationManager() {
        return Robolectric.shadowOf((NotificationManager)
                Robolectric.application.getSystemService(Context.NOTIFICATION_SERVICE));
    }

    @Test
    public void givenAnIntentWithoutTheProximityEnteringKeyWhenTheIntentIsReceivedThenNoNotificationIsSent() {
        Intent intent = BuildIntentToListenFor();

        broadcastReceiver.onReceive(Robolectric.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        assertEquals(0, shadowNotificationManager.size());
    }

    @Test
    public void whenANotificationIsSentThenTheNotificationIdShouldBeSet() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(Robolectric.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNotNull(notification);
    }

    @Test
    public void whenANotificationIsSentThenTheSmallIconIsSet() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(Robolectric.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertEquals(R.drawable.ic_stat_maps_local_grocery_store, notification.icon);
    }

    @Test
    public void whenANotificationIsSentThenTheContentTitleIsSet() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(Robolectric.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Robolectric.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));
        assertEquals(Robolectric.application.getString(R.string.app_name), notification.getContentTitle());
    }

    @Test
    public void whenANotificationIsSentThenTheContentTextIsSet() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(Robolectric.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Robolectric.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));
        assertEquals(Robolectric.application.getString(R.string.reminder_notification), notification.getContentText());
    }
}
