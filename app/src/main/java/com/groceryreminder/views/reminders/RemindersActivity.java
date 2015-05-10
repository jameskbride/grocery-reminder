package com.groceryreminder.views.reminders;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.groceryreminder.R;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.injection.views.ReminderFragmentBaseActivity;
import com.groceryreminder.models.Reminder;
import com.groceryreminder.services.GroceryLocatorService;
import com.groceryreminder.views.stores.GroceryStoresActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class RemindersActivity extends ReminderFragmentBaseActivity implements OnAddReminderRequestListener, OnAddReminderListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String REMINDER_LIST_FRAGMENT = "REMINDER_LIST_FRAGMENT";
    private static final String TAG = "RemindersActivity";
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
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
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
        ContentValues values = new ContentValues();
        values.put(ReminderContract.Reminders.DESCRIPTION, value);
        getContentResolver().insert(ReminderContract.Reminders.CONTENT_URI, values);
        reminderListFragment.addReminder(reminder);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, reminderListFragment, REMINDER_LIST_FRAGMENT)
                .commit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "in onCreateLoader");
        CursorLoader loader = new CursorLoader(this,
                ReminderContract.Reminders.CONTENT_URI,
                ReminderContract.Reminders.PROJECT_ALL,
                null,
                null,
                ReminderContract.Reminders.SORT_ORDER_DEFAULT);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
