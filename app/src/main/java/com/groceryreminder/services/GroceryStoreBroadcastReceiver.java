package com.groceryreminder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.groceryreminder.R;
import com.groceryreminder.domain.GroceryReminderConstants;

public class GroceryStoreBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
            return;
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_stat_maps_local_grocery_store);
        notificationManager.notify(GroceryReminderConstants.NOTIFICATION_PROXIMITY_ALERT, builder.build());
    }
}
