package com.groceryreminder.views.stores;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.models.GroceryStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryStoreListFragmentTest extends RobolectricTestBase {

    private GroceryStoresActivity activity;

    @Before
    public void setUp() {
        super.setUp();
        activity = Robolectric.buildActivity(GroceryStoresActivity.class).create().start().visible().get();
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
        assertEquals(reminderText.getText(), "test");
    }
}
