package com.groceryreminder.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.domain.GroceryReminderConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
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

    private ShadowNotificationManager getShadowNotificationManager() {
        return Shadows.shadowOf((NotificationManager)
                RuntimeEnvironment.application.getSystemService(Context.NOTIFICATION_SERVICE));
    }

    @Test
    public void whenTheProximityEventIntentIsSentThenTheBroadcastReceiverListensForIt() {
        Intent intent = BuildIntentToListenFor();

        ShadowApplication shadowApplication = Shadows.shadowOf(RuntimeEnvironment.application);
        assertTrue(shadowApplication.hasReceiverForIntent(intent));
    }

    @Test
    public void givenAnIntentWithTheProximityEnteringKeyWhenTheIntentIsReceivedThenANotificationIsSent() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        assertEquals(1, shadowNotificationManager.size());
    }

    @Test
    public void givenAnIntentWithoutTheProximityEnteringKeyWhenTheIntentIsReceivedThenNoNotificationIsSent() {
        Intent intent = BuildIntentToListenFor();

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        assertEquals(0, shadowNotificationManager.size());
    }

    @Test
    public void whenANotificationIsSentThenTheNotificationIdShouldBeSet() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNotNull(notification);
    }

    @Test
    public void whenANotificationIsSentThenTheSmallIconIsSet() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertEquals(R.drawable.ic_stat_maps_local_grocery_store, notification.icon);
    }

    @Test
    public void whenANotificationIsSentThenTheContentTitleIsSet() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));
        assertEquals(RuntimeEnvironment.application.getString(R.string.app_name), notification.getContentTitle());
    }

    @Test
    public void whenANotificationIsSentThenTheContentTextIsSet() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));
        assertEquals(RuntimeEnvironment.application.getString(R.string.reminder_notification), notification.getContentText());
    }

    @Test
    public void whenANotificationIsSentThenTheVibrationIsSet() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));

        assertArrayEquals(GroceryReminderConstants.PROXIMITY_VIBRATION_PATTERN, notification.getRealNotification().vibrate);
    }

    @Test
    public void whenANotificationIsSentThenTheDefaultNotificationSoundPlays() {

    }
}
