package com.groceryreminder.injection;

import android.support.test.espresso.Espresso;
import android.util.Log;

import com.groceryreminder.domain.GroceryStoreLocationManager;
import com.groceryreminder.domain.GroceryStoreLocationManagerFake;
import com.groceryreminder.domain.GroceryStoreLocationManagerInterface;
import com.groceryreminder.domain.GroceryStoreManager;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.reminders.RemindersActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                AndroidModule.class,
                EspressoRemoteResourcesModule.class},
        injects = {
                RemindersActivity.class,
                GroceryLocatorService.class,
                GroceryStoreManager.class,
                GroceryStoreLocationManager.class
        },
        complete = false
)
public class EspressoReminderModule {
    private static final String TAG = "ReminderModule";

    @Provides
    @Singleton
    public GroceryStoreManagerInterface getGroceryStoreManager(GroceryStoreManager groceryStoreManager) {
        Log.d(TAG, "Providing the GroceryStoreManager");
        return groceryStoreManager;
    }

    @Provides
    @Singleton
    public GroceryStoreLocationManagerInterface getGroceryStoreLocationManager() {
        Log.d(TAG, "Providing the GroceryStoreLocationManager");
        return new GroceryStoreLocationManagerFake();
    }
}
