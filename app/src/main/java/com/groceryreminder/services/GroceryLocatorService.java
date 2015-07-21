package com.groceryreminder.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.*;
import android.util.Log;

import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreLocationManagerInterface;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

public class GroceryLocatorService extends Service {

    private static final String TAG = "GroceryLocatorService";

    @Inject
    GroceryStoreLocationManagerInterface groceryStoreLocationManager;

    @Inject
    GroceryStoreManagerInterface groceryStoreManager;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    public GroceryLocatorService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ReminderApplication)getApplication()).inject(this);

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "In onHandleIntent");
        if (intent == null) {
            return START_STICKY;
        }

        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        message.obj = intent.getBooleanExtra(GroceryReminderConstants.LISTEN_FOR_GPS_EXTRA, false);
        mServiceHandler.handleMessage(message);

        return START_STICKY;
    }

    private class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            boolean listenForGPS = (boolean)msg.obj;
            if (listenForGPS) {
                groceryStoreManager.listenForLocationUpdates(true);
            } else {
                groceryStoreManager.listenForLocationUpdates(false);
            }
            Location location = groceryStoreLocationManager.getLastKnownLocation();
            if (location != null && groceryStoreManager.isBetterThanCurrentLocation(location)) {
                groceryStoreManager.handleLocationUpdated(location);
            }
        }
    }
}
