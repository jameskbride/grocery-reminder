package com.groceryreminder.injection;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.groceryreminder.domain.GroceryStoreManager;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.reminders.RemindersActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.GooglePlacesInterface;

@Module(
    includes = {AndroidModule.class},
    injects = {
      RemindersActivity.class,
      GroceryLocatorService.class,
      GroceryStoreManager.class
    },
    complete = false
)
public class ReminderModule {

    private static final String TAG = "ReminderModule";
    private ReminderApplication reminderApplication;

    public ReminderModule(ReminderApplication reminderApplication) {
        this.reminderApplication = reminderApplication;
    }

    @Provides
    @Singleton
    public GooglePlacesInterface getGooglePlaces() {

        String apiKey;
        try {
            ApplicationInfo applicationInfo = reminderApplication.getPackageManager()
                    .getApplicationInfo(reminderApplication.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            apiKey = bundle.getString("google-places-api-key");
            Log.d(TAG, "api key: " + apiKey);
            return new GooglePlaces(apiKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Provides
    @Singleton
    public GroceryStoreManagerInterface getGroceryStoreManager(GroceryStoreManager groceryStoreManager) {
        Log.d(TAG, "Providing the GroceryStoreManager");
        return groceryStoreManager;
    }
}
