package com.groceryreminder.views.reminders;

import android.os.Bundle;
import android.support.v7.appcompat.BuildConfig;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "com.groceryreminder")
public class AddReminderFragmentTest extends RobolectricTestBase {

    private RemindersActivity activity;

    @Before
    public void setUp() {
        this.activity = Robolectric.buildActivity(RemindersActivity.class).create().start().get();
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

    @Test
    public void whenTheReminderTextIsIsEmptyThenTheAddReminderButtonIsDisabled() {
        AddReminderFragment reminderListFragment = getAddReminderFragmentFromOnCreateView();

        Button addReminderButton = getAddReminderButton(reminderListFragment);
        assertFalse(addReminderButton.isEnabled());
    }

    @Test
    public void whenTheReminderTextIsIsSetThenTheAddReminderButtonIsEnabled() {
        AddReminderFragment reminderListFragment = getAddReminderFragmentFromOnCreateView();
        EditText reminderText = getReminderText(reminderListFragment);

        reminderText.setText("test");

        Button addReminderButton = getAddReminderButton(reminderListFragment);
        assertTrue(addReminderButton.isEnabled());
    }

    @Test
    public void givenTheReminderTextIsSetWhenTheTextIsClearedThenTheAddReminderButtonIsDisabled() {
        AddReminderFragment reminderListFragment = getAddReminderFragmentFromOnCreateView();
        EditText reminderText = getReminderText(reminderListFragment);

        reminderText.setText("test");
        reminderText.setText("");

        Button addReminderButton = getAddReminderButton(reminderListFragment);
        assertFalse(addReminderButton.isEnabled());
    }

    @Test
    public void whenTheReminderTextIsSetToWhitespaceThenTheAddReminderButtonIsDisabled() {
        AddReminderFragment reminderListFragment = getAddReminderFragmentFromOnCreateView();
        EditText reminderText = getReminderText(reminderListFragment);

        reminderText.setText(" ");

        Button addReminderButton = getAddReminderButton(reminderListFragment);
        assertFalse(addReminderButton.isEnabled());
    }

    private Button getAddReminderButton(AddReminderFragment reminderListFragment) {
        return (Button)reminderListFragment.getView().findViewById(R.id.add_reminder_button);
    }

    private EditText getReminderText(AddReminderFragment reminderListFragment) {
        return (EditText)reminderListFragment.getView().findViewById(R.id.add_reminder_edit);
    }

    private AddReminderFragment getAddReminderFragmentFromOnCreateView() {
        AddReminderFragment reminderListFragment = AddReminderFragment.newInstance();
        startFragment(activity, reminderListFragment);
        reminderListFragment.onAttach(activity);
        reminderListFragment.onCreate(new Bundle());
        reminderListFragment.onCreateView(LayoutInflater.from(activity),
                (ViewGroup)activity.findViewById(R.id.reminder_fragment_container), null);

        return reminderListFragment;
    }
}
