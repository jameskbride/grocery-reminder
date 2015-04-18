package com.groceryreminder.injection;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.GooglePlacesInterface;

@Module(library = true
)
public class RemoteResourcesModule {

    private final ReminderApplication reminderApplication;

    private static final String TAG = "RemoteResourcesModule";

    public RemoteResourcesModule(ReminderApplication reminderApplication) {
        this.reminderApplication = reminderApplication;
    }

    @Provides
    @Singleton
    public GooglePlacesInterface getGooglePlaces() {

        String apiKey;
        try {
            ApplicationInfo applicationInfo = reminderApplication.getPackageManager()
                    .getApplicationInfo(reminderApplication.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            apiKey = bundle.getString("google-places-api-key");
            Log.d(TAG, "api key: " + apiKey);
            return new GooglePlaces(apiKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
