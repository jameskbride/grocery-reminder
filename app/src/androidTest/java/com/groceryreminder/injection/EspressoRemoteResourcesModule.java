package com.groceryreminder.injection;

import com.groceryreminder.GooglePlacesFake;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.walkercrou.places.GooglePlacesInterface;

@Module (
        overrides = true,
        library = true
)
public class EspressoRemoteResourcesModule {

    @Provides
    @Singleton
    public GooglePlacesInterface getGooglePlaces() {
        return new GooglePlacesFake();
    }
}
