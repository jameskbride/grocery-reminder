package com.groceryreminder.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.provider.Settings;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.testUtils.ReminderValuesBuilder;
import com.groceryreminder.views.reminders.RemindersActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;
import org.robolectric.shadows.ShadowPendingIntent;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoreBroadcastReceiverTest extends RobolectricTestBase {

    private static final String ARBITRARY_STORE_NAME = "store name";
    private GroceryStoreBroadcastReceiver broadcastReceiver;

    @Before
    public void setUp() {
        super.setUp();
        broadcastReceiver = new GroceryStoreBroadcastReceiver();
        ContentValues reminderValues = new ReminderValuesBuilder().createDefaultReminderValues().build();
        RuntimeEnvironment.application.getContentResolver().insert(ReminderContract.Reminders.CONTENT_URI, reminderValues);
    }

    private Intent BuildIntentToListenFor() {
        Intent intentToListenFor =  new Intent(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT);

        return intentToListenFor;
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
    public void givenNoRemindersExistWhenTheIntentIsReceivedThenNoNotificationIsSent() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        RuntimeEnvironment.application.getContentResolver().delete(ReminderContract.Reminders.CONTENT_URI, "", null);
        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        assertEquals(0, shadowNotificationManager.size());
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
        intent.putExtra(ReminderContract.Locations.NAME, ARBITRARY_STORE_NAME);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));
        assertEquals(RuntimeEnvironment.application.getString(R.string.app_name) + ": " + ARBITRARY_STORE_NAME, notification.getContentTitle());
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
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));

        assertEquals(Settings.System.DEFAULT_NOTIFICATION_URI, notification.getRealNotification().sound);
    }

    @Test
    public void whenANotificationIsSentThenTheStoreNameIsStoredAsTheMostRecentStore() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
        intent.putExtra(ReminderContract.Locations.NAME, ARBITRARY_STORE_NAME);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        SharedPreferences sharedPreferences = RuntimeEnvironment.application
                .getSharedPreferences(RuntimeEnvironment.application.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);

        assertEquals(ARBITRARY_STORE_NAME, sharedPreferences.getString(GroceryReminderConstants.LAST_NOTIFIED_STORE_KEY, ""));
    }

    @Test
    public void whenANotificationIsSentThenTheNotificationTimeIsStoredAsTheMostRecentNotificationTime() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
        intent.putExtra(ReminderContract.Locations.NAME, ARBITRARY_STORE_NAME);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        SharedPreferences sharedPreferences = RuntimeEnvironment.application
                .getSharedPreferences(RuntimeEnvironment.application.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);

        assertTrue(sharedPreferences.getLong(GroceryReminderConstants.LAST_NOTIFICATION_TIME, 0) > 0);
    }

    @Test
    public void givenAStoreNotificationHasBeenStoredWhenAProximityAlertForTheSameStoreIsReceivedThenNoNotificationIsSent() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
        intent.putExtra(ReminderContract.Locations.NAME, ARBITRARY_STORE_NAME);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();

        Intent secondIntentForSameStore = BuildIntentToListenFor();
        secondIntentForSameStore.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
        secondIntentForSameStore.putExtra(ReminderContract.Locations.NAME, ARBITRARY_STORE_NAME);

        shadowNotificationManager.cancelAll();

        broadcastReceiver.onReceive(RuntimeEnvironment.application, secondIntentForSameStore);
        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNull(notification);
    }

    @Test
    public void givenANotificationIsSentWhenTheNotificationIsActedOnThenTheRemindersActivityIsLaunched() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));
        ShadowPendingIntent shadowPendingIntent = Shadows.shadowOf(notification.getRealNotification().contentIntent);
        ShadowIntent shadowIntent = Shadows.shadowOf(shadowPendingIntent.getSavedIntent());

        assertEquals(RemindersActivity.class.getName(), shadowIntent.getComponent().getClassName());
    }

    @Test
    public void givenANotificationIsSentWhenTheNotificationIsActedOnThenTheTheNotificationIsDismissed() {
        Intent intent = BuildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));

        assertTrue((notification.getRealNotification().flags & Notification.FLAG_AUTO_CANCEL) == Notification.FLAG_AUTO_CANCEL);
    }
}
