package com.groceryreminder.views.stores;

import android.database.Cursor;
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
import com.groceryreminder.models.GroceryStore;

import java.util.ArrayList;
import java.util.List;

public class GroceryStoresActivity extends ReminderFragmentBaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "StoresActivity";
    public static final String STORE_LIST_FRAGMENT_TAG = "StoreListFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocery_stores_activity);
        GroceryStoreListFragment groceryStoreListFragment = GroceryStoreListFragment.newInstance(new ArrayList<GroceryStore>());
        getSupportFragmentManager().beginTransaction().add(R.id.stores_fragment_container, groceryStoreListFragment).commit();
        getSupportLoaderManager().initLoader(0, savedInstanceState, this);
    }

    @Override
    protected boolean shouldInject() {
        return false;
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
            String storeName = cursor.getString(1);
            Log.d(TAG, "Loading store from cursor: " + storeName);
            GroceryStore store = new GroceryStore(storeName);
            groceryStoreList.add(store);
        }

        GroceryStoreListFragment groceryStoreListFragment =
                (GroceryStoreListFragment)getSupportFragmentManager().findFragmentById(R.id.stores_fragment_container);
        groceryStoreListFragment.setStores(groceryStoreList);
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
