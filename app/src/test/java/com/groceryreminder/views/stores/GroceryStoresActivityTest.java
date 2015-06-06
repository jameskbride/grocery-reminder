package com.groceryreminder.views.stores;

import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.data.ReminderContract;
import com.groceryreminder.domain.GroceryReminderConstants;
import com.groceryreminder.domain.GroceryStoreManagerInterface;
import com.groceryreminder.models.GroceryStore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowCursorWrapper;
import org.robolectric.shadows.ShadowLocation;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoresActivityTest extends RobolectricTestBase {

    private ActivityController<GroceryStoresActivity> activityController;
    private GroceryStoresActivity activity;

    @Before
    public void setUp() {
        super.setUp();

        activityController = Robolectric.buildActivity(GroceryStoresActivity.class);
        activity = activityController.create().start().get();
        ShadowLocation.setDistanceBetween(new float[]{(float) GroceryReminderConstants.LOCATION_SEARCH_RADIUS_METERS});
    }

    @After
    public void tearDown() {
        activityController.pause().stop().destroy();
    }

    private void loadGroceryStoreListFragment() {
        ShadowCursorWrapper wrapper = createCursorWithDefaultReminder();

        GroceryStoreManagerInterface groceryStoreManagerMock = getTestReminderModule().getGroceryStoreManager();
        when(groceryStoreManagerMock.getCurrentLocation()).thenReturn(new Location(LocationManager.PASSIVE_PROVIDER));

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoadFinished(cursorLoader, wrapper);
    }

    private GroceryStoreListFragment getGroceryStoreListFragment() {
        return (GroceryStoreListFragment)activity.getSupportFragmentManager()
                .findFragmentById(R.id.stores_fragment_container);
    }

    private ShadowCursorWrapper createCursorWithDefaultReminder() {
        Cursor mockCursor = mock(Cursor.class);
        when(mockCursor.moveToNext()).thenReturn(true).thenReturn(false);
        when(mockCursor.getString(anyInt())).thenReturn("test");
        when(mockCursor.getDouble(3)).thenReturn(0.0);
        when(mockCursor.getDouble(4)).thenReturn(1.0);
        ShadowCursorWrapper wrapper = new ShadowCursorWrapper();
        wrapper.__constructor__(mockCursor);
        return wrapper;
    }

    @Test
    public void whenTheActivityIsCreatedThenTheTitleShouldBeSet() {
        assertEquals(RuntimeEnvironment.application.getString(R.string.store_list_title), activity.getTitle());
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
        ShadowCursorWrapper wrapper = createCursorWithDefaultReminder();

        GroceryStoreManagerInterface groceryStoreManagerMock = getTestReminderModule().getGroceryStoreManager();
        when(groceryStoreManagerMock.getCurrentLocation()).thenReturn(new Location(LocationManager.PASSIVE_PROVIDER));

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoadFinished(cursorLoader, wrapper);
        GroceryStoreListFragment groceryStoreListFragment = getGroceryStoreListFragment();
        assertNotNull(groceryStoreListFragment);

        RecyclerView listView = getRecyclerView(groceryStoreListFragment, R.id.stores_recycler_view);
        TextView storeNameText = (TextView)listView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.stores_text_view);
        assertEquals(View.VISIBLE, storeNameText.getVisibility());
        assertEquals("test", storeNameText.getText());
    }

    @Test
    public void whenTheCursorLoaderIsFinishedThenTheDistanceFromCurrentLocationIsSetOnTheStoreList() {
        ShadowCursorWrapper wrapper = createCursorWithDefaultReminder();

        GroceryStoreManagerInterface groceryStoreManagerMock = getTestReminderModule().getGroceryStoreManager();
        when(groceryStoreManagerMock.getCurrentLocation()).thenReturn(new Location(LocationManager.PASSIVE_PROVIDER));

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoadFinished(cursorLoader, wrapper);
        GroceryStoreListFragment groceryStoreListFragment = getGroceryStoreListFragment();
        assertNotNull(groceryStoreListFragment);

        RecyclerView listView = getRecyclerView(groceryStoreListFragment, R.id.stores_recycler_view);
        TextView distanceText = (TextView)listView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.store_distance);
        assertEquals("5.0 mi", distanceText.getText());

        verify(groceryStoreManagerMock).getCurrentLocation();
    }

    @Test
    public void whenTheCursorIsResetThenTheGroceryStoreListFragmentShouldContainNoStores() {
        loadGroceryStoreListFragment();

        GroceryStoreManagerInterface groceryStoreManagerMock = getTestReminderModule().getGroceryStoreManager();
        when(groceryStoreManagerMock.getCurrentLocation()).thenReturn(new Location(LocationManager.PASSIVE_PROVIDER));

        CursorLoader cursorLoader = (CursorLoader)activity.onCreateLoader(0, null);
        activity.onLoaderReset(cursorLoader);

        GroceryStoreListFragment groceryStoreListFragment = getGroceryStoreListFragment();
        assertNotNull(groceryStoreListFragment);

        RecyclerView listView = getRecyclerView(groceryStoreListFragment, R.id.stores_recycler_view);
        assertEquals(0, listView.getAdapter().getItemCount());
    }
}
