package com.groceryreminder.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.walkercrou.places.GooglePlacesInterface;

import static org.mockito.Mockito.mock;

@Module(
        library = true
)
public class TestRemoteResourcesModule {

    private GooglePlacesInterface googlePlacesMock = mock(GooglePlacesInterface.class);

    @Provides
    @Singleton
    public GooglePlacesInterface getGooglePlaces() {
        return googlePlacesMock;
    }
}
