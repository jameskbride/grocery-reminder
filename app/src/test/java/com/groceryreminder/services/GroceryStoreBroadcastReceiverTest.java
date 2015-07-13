package com.groceryreminder.services;

import android.content.ContentValues;
import android.content.Intent;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;
import com.groceryreminder.testUtils.ReminderValuesBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoreBroadcastReceiverTest extends RobolectricTestBase {

    private static final String ARBITRARY_STORE_NAME = "store name";
    private GroceryStoreBroadcastReceiver broadcastReceiver;

    @Before
    public void setUp() {
        super.setUp();
        broadcastReceiver = new GroceryStoreBroadcastReceiver();
        ContentValues reminderValues = new ReminderValuesBuilder().createDefaultReminderValues().build();
        RuntimeEnvironment.application.getContentResolver().insert(ReminderContract.Reminders.CONTENT_URI, reminderValues);
    }

    private Intent buildIntentToListenFor() {
        Intent intentToListenFor =  new Intent(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT);

        return intentToListenFor;
    }

    @Test
    public void whenAProximityAlertWithoutTheProximityEnteringFlagThenNoNotificationIsSent() {
        Intent intent = buildIntentToListenFor();

        GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerMock = getTestReminderModule().getGroceryStoreNotificationManager();
        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        verifyNoMoreInteractions(groceryStoreNotificationManagerMock);
    }
}
