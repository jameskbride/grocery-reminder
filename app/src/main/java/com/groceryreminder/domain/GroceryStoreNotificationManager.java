package com.groceryreminder.domain;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.groceryreminder.R;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.injection.ForApplication;
import com.groceryreminder.views.reminders.RemindersActivity;

import javax.inject.Inject;

public class GroceryStoreNotificationManager implements GroceryStoreNotificationManagerInterface {

    Application context;
    LocationManager locationManager;

    @Inject
    public GroceryStoreNotificationManager(@ForApplication Application context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;
    }

    @Override
    public void saveNoticeDetails(String currentStoreName, long currentTime) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(GroceryReminderConstants.LAST_NOTIFIED_STORE_KEY, currentStoreName)
                .putLong(GroceryReminderConstants.LAST_NOTIFICATION_TIME, currentTime)
                .commit();
    }

    @Override
    public void sendNotification(Intent intent) {
        PendingIntent resultPendingIntent = createRemindersActivityIntent(context);
        NotificationCompat.Builder builder = buildReminderNotification(resultPendingIntent, intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT, builder.build());
    }

    @Override
    public boolean noticeCanBeSent(String currentStoreName, long currentTime) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.reminder_pref_key), Context.MODE_PRIVATE);
        String lastNotifiedStore = sharedPreferences.getString(GroceryReminderConstants.LAST_NOTIFIED_STORE_KEY, "");
        long lastNotificationTime = sharedPreferences.getLong(GroceryReminderConstants.LAST_NOTIFICATION_TIME, 0);

        boolean locationIsAccurate = isLocationIsAccurate();

        return remindersExist() &&
                !isNotificationForCurrentStore(lastNotifiedStore, currentStoreName) &&
                !notificationIsTooRecent(lastNotificationTime, currentTime) &&
                locationIsAccurate;
    }

    private boolean isLocationIsAccurate() {
        boolean networkLocationIsAccurate = isLocationProviderAccurate(LocationManager.NETWORK_PROVIDER);
        boolean passiveLocationIsAccurate = isLocationProviderAccurate(LocationManager.PASSIVE_PROVIDER);
        boolean gpsLocationIsAccurate = isLocationProviderAccurate(LocationManager.GPS_PROVIDER);

        return (networkLocationIsAccurate || passiveLocationIsAccurate || gpsLocationIsAccurate);
    }

    private boolean isLocationProviderAccurate(String provider) {
        Location location = null;
        if (locationManager.isProviderEnabled(provider)) {
            location = locationManager.getLastKnownLocation(provider);
        }

        boolean locationIsAccurate = false;
        if (location != null && location.getAccuracy() <= GroceryReminderConstants.MAXIMUM_ACCURACY_IN_METERS) {
            locationIsAccurate = true;
        }
        return locationIsAccurate;
    }

    private boolean remindersExist() {
        Cursor cursor = context.getContentResolver().query(ReminderContract.Reminders.CONTENT_URI, ReminderContract.Reminders.PROJECT_ALL, "", null, null);

        return cursor.getCount() > 0;
    }

    private boolean notificationIsTooRecent(long lastNotificationTime, long currentTime) {
        return (currentTime - lastNotificationTime) <= GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME_MILLIS;
    }

    private boolean isNotificationForCurrentStore(String lastNotifiedStore, String currentStoreName) {
        return lastNotifiedStore.equals(currentStoreName);
    }

    private NotificationCompat.Builder buildReminderNotification(PendingIntent resultPendingIntent, Intent intent) {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_maps_local_grocery_store)
                .setContentTitle(context.getString(R.string.app_name) + ": " + intent.getStringExtra(ReminderContract.Locations.NAME))
                .setContentText(context.getString(R.string.reminder_notification))
                .setVibrate(GroceryReminderConstants.PROXIMITY_VIBRATION_PATTERN)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }

    private PendingIntent createRemindersActivityIntent(Context context) {
        Intent remindersActivityIntent = new Intent(context, RemindersActivity.class);
        return PendingIntent
                .getActivity(context, 0, remindersActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}