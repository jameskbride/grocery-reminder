package com.groceryreminder.data;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.RobolectricTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ReminderContentProviderTest extends RobolectricTestBase {

    private ReminderContentProvider provider;

    @Before
    public void setUp() {
        super.setUp();
        provider = new ReminderContentProvider();
    }

    @Test
    public void whenTheProviderIsCreatedThenItShouldBeInitialized() {
        ReminderContentProvider provider = new ReminderContentProvider();
        assertTrue(provider.onCreate());
    }

}
