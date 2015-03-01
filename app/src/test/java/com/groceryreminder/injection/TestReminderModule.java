package com.groceryreminder.injection;

import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.MainActivity;

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
                MainActivity.class,
                GroceryLocatorService.class
        }
)
public class TestReminderModule {

    private GooglePlacesInterface googlePlacesMock = mock(GooglePlacesInterface.class);

    @Provides
    @Singleton
    public GooglePlacesInterface getGooglePlaces() {
        return googlePlacesMock;
    }
}
