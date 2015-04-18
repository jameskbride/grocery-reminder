package com.groceryreminder.services;

import android.content.Intent;

import com.groceryreminder.RobolectricTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class BootBroadcastReceiverTest extends RobolectricTestBase {

    BootBroadcastReceiver broadcastReceiver;

    @Before
    public void setUp() {
        super.setUp();
        broadcastReceiver = new BootBroadcastReceiver();
    }

    @Test
    public void givenTheApplicationIsConfiguredThenTheBootBroadcastReceiverIsRegistered() {
        List<ShadowApplication.Wrapper> registeredReceivers = Shadows.shadowOf(RuntimeEnvironment.application).getRegisteredReceivers();

        assertFalse(registeredReceivers.isEmpty());
        assertTrue(isBootBroadcastReceiverRegistered(registeredReceivers));
    }

    @Test
    public void whenTheBootCompletedIntentIsSentThenTheBroadcastReceiverListensForIt() {
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);

        ShadowApplication shadowApplication = Shadows.shadowOf(RuntimeEnvironment.application);
        assertTrue(shadowApplication.hasReceiverForIntent(intent));
    }

    @Test
    public void whenTheBootCompletedIntentIsReceivedThenTheGroceryLocatorServiceIsStarted() {
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);

        broadcastReceiver.onReceive(Shadows.shadowOf(RuntimeEnvironment.application).getApplicationContext(), intent);

        Intent serviceIntent = Shadows.shadowOf(RuntimeEnvironment.application).peekNextStartedService();
        assertEquals(GroceryLocatorService.class.getCanonicalName(), serviceIntent.getComponent().getClassName());
    }

    private boolean isBootBroadcastReceiverRegistered(List<ShadowApplication.Wrapper> registeredReceivers) {
        boolean receiverFound = false;
        for (ShadowApplication.Wrapper wrapper : registeredReceivers) {
            if (!receiverFound)
                receiverFound = BootBroadcastReceiver.class.getSimpleName().equals(
                        wrapper.broadcastReceiver.getClass().getSimpleName());
        }
        return receiverFound;
    }

}
