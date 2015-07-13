package com.groceryreminder.domain;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.groceryreminder.R;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.views.reminders.RemindersActivity;

public class GroceryStoreNotificationManager {
    public GroceryStoreNotificationManager() {
    }

    public boolean remindersExist(Cursor cursor) {
        return cursor.getCount() > 0;
    }

    public void saveNoticeDetails(SharedPreferences sharedPreferences, String currentStoreName, long currentTime) {
        sharedPreferences.edit()
                .putString(GroceryReminderConstants.LAST_NOTIFIED_STORE_KEY, currentStoreName)
                .putLong(GroceryReminderConstants.LAST_NOTIFICATION_TIME, currentTime)
                .commit();
    }

    public void sendNotification(Context context, Intent intent) {
        PendingIntent resultPendingIntent = createRemindersActivityIntent(context);
        NotificationCompat.Builder builder = buildReminderNotification(context, resultPendingIntent, intent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT, builder.build());
    }

    public boolean noticeCanBeSent(String lastNotifiedStore, String currentStoreName, long lastNotificationTime, long currentTime) {
        return !isNotificationForCurrentStore(lastNotifiedStore, currentStoreName) && !notificationIsTooRecent(lastNotificationTime, currentTime);
    }

    public boolean notificationIsTooRecent(long lastNotificationTime, long currentTime) {
        return (currentTime - lastNotificationTime) <= GroceryReminderConstants.MIN_LOCATION_UPDATE_TIME_MILLIS;
    }

    public boolean isNotificationForCurrentStore(String lastNotifiedStore, String currentStoreName) {
        return lastNotifiedStore.equals(currentStoreName);
    }

    public NotificationCompat.Builder buildReminderNotification(Context context, PendingIntent resultPendingIntent, Intent intent) {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_maps_local_grocery_store)
                .setContentTitle(context.getString(R.string.app_name) + ": " + intent.getStringExtra(ReminderContract.Locations.NAME))
                .setContentText(context.getString(R.string.reminder_notification))
                .setVibrate(GroceryReminderConstants.PROXIMITY_VIBRATION_PATTERN)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }

    public PendingIntent createRemindersActivityIntent(Context context) {
        Intent remindersActivityIntent = new Intent(context, RemindersActivity.class);
        return PendingIntent
                .getActivity(context, 0, remindersActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}