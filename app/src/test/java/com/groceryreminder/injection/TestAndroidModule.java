package com.groceryreminder.injection;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true
)
public class TestAndroidModule {

    @Provides
    @Singleton
    public LocationManager getLocationManager() {
        return (LocationManager) RuntimeEnvironment.application.getSystemService(Application.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    @ForApplication
    public Application getApplicationContext() {
        return RuntimeEnvironment.application;
    }
}
