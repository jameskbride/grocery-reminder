package com.groceryreminder.views.stores;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.models.GroceryStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowCursorWrapper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        GroceryStoreListFragment groceryStoreListFragment = getGroceryStoreListFragment();

        assertNotNull(groceryStoreListFragment);
    }

    @Test
    public void whenTheCursorLoaderIsFinishedThenTheGroceryStoreListFragmentShouldBeUpdatedWithStores() {
        GroceryStore store = new GroceryStore("test");
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mockCursor.getString(1)).thenReturn(store.getName());
        ShadowCursorWrapper wrapper = new ShadowCursorWrapper();
        wrapper.__constructor__(mockCursor);

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoadFinished(cursorLoader, wrapper);
        GroceryStoreListFragment groceryStoreListFragment = getGroceryStoreListFragment();
        assertNotNull(groceryStoreListFragment);

        RecyclerView listView = getRecyclerView(groceryStoreListFragment, R.id.stores_recycler_view);
        TextView storeNameText = (TextView)listView.findViewHolderForPosition(0).itemView.findViewById(R.id.stores_text_view);
        assertEquals(View.VISIBLE, storeNameText.getVisibility());
        assertEquals(store.getName(), storeNameText.getText());
    }

    @Test
    public void whenTheCursorIsResetThenTheGroceryStoreListFragmentShouldContainNoStores() {
        loadGroceryStoreListFragment();

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoaderReset(cursorLoader);

        GroceryStoreListFragment groceryStoreListFragment = getGroceryStoreListFragment();
        assertNotNull(groceryStoreListFragment);

        RecyclerView listView = getRecyclerView(groceryStoreListFragment, R.id.stores_recycler_view);
        assertEquals(0, listView.getAdapter().getItemCount());
    }

    private void loadGroceryStoreListFragment() {
        GroceryStore store = new GroceryStore("test");
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mockCursor.getString(1)).thenReturn(store.getName());
        ShadowCursorWrapper wrapper = new ShadowCursorWrapper();
        wrapper.__constructor__(mockCursor);

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoadFinished(cursorLoader, wrapper);
    }

    private GroceryStoreListFragment getGroceryStoreListFragment() {
        return (GroceryStoreListFragment)activity.getSupportFragmentManager()
                .findFragmentById(R.id.stores_fragment_container);
    }
}
