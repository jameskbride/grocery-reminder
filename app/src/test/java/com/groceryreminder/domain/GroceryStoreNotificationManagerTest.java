package com.groceryreminder.domain;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.shadows.ShadowLocationManager;
import com.groceryreminder.testUtils.LocationValuesBuilder;
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
import org.robolectric.shadows.ShadowLocation;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;
import org.robolectric.shadows.ShadowPendingIntent;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, shadows = {ShadowLocationManager.class})
public class GroceryStoreNotificationManagerTest extends RobolectricTestBase {

    public static final String ARBITRARY_STORE_NAME = "test";
    GroceryStoreNotificationManager groceryStoreNotificationManager;

    @Before
    public void setUp() {
        super.setUp();
        ContentValues reminderValues = new ReminderValuesBuilder().createDefaultReminderValues().build();
        RuntimeEnvironment.application.getContentResolver().insert(ReminderContract.Reminders.CONTENT_URI, reminderValues);
        groceryStoreNotificationManager = new GroceryStoreNotificationManager(RuntimeEnvironment.application, getTestAndroidModule().getLocationManager());
    }

    private Intent buildIntentToListenFor() {
        Intent intentToListenFor =  new Intent(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT);
        intentToListenFor.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
        intentToListenFor.putExtra(ReminderContract.Locations.NAME, ARBITRARY_STORE_NAME);

        return intentToListenFor;
    }

    private ShadowNotificationManager getShadowNotificationManager() {
        return Shadows.shadowOf((NotificationManager)
                RuntimeEnvironment.application.getSystemService(Context.NOTIFICATION_SERVICE));
    }

    @Test
    public void whenANotificationIsSentThenTheNotificationShouldBeCreated() {
        Intent intent = buildIntentToListenFor();

        groceryStoreNotificationManager.sendNotification(intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNotNull(notification);
    }

    @Test
    public void whenANotificationIsSentThenTheSmallIconIsSet() {
        Intent intent = buildIntentToListenFor();

        groceryStoreNotificationManager.sendNotification(intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertEquals(R.drawable.ic_stat_maps_local_grocery_store, notification.icon);
    }

    @Test
    public void whenANotificationIsSentThenTheContentTitleIsSet() {
        Intent intent = buildIntentToListenFor();

        groceryStoreNotificationManager.sendNotification(intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));
        assertEquals(RuntimeEnvironment.application.getString(R.string.app_name) + ": " + ARBITRARY_STORE_NAME, notification.getContentTitle());
    }

    @Test
    public void whenANotificationIsSentThenTheContentTextIsSet() {
        Intent intent = buildIntentToListenFor();

        groceryStoreNotificationManager.sendNotification(intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));
        assertEquals(RuntimeEnvironment.application.getString(R.string.reminder_notification), notification.getContentText());
    }

    @Test
    public void whenANotificationIsSentThenTheVibrationIsSet() {
        Intent intent = buildIntentToListenFor();

        groceryStoreNotificationManager.sendNotification(intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));

        assertArrayEquals(GroceryReminderConstants.PROXIMITY_VIBRATION_PATTERN, notification.getRealNotification().vibrate);
    }

    @Test
    public void whenANotificationIsSentThenTheDefaultNotificationSoundPlays() {
        Intent intent = buildIntentToListenFor();

        groceryStoreNotificationManager.sendNotification(intent);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));

        assertEquals(Settings.System.DEFAULT_NOTIFICATION_URI, notification.getRealNotification().sound);
    }

    @Test
    public void whenANotificationIsSavedThenTheStoreNameIsStoredAsTheMostRecentStore() {
        groceryStoreNotificationManager.saveNoticeDetails(ARBITRARY_STORE_NAME, System.currentTimeMillis());

        SharedPreferences sharedPreferences = RuntimeEnvironment.application
                .getSharedPreferences(RuntimeEnvironment.application.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);

        assertEquals(ARBITRARY_STORE_NAME, sharedPreferences.getString(GroceryReminderConstants.LAST_NOTIFIED_STORE_KEY, ""));
    }

    @Test
    public void whenANotificationIsSentThenTheNotificationTimeIsStoredAsTheMostRecentNotificationTime() {
        long notificationTime = System.currentTimeMillis();

        groceryStoreNotificationManager.saveNoticeDetails(ARBITRARY_STORE_NAME, notificationTime);
        SharedPreferences sharedPreferences = RuntimeEnvironment.application
                .getSharedPreferences(RuntimeEnvironment.application.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);

        assertTrue(sharedPreferences.getLong(GroceryReminderConstants.LAST_NOTIFICATION_TIME, 0) > 0);
    }

    @Test
    public void whenThereAreNoStoresThenTheNotificationIsNotSent() {
        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();

        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);
        shadowApplication.getContentResolver().delete(ReminderContract.Locations.CONTENT_URI, null, null);

        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), System.currentTimeMillis());

        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNull(notification);
    }

    @Test
    public void givenThereIsAStoreNearbyThenTheNotificationIsSent() {
        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();

        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);

        insertStoreLocation(shadowApplication);

        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_GEOFENCE_RADIUS_METERS});

        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), System.currentTimeMillis());

        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNotNull(notification);
    }

    @Test
    public void whenANotificationIsSentThenTheTheNotificationDetailsAreSaved() {
        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);

        insertStoreLocation(shadowApplication);

        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_GEOFENCE_RADIUS_METERS});

        long currentTime = System.currentTimeMillis();
        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), currentTime);

        SharedPreferences sharedPreferences = shadowApplication.getSharedPreferences(shadowApplication.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);
        assertEquals(ARBITRARY_STORE_NAME, sharedPreferences.getString(GroceryReminderConstants.LAST_NOTIFIED_STORE_KEY, ""));
        assertEquals(currentTime, sharedPreferences.getLong(GroceryReminderConstants.LAST_NOTIFICATION_TIME, 0));
    }

    @Test
    public void whenANotificationIsSentThenTheTheNotificationIncludesTheStoreName() {
        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);

        insertStoreLocation(shadowApplication);

        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_GEOFENCE_RADIUS_METERS});

        long currentTime = System.currentTimeMillis();
        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), currentTime);

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        ShadowNotification shadowNotification = (ShadowNotification)Shadows.shadowOf(notification);
        assertTrue(((String)shadowNotification.getContentTitle()).contains(ARBITRARY_STORE_NAME));
    }

    private void insertStoreLocation(ShadowApplication shadowApplication) {
        ContentValues locationValues = new LocationValuesBuilder().createDefaultLocationValues().withName(ARBITRARY_STORE_NAME).build();
        shadowApplication.getContentResolver().insert(ReminderContract.Locations.CONTENT_URI, locationValues);
    }

    @Test
    public void givenThereAreStoresButNotNearbyThenTheNotificationIsNotSent() {
        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();

        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);

        insertStoreLocation(shadowApplication);

        ShadowLocation.setDistanceBetween(new float[] {(float)GroceryReminderConstants.LOCATION_GEOFENCE_RADIUS_METERS + 1});

        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), System.currentTimeMillis());

        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNull(notification);
    }

    @Test
    public void givenAStoreNotificationHasBeenStoredWhenARequestToSendANotificationWithTheTheSameStoreIsReceivedThenTheNotificationIsNotSent() {
        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);

        insertStoreLocation(shadowApplication);
        SharedPreferences sharedPreferences = shadowApplication.getSharedPreferences(shadowApplication.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(GroceryReminderConstants.LAST_NOTIFIED_STORE_KEY, ARBITRARY_STORE_NAME).commit();

        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_GEOFENCE_RADIUS_METERS});

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();

        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), System.currentTimeMillis());

        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNull(notification);
    }

    @Test
    public void givenAStoreNotificationHasBeenSentWhenARequestToSendANotificationIsReceivedUnderTheMinimumLocationUpdateTimeThenTheNotificationIsNotSent() {
        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);

        insertStoreLocation(shadowApplication);
        SharedPreferences sharedPreferences = shadowApplication.getSharedPreferences(shadowApplication.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(GroceryReminderConstants.LAST_NOTIFIED_STORE_KEY, ARBITRARY_STORE_NAME + 1)
                .putLong(GroceryReminderConstants.LAST_NOTIFICATION_TIME, System.currentTimeMillis())
                .commit();

        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_GEOFENCE_RADIUS_METERS});

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();

        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), System.currentTimeMillis());

        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNull(notification);
    }

    @Test
    public void givenAStoreNotificationHasNotBeenSentWhenARequestToSendANotificationIsReceivedThenTheIsSent() {
        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);

        insertStoreLocation(shadowApplication);
        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_GEOFENCE_RADIUS_METERS});

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();

        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), System.currentTimeMillis());

        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNotNull(notification);
    }

    @Test
    public void whenNoRemindersExistThenNoNotificationIsSent() {
        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);
        shadowApplication.getContentResolver().delete(ReminderContract.Reminders.CONTENT_URI, null, null);
        insertStoreLocation(shadowApplication);
        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_GEOFENCE_RADIUS_METERS});

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();

        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), System.currentTimeMillis());

        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNull(notification);
    }

    @Test
    public void whenThereAreMultipleStoresNearbyThenOnlyOneNotificationIsSent() {
        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();

        ShadowApplication shadowApplication = (ShadowApplication) Shadows.shadowOf(RuntimeEnvironment.application);

        insertStoreLocation(shadowApplication);
        insertStoreLocation(shadowApplication);

        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_GEOFENCE_RADIUS_METERS});

        groceryStoreNotificationManager.sendPotentialNotification(new Location(LocationManager.GPS_PROVIDER), System.currentTimeMillis());

        List<Notification> notifications = shadowNotificationManager.getAllNotifications();
        assertEquals(1, notifications.size());
    }

    @Test
    public void givenANotificationIsSentWhenTheNotificationIsActedOnThenTheRemindersActivityIsLaunched() {
        groceryStoreNotificationManager.sendNotification(buildIntentToListenFor());

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));
        ShadowPendingIntent shadowPendingIntent = Shadows.shadowOf(notification.getRealNotification().contentIntent);
        ShadowIntent shadowIntent = Shadows.shadowOf(shadowPendingIntent.getSavedIntent());

        assertEquals(RemindersActivity.class.getName(), shadowIntent.getComponent().getClassName());
    }

    @Test
    public void givenANotificationIsSentWhenTheNotificationIsActedOnThenTheTheNotificationIsDismissed() {
        groceryStoreNotificationManager.sendNotification(buildIntentToListenFor());

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        ShadowNotification notification = Shadows.shadowOf(shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT));

        assertTrue((notification.getRealNotification().flags & Notification.FLAG_AUTO_CANCEL) == Notification.FLAG_AUTO_CANCEL);
    }
}
