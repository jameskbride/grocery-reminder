package com.groceryreminder.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.walkercrou.places.GooglePlacesInterface;

import static org.mockito.Mockito.mock;

@Module (
        overrides = true,
        library = true
)
public class EspressoRemoteResourcesModule {

    private GooglePlacesInterface googlePlacesMock = mock(GooglePlacesInterface.class);

    @Provides
    @Singleton
    public GooglePlacesInterface getGooglePlaces() {
        return googlePlacesMock;
    }
}
