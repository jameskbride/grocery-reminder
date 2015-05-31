package com.groceryreminder.injection;

import com.groceryreminder.domain.GroceryStoreLocationManager;
import com.groceryreminder.domain.GroceryStoreLocationManagerInterface;
import com.groceryreminder.domain.GroceryStoreManager;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.reminders.RemindersActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module(
        overrides = true,
        includes = {
                TestAndroidModule.class,
                TestRemoteResourcesModule.class
        },
        injects = {
                RemindersActivity.class,
                GroceryLocatorService.class,
                GroceryStoreManager.class,
                GroceryStoreLocationManager.class
        }
)
public class TestReminderModule {

    private GroceryStoreManagerInterface groceryStoreManagerMock = mock(GroceryStoreManagerInterface.class);
    private GroceryStoreLocationManagerInterface groceryStoreLocationManagerMock = mock(GroceryStoreLocationManagerInterface.class);

    @Provides
    @Singleton
    public GroceryStoreManagerInterface getGroceryStoreManager() {
        return groceryStoreManagerMock;
    }

    @Provides
    @Singleton
    public GroceryStoreLocationManagerInterface getGroceryStoreLocationManager() {
        return groceryStoreLocationManagerMock;
    }
}
