package com.groceryreminder.views.reminders;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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


public class RemindersActivity extends ReminderFragmentBaseActivity implements OnAddReminderRequestListener, OnReminderDataChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String REMINDER_LIST_FRAGMENT = "REMINDER_LIST_FRAGMENT";
    private static final String TAG = "RemindersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminders_activity);

        List<Reminder> reminders = new ArrayList<Reminder>();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.reminder_fragment_container, ReminderListFragment.newInstance(reminders), REMINDER_LIST_FRAGMENT)
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
                .replace(R.id.reminder_fragment_container, addReminderFragment).addToBackStack(null).commit();
    }

    @Override
    public void addReminder(String value) {
        ReminderListFragment reminderListFragment = (ReminderListFragment)getSupportFragmentManager()
                .findFragmentByTag(REMINDER_LIST_FRAGMENT);
        ContentValues values = new ContentValues();
        values.put(ReminderContract.Reminders.DESCRIPTION, value);
        getContentResolver().insert(ReminderContract.Reminders.CONTENT_URI, values);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reminder_fragment_container, reminderListFragment, REMINDER_LIST_FRAGMENT)
                .commit();
    }

    @Override
    public int removeReminder(Reminder reminder) {
        Uri deleteReminderUri = ContentUris.withAppendedId(ReminderContract.Reminders.CONTENT_URI, reminder.getId());

        return getContentResolver().delete(deleteReminderUri, "", null);
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<Reminder> reminders = new ArrayList<Reminder>();
        Log.d(TAG, "In onLoadFinished");
        while (cursor.moveToNext()) {
            Long reminderId = cursor.getLong(0);
            String reminderDescription = cursor.getString(1);
            Log.d(TAG, "Loading reminder from cursor: " + reminderDescription);
            Reminder store = new Reminder(reminderId, reminderDescription);
            reminders.add(store);
        }

        ReminderListFragment groceryStoreListFragment =
                (ReminderListFragment)getSupportFragmentManager().findFragmentById(R.id.reminder_fragment_container);
        groceryStoreListFragment.setReminders(reminders);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "In onLoaderReset");
        ReminderListFragment reminderListFragment =
                (ReminderListFragment)getSupportFragmentManager().findFragmentById(R.id.reminder_fragment_container);

        if (reminderListFragment != null) {
            reminderListFragment.setReminders(new ArrayList<Reminder>());
        }
    }
}
