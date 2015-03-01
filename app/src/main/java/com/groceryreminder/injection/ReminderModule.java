package com.groceryreminder.injection;

import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.GooglePlacesInterface;

@Module(
    injects = {
      GroceryLocatorService.class
    },
    complete = false
)
public class ReminderModule {

    @Provides
    @Singleton
    public GooglePlacesInterface getGooglePlaces() {
        return new GooglePlaces("test");
    }
}
