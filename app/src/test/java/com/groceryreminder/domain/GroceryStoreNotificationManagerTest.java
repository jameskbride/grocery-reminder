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
import com.groceryreminder.testUtils.ReminderValuesBuilder;
import com.groceryreminder.views.reminders.RemindersActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;
import org.robolectric.shadows.ShadowPendingIntent;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        setAllProvidersAccurate();
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

    private void setAccurateLocation(String provider) {
        Location location = buildAccurateLocation(provider);
        ShadowLocationManager shadowLocationManager = (ShadowLocationManager) Shadows.shadowOf(getTestAndroidModule().getLocationManager());
        shadowLocationManager.setLastKnownLocation(provider, location);
    }

    private void setInaccurateLocation(String provider) {
        Location location = buildInaccurateLocation(provider);
        ShadowLocationManager shadowLocationManager = (ShadowLocationManager) Shadows.shadowOf(getTestAndroidModule().getLocationManager());
        shadowLocationManager.setLastKnownLocation(provider, location);
    }

    private Location buildInaccurateLocation(String provider) {
        Location networkLocation = new Location(provider);
        networkLocation.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS + 1);
        return networkLocation;
    }

    private Location buildAccurateLocation(String provider) {
        Location networkLocation = new Location(provider);
        networkLocation.setAccuracy(GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS - 1);
        return networkLocation;
    }

    private void setAllProvidersAccurate() {
        setAccurateLocation(LocationManager.NETWORK_PROVIDER);
        setAccurateLocation(LocationManager.PASSIVE_PROVIDER);
        setAccurateLocation(LocationManager.GPS_PROVIDER);
    }

    @Test
    public void givenNoRemindersExistWhenTheIntentIsReceivedThenNoNotificationIsSent() {
        RuntimeEnvironment.application.getContentResolver().delete(ReminderContract.Reminders.CONTENT_URI, "", null);
        assertFalse(groceryStoreNotificationManager.noticeCanBeSent(ARBITRARY_STORE_NAME, System.currentTimeMillis()));
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
    public void givenAStoreNotificationHasBeenStoredWhenARequestToSendANotificationWithTheTheSameStoreIsReceivedThenNoNotificationIsSent() {
        groceryStoreNotificationManager.noticeCanBeSent(ARBITRARY_STORE_NAME, System.currentTimeMillis());

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        shadowNotificationManager.cancelAll();

        groceryStoreNotificationManager.noticeCanBeSent(ARBITRARY_STORE_NAME, System.currentTimeMillis() + GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME_MILLIS + 1);

        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNull(notification);
    }

    @Test
    public void givenAStoreNotificationHasBeenSentWhenARequestToSendANotificationIsReceivedUnderTheMinimumLocationUpdateTimeThenNoNotificationIsSent() {
        groceryStoreNotificationManager.noticeCanBeSent(ARBITRARY_STORE_NAME, System.currentTimeMillis());

        ShadowNotificationManager shadowNotificationManager = getShadowNotificationManager();
        shadowNotificationManager.cancelAll();

        groceryStoreNotificationManager.noticeCanBeSent(ARBITRARY_STORE_NAME + 1, System.currentTimeMillis() + GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME_MILLIS - 1);

        Notification notification = shadowNotificationManager.getNotification(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT);
        assertNull(notification);
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

    @Test
    public void whenAllProvidersAreInaccurateWThenANotificationIsNotSent() {
        setInaccurateLocation(LocationManager.NETWORK_PROVIDER);
        setInaccurateLocation(LocationManager.PASSIVE_PROVIDER);
        setInaccurateLocation(LocationManager.GPS_PROVIDER);

        assertFalse(groceryStoreNotificationManager.noticeCanBeSent("test", System.currentTimeMillis()));
    }

    @Test
    public void givenTheOtherProvidersAreInaccurateWhenTheLastPassiveLocationIsAccurateThenANotificationIsSent() {
        setInaccurateLocation(LocationManager.NETWORK_PROVIDER);
        setInaccurateLocation(LocationManager.GPS_PROVIDER);
        setAccurateLocation(LocationManager.PASSIVE_PROVIDER);

        assertTrue(groceryStoreNotificationManager.noticeCanBeSent("test", System.currentTimeMillis()));
    }

    @Test
    public void givenTheOtherProviderLocationsAreInaccurateWhenTheGPSLocationIsAccurateThenANotificationIsSent() {
        setInaccurateLocation(LocationManager.NETWORK_PROVIDER);
        setInaccurateLocation(LocationManager.PASSIVE_PROVIDER);
        setAccurateLocation(LocationManager.GPS_PROVIDER);

        assertTrue(groceryStoreNotificationManager.noticeCanBeSent("test", System.currentTimeMillis()));
    }
}
