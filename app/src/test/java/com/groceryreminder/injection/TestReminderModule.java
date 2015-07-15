package com.groceryreminder.injection;

import com.groceryreminder.domain.GroceryStoreLocationManager;
import com.groceryreminder.domain.GroceryStoreLocationManagerInterface;
import com.groceryreminder.domain.GroceryStoreManager;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.domain.GroceryStoreNotificationManager;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;
import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.services.GroceryStoreBroadcastReceiver;
import com.groceryreminder.services.GroceryStoreNotificationService;
import com.groceryreminder.views.reminders.RemindersActivity;
import com.groceryreminder.views.stores.GroceryStoresActivity;

import org.robolectric.RuntimeEnvironment;

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
                GroceryStoreLocationManager.class,
                GroceryStoresActivity.class,
                GroceryStoreNotificationService.class,
                GroceryStoreNotificationManager.class
        }
)
public class TestReminderModule {

    private GroceryStoreManagerInterface groceryStoreManagerMock = mock(GroceryStoreManagerInterface.class);
    private GroceryStoreLocationManagerInterface groceryStoreLocationManagerMock = mock(GroceryStoreLocationManagerInterface.class);
    private GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerMock = mock(GroceryStoreNotificationManagerInterface.class);

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

    @Provides
    @Singleton
    public GroceryStoreNotificationManagerInterface getGroceryStoreNotificationManager() {
        return groceryStoreNotificationManagerMock;
    }
}
