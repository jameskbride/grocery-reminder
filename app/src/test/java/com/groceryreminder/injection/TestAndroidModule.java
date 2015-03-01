package com.groceryreminder.injection;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.groceryreminder.views.MainActivity;

import org.robolectric.Robolectric;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;
import static org.mockito.Mockito.spy;

@Module(
        overrides = true,
        includes = AndroidModule.class,
        injects = {MainActivity.class}
)
public class TestAndroidModule {

    private LocationManager locationManagerSpy;

    public TestAndroidModule() {
        LocationManager locationManager = Robolectric.newInstanceOf(LocationManager.class);
        this.locationManagerSpy = spy(locationManager);
    }

    @Provides
    @Singleton
    public LocationManager getLocationManager() {
        return locationManagerSpy;
    }
}
