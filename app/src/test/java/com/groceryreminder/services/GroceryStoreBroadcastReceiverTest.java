package com.groceryreminder.services;

import android.content.ContentValues;
import android.content.Intent;
import android.location.LocationManager;

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

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        intentToListenFor.putExtra(ReminderContract.Locations.NAME, ARBITRARY_STORE_NAME);

        return intentToListenFor;
    }

    @Test
    public void whenAProximityAlertWithoutTheProximityEnteringFlagThenNoNotificationIsSent() {
        Intent intent = buildIntentToListenFor();

        GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerMock = getTestReminderModule().getGroceryStoreNotificationManager();
        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        verifyNoMoreInteractions(groceryStoreNotificationManagerMock);
    }

    @Test
    public void givenAValidProximityAlertWhenTheNoticeCannotBeSentThenTheNoticeIsNotSent() {
        Intent intent = buildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerMock = getTestReminderModule().getGroceryStoreNotificationManager();

        when(groceryStoreNotificationManagerMock.noticeCanBeSent(eq(ARBITRARY_STORE_NAME), anyLong())).thenReturn(false);
        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        verify(groceryStoreNotificationManagerMock).noticeCanBeSent(eq(ARBITRARY_STORE_NAME), anyLong());
        verifyNoMoreInteractions(groceryStoreNotificationManagerMock);
    }

    @Test
    public void givenAValidProximityAlertWhenTheNoticeCanBeSentThenTheNoticeIsSent() {
        Intent intent = buildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerMock = getTestReminderModule().getGroceryStoreNotificationManager();

        when(groceryStoreNotificationManagerMock.noticeCanBeSent(eq(ARBITRARY_STORE_NAME), anyLong())).thenReturn(true);
        broadcastReceiver.onReceive(RuntimeEnvironment.application, intent);

        verify(groceryStoreNotificationManagerMock).noticeCanBeSent(eq(ARBITRARY_STORE_NAME), anyLong());
        verify(groceryStoreNotificationManagerMock).sendNotification(intent);
        verify(groceryStoreNotificationManagerMock).saveNoticeDetails(eq(ARBITRARY_STORE_NAME), anyLong());
    }
}
