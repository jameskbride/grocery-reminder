package com.groceryreminder;

import android.content.Context;
import android.location.LocationManager;

import static android.content.Context.LOCATION_SERVICE;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class AndroidModule {

    private ReminderApplication application;

    public AndroidModule(ReminderApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    public Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    public LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }
}
