package com.groceryreminder.injection;

import android.util.Log;

import com.groceryreminder.domain.GroceryStoreManager;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.reminders.RemindersActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.walkercrou.places.GooglePlacesInterface;

import static org.mockito.Mockito.mock;

@Module(
        overrides = true,
        includes = {TestAndroidModule.class},
        complete = false,
        injects = {
                RemindersActivity.class,
                GroceryLocatorService.class,
                GroceryStoreManager.class
        }
)
public class TestReminderModule {

    private GooglePlacesInterface googlePlacesMock = mock(GooglePlacesInterface.class);

    @Provides
    @Singleton
    public GooglePlacesInterface getGooglePlaces() {
        return googlePlacesMock;
    }

    @Provides
    @Singleton
    public GroceryStoreManagerInterface getGroceryStoreManager(GroceryStoreManager groceryStoreManager) {
        Log.d("ReminderModule", "Providing the GroceryStoreManager");
        return groceryStoreManager;
    }
}
