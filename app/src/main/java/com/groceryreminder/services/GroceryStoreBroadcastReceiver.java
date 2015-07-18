package com.groceryreminder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;
import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

public class GroceryStoreBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "StoreBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Receiving proximity alert.");
        Intent serviceIntent = new Intent(context, GroceryStoreNotificationService.class);
        serviceIntent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false));
        serviceIntent.putExtra(ReminderContract.Locations.NAME, intent.getStringExtra(ReminderContract.Locations.NAME));
        context.startService(serviceIntent);
    }
}
