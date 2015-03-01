package com.groceryreminder.injection;

import android.content.Context;
import android.location.LocationManager;

import com.groceryreminder.views.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;

@Module(library = true,
    injects = {MainActivity.class}
)
public class AndroidModule {

    private ReminderApplication application;

    public AndroidModule(ReminderApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    public Context getApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    public LocationManager getLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }
}
