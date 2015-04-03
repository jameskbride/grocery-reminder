package com.groceryreminder.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootBroadcastReceiver", "in BootBroadcastReceiver.onReceive");

        Intent serviceIntent = new Intent(context, GroceryLocatorService.class);
        context.startService(serviceIntent);
    }
}
