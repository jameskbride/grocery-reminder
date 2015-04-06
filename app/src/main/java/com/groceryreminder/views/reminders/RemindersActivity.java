package com.groceryreminder.views.reminders;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.groceryreminder.R;
import com.groceryreminder.injection.views.ReminderFragmentBaseActivity;
import com.groceryreminder.models.Reminder;
import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.stores.GroceryStoresActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class RemindersActivity extends ReminderFragmentBaseActivity implements OnAddReminderRequestListener, OnAddReminderListener {

    public static final String REMINDER_LIST_FRAGMENT = "REMINDER_LIST_FRAGMENT";
    @Inject
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminders_activity);

        List<Reminder> reminders = new ArrayList<Reminder>();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, ReminderListFragment.newInstance(reminders), REMINDER_LIST_FRAGMENT)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_find_stores) {
            startService(new Intent(this, GroceryLocatorService.class));
            startActivity(new Intent(this, GroceryStoresActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestNewReminder() {
        AddReminderFragment addReminderFragment = AddReminderFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, addReminderFragment).addToBackStack(null).commit();
    }

    @Override
    public void addReminder(String value) {
        ReminderListFragment reminderListFragment = (ReminderListFragment)getSupportFragmentManager()
                .findFragmentByTag(REMINDER_LIST_FRAGMENT);
        Reminder reminder = new Reminder(value);
        reminderListFragment.addReminder(reminder);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, reminderListFragment, REMINDER_LIST_FRAGMENT)
                .commit();
    }
}
