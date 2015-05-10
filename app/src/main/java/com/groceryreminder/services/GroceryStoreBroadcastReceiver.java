package com.groceryreminder.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.groceryreminder.R;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.views.reminders.RemindersActivity;

public class GroceryStoreBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
            PendingIntent resultPendingIntent = createRemindersActivityIntent(context);

            NotificationCompat.Builder builder = buildReminderNotification(context, resultPendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT, builder.build());
        }

    }

    private NotificationCompat.Builder buildReminderNotification(Context context, PendingIntent resultPendingIntent) {
        return new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_maps_local_grocery_store)
                        .setContentTitle(context.getString(R.string.app_name))
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
