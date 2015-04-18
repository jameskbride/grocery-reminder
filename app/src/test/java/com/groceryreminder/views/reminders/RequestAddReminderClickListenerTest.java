package com.groceryreminder.views.reminders;

import com.groceryreminder.RobolectricTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class RequestAddReminderClickListenerTest extends RobolectricTestBase {

    private OnAddReminderRequestListener mockRequestListener = mock(OnAddReminderRequestListener.class);
    private RequestAddReminderClickListener clickListener;

    @Before
    public void setUp() {
        this.clickListener = new RequestAddReminderClickListener(mockRequestListener);
    }

    @Test
    public void whenTheClickListenerIsClickedThenTheAddReminderRequestListenerIsFired() {
        clickListener.onClick(null);
        verify(mockRequestListener).requestNewReminder();
    }
}
