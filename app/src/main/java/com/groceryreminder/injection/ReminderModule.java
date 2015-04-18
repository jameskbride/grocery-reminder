package com.groceryreminder.injection;

import android.util.Log;

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
        RemoteResourcesModule.class},
    injects = {
        RemindersActivity.class,
        GroceryLocatorService.class,
        GroceryStoreManager.class
    },
    complete = false
)
public class ReminderModule {

    private static final String TAG = "ReminderModule";

    @Provides
    @Singleton
    public GroceryStoreManagerInterface getGroceryStoreManager(GroceryStoreManager groceryStoreManager) {
        Log.d(TAG, "Providing the GroceryStoreManager");
        return groceryStoreManager;
    }
}
