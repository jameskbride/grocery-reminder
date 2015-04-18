package com.groceryreminder.services;

import android.content.Intent;

import com.groceryreminder.RobolectricTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryStoreBroadcastReceiverTest extends RobolectricTestBase {

    private GroceryStoreBroadcastReceiver broadcastReceiver;

    @Before
    public void setUp() {
        super.setUp();
        broadcastReceiver = new GroceryStoreBroadcastReceiver();
    }
    @Test
    public void whenTheProximityEventIntentIsSentThenTheBroadcastReceiverListensForIt() {
        Intent intent = new Intent("com.groceryreminder.STORE_PROXIMITY_EVENT");

        ShadowApplication shadowApplication = Robolectric.getShadowApplication();
        assertTrue(shadowApplication.hasReceiverForIntent(intent));
    }
}
