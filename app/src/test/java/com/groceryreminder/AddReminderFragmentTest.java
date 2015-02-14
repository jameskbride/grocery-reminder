package com.groceryreminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class AddReminderFragmentTest extends RobolectricTestBase {

    private MainActivity activity;

    @Before
    public void setUp() {
        this.activity = Robolectric.buildActivity(MainActivity.class).create().start().get();
    }

    @Test
    public void whenANewFragmentInstanceIsCreatedThenItIsNotNull() {
        AddReminderFragment addReminderFragment = AddReminderFragment.newInstance();

        assertNotNull(addReminderFragment);
    }

    @Test
    public void whenTheFragmentIsAttachedThenTheOnAddReminderListenerShouldBeSet() {
        AddReminderFragment reminderListFragment = AddReminderFragment.newInstance();
        startFragment(activity, reminderListFragment);
        reminderListFragment.onAttach(activity);

        OnAddReminderListener onAddReminderListener = reminderListFragment.getOnAddReminderListener();
        assertNotNull(onAddReminderListener);
    }
}
