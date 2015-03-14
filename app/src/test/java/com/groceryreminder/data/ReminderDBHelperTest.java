package com.groceryreminder.data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ReminderDBHelperTest {

    private ReminderDBHelper reminderDBHelper;

    @Before
    public void setUp() {
        ShadowApplication context = Robolectric.getShadowApplication();
        reminderDBHelper = new ReminderDBHelper(context.getApplicationContext());
    }

    @Test
    public void whenTheDBHelperIsCreatedThenTheDatabaseNameShouldBeSet() {
        assertEquals("grocery_reminder.sqlite", reminderDBHelper.getDatabaseName());
    }
}
