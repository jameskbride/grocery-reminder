package com.groceryreminder.views.stores;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.models.GroceryStore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoreListFragmentTest extends RobolectricTestBase {

    private ActivityController<GroceryStoresActivity> activityController;
    private GroceryStoresActivity activity;

    @Before
    public void setUp() {
        super.setUp();
        activityController = Robolectric.buildActivity(GroceryStoresActivity.class);
        activity = activityController.create().start().visible().get();
    }

    @After
    public void tearDown() {
        activityController.pause().stop().destroy();
    }

    @Test
    public void givenGroceryStoresWhenTheFragmentIsCreatedThenTheViewShouldBePopulated() {
        List<GroceryStore> stores = new ArrayList<GroceryStore>();
        GroceryStore groceryStore = new GroceryStore("test");
        stores.add(groceryStore);

        GroceryStoreListFragment groceryStoreListFragment = GroceryStoreListFragment.newInstance(stores);
        startFragment(activity, groceryStoreListFragment);

        RecyclerView groceryStoreRecyclerView = getRecyclerView(groceryStoreListFragment, R.id.stores_recycler_view);
        GroceryStoreListViewHolder groceryStoreListViewHolder = (GroceryStoreListViewHolder)groceryStoreRecyclerView.findViewHolderForPosition(0);

        TextView reminderText = (TextView)groceryStoreListViewHolder.itemView.findViewById(R.id.stores_text_view);
        assertEquals(groceryStore.getName(), reminderText.getText());
    }

    @Test
    public void givenGroceryStoresWhenFragmentSetsStoresThenTheViewShouldBeUpdated() {
        List<GroceryStore> stores = new ArrayList<GroceryStore>();
        GroceryStore groceryStore = new GroceryStore("test");
        stores.add(groceryStore);

        GroceryStoreListFragment groceryStoreListFragment = GroceryStoreListFragment.newInstance(stores);
        startFragment(activity, groceryStoreListFragment);

        List<GroceryStore> updatedStores = new ArrayList<GroceryStore>();
        GroceryStore updatedStore = new GroceryStore("update");
        updatedStores.add(updatedStore);

        groceryStoreListFragment.setStores(updatedStores);

        RecyclerView groceryStoreRecyclerView = getRecyclerView(groceryStoreListFragment, R.id.stores_recycler_view);
        GroceryStoreListViewHolder groceryStoreListViewHolder = (GroceryStoreListViewHolder)groceryStoreRecyclerView.findViewHolderForPosition(0);

        TextView reminderText = (TextView)groceryStoreListViewHolder.itemView.findViewById(R.id.stores_text_view);
        assertEquals(View.VISIBLE, reminderText.getVisibility());
        assertEquals(updatedStore.getName(), reminderText.getText());
    }
}
