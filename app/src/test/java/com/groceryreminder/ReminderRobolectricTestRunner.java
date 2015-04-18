package com.groceryreminder;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.SdkConfig;
import org.robolectric.manifest.AndroidManifest;

import java.io.File;
import java.util.Properties;

public class ReminderRobolectricTestRunner extends RobolectricTestRunner {

    private Properties properties;

    public ReminderRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        properties = new Properties();
        properties.setProperty("emulateSdk", "18");
        properties.setProperty("reportSdk", "18");
        properties.setProperty("manifest", configurationManifestPath());
        properties.setProperty("android.library.reference.1", "../../../../app/build/intermediates/exploded-aar/com.android.support/appcompat-v7/21.0.3");
    }

    private String configurationManifestPath() {
        String path = "src/main/AndroidManifest.xml";

        // android studio has a different execution root for tests than pure gradle
        // so we avoid here manual effort to get them running inside android studio
        if (!new File(path).exists()) {
            path = "app/" + path;
        }

        return path;
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        Config.Implementation updatedConfig = overwriteConfig(config);

        return super.getAppManifest(updatedConfig);
    }

    @Override
    protected SdkConfig pickSdkVersion(
            AndroidManifest appManifest, Config config) {
        // current Robolectric supports not the latest android SDK version
        // so we must downgrade to simulate the latest supported version.

        Config.Implementation updatedConfig = overwriteConfig(config);
        return super.pickSdkVersion(appManifest, updatedConfig);
    }

    protected Config.Implementation overwriteConfig(
            Config config) {
        return new Config.Implementation(config,
                Config.Implementation.fromProperties(properties));
    }
}
