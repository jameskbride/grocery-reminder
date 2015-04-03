package com.groceryreminder.views.stores;

import android.support.v4.content.CursorLoader;

import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryStoresActivityTest extends RobolectricTestBase {

    private GroceryStoresActivity activity;

    @Before
    public void setUp() {
        super.setUp();

        activity = Robolectric.buildActivity(GroceryStoresActivity.class).create().start().get();
    }

    @Test
    public void whenTheActivityIsCreatedThenTheCursorLoaderShouldBeConfigured() {
        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);

        assertNotNull(cursorLoader);
        assertEquals(ReminderContract.Locations.CONTENT_URI, cursorLoader.getUri());
        assertArrayEquals(ReminderContract.Locations.PROJECT_ALL, cursorLoader.getProjection());
        assertEquals(ReminderContract.Locations.SORT_ORDER_DEFAULT, cursorLoader.getSortOrder());
        assertNull(cursorLoader.getSelection());
        assertNull(cursorLoader.getSelectionArgs());
    }

    @Test
    public void whenTheActivityIsCreatedThenTheGroceryStoreListFragmentShouldBeCreated() {
        GroceryStoreListFragment groceryStoreListFragment =
                (GroceryStoreListFragment)activity.getSupportFragmentManager()
                        .findFragmentByTag(GroceryStoresActivity.STORE_LIST_FRAGMENT_TAG);

        assertNotNull(groceryStoreListFragment);
    }
}
