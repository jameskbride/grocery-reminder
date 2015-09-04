package com.groceryreminder.views.stores;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.groceryreminder.R;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.injection.views.ReminderFragmentBaseActivity;
import com.groceryreminder.models.GroceryStore;
import com.groceryreminder.services.GroceryLocatorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GroceryStoresActivity extends ReminderFragmentBaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "StoresActivity";

    @Inject
    GroceryStoreManagerInterface groceryStoreManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocery_stores_activity);
        GroceryStoreListFragment groceryStoreListFragment = GroceryStoreListFragment.newInstance(new ArrayList<GroceryStore>());
        getSupportFragmentManager().beginTransaction().add(R.id.stores_fragment_container, groceryStoreListFragment).commit();
        this.progressDialog = ProgressDialog.show(this, getString(R.string.loading_stores_dialog_title), getString(R.string.loading_stores_dialog_message), true);
        this.progressDialog.setCancelable(true);
        this.progressDialog.setIndeterminate(true);
        getSupportLoaderManager().initLoader(0, savedInstanceState, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(this, GroceryLocatorService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stores_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "in onCreateLoader");
        CursorLoader loader = new CursorLoader(this,
                ReminderContract.Locations.CONTENT_URI,
                ReminderContract.Locations.PROJECT_ALL,
                null,
                null,
                ReminderContract.Locations.SORT_ORDER_DEFAULT);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<GroceryStore> groceryStoreList = new ArrayList<GroceryStore>();
        Log.d(TAG, "In onLoadFinished");
        while (cursor.moveToNext()) {
            GroceryStore store = loadStoreFromCursor(cursor);
            groceryStoreList.add(store);
        }

        Collections.sort(groceryStoreList);
        if (!groceryStoreList.isEmpty()) {
            progressDialog.hide();
        }

        GroceryStoreListFragment groceryStoreListFragment =
                (GroceryStoreListFragment)getSupportFragmentManager().findFragmentById(R.id.stores_fragment_container);
        groceryStoreListFragment.setStores(groceryStoreList);
    }

    private GroceryStore loadStoreFromCursor(Cursor cursor) {
        String storeName = cursor.getString(cursor.getColumnIndex(ReminderContract.Locations.NAME));
        Log.d(TAG, "Loading store from cursor: " + storeName);

        Double latitude = cursor.getDouble(cursor.getColumnIndex(ReminderContract.Locations.LATITUDE));
        Double longitude = cursor.getDouble(cursor.getColumnIndex(ReminderContract.Locations.LONGITUDE));
        Location currentLocation = groceryStoreManager.getCurrentLocation();
        float[] distanceResults = new float[1];
        if (currentLocation != null) {
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), latitude, longitude, distanceResults);
        } else {
            distanceResults[0] = -1;
        }

        return new GroceryStore(storeName, distanceResults[0], latitude, longitude);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG, "In onLoaderReset");
        GroceryStoreListFragment groceryStoreListFragment =
                (GroceryStoreListFragment)getSupportFragmentManager().findFragmentById(R.id.stores_fragment_container);

        if (groceryStoreListFragment != null) {
            groceryStoreListFragment.setStores(new ArrayList<GroceryStore>());
        }
    }
}
