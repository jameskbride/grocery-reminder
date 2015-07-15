package com.groceryreminder.services;

import android.content.Intent;
import android.location.LocationManager;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreNotificationManagerInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoreNotificationServiceTest extends RobolectricTestBase {

    private GroceryStoreNotificationService groceryStoreNotificationService;
    private static final String ARBITRARY_STORE_NAME = "store name";

    @Before
    public void setUp() {
        super.setUp();
        groceryStoreNotificationService = new GroceryStoreNotificationService();
        groceryStoreNotificationService.onCreate();
    }

    private Intent buildIntentToListenFor() {
        Intent intentToListenFor =  new Intent(GroceryReminderConstants.ACTION_STORE_PROXIMITY_EVENT);
        intentToListenFor.putExtra(ReminderContract.Locations.NAME, ARBITRARY_STORE_NAME);

        return intentToListenFor;
    }

    @Test
    public void whenARequestWithoutTheProximityEnteringFlagThenNoNotificationIsSent() {
        Intent intent = buildIntentToListenFor();

        GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerMock = getTestReminderModule().getGroceryStoreNotificationManager();
        groceryStoreNotificationService.onHandleIntent(intent);

        verifyNoMoreInteractions(groceryStoreNotificationManagerMock);
    }

    @Test
    public void givenAValidRequestWhenTheNoticeCannotBeSentThenTheNoticeIsNotSent() {
        Intent intent = buildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerMock = getTestReminderModule().getGroceryStoreNotificationManager();

        when(groceryStoreNotificationManagerMock.noticeCanBeSent(eq(ARBITRARY_STORE_NAME), anyLong())).thenReturn(false);
        groceryStoreNotificationService.onHandleIntent(intent);

        verify(groceryStoreNotificationManagerMock).noticeCanBeSent(eq(ARBITRARY_STORE_NAME), anyLong());
        verifyNoMoreInteractions(groceryStoreNotificationManagerMock);
    }

    @Test
    public void givenAValidRequestWhenTheNoticeCanBeSentThenTheNoticeIsSent() {
        Intent intent = buildIntentToListenFor();
        intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

        GroceryStoreNotificationManagerInterface groceryStoreNotificationManagerMock = getTestReminderModule().getGroceryStoreNotificationManager();

        when(groceryStoreNotificationManagerMock.noticeCanBeSent(eq(ARBITRARY_STORE_NAME), anyLong())).thenReturn(true);
        groceryStoreNotificationService.onHandleIntent(intent);

        verify(groceryStoreNotificationManagerMock).noticeCanBeSent(eq(ARBITRARY_STORE_NAME), anyLong());
        verify(groceryStoreNotificationManagerMock).sendNotification(intent);
        verify(groceryStoreNotificationManagerMock).saveNoticeDetails(eq(ARBITRARY_STORE_NAME), anyLong());
    }
}
