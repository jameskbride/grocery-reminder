package com.groceryreminder.views.stores;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryStoresRecyclerViewAdapterTest extends RobolectricTestBase {

    private static final String ARBITRARY_STORE_NAME = "test";
    private FragmentActivity activity;
    private List<GroceryStore> stores;

    @Before
    public void setUp() {
        this.activity = Robolectric.buildActivity(FragmentActivity.class).create().start().get();
        this.stores = new ArrayList<GroceryStore>();
        activity.setContentView(R.layout.grocery_stores_activity);
        //TODO This should be removed once the activity is completed test-drove with CursorLoader.
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.stores_fragment_container, GroceryStoreListFragment.newInstance(stores), "tag")
                .commit();
    }

    @Test
    public void whenTheAdapterIsCreatedWithRemindersThenTheItemCountIsSet() {
        GroceryStore store = new GroceryStore(ARBITRARY_STORE_NAME);
        stores.add(store);
        GroceryStoresRecyclerViewAdapter adapter = createAdapter(stores);

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void whenTheStoresAreSetThenObserversAreNotified() {
        GroceryStore store = new GroceryStore(ARBITRARY_STORE_NAME);
        stores.add(store);
        GroceryStoresRecyclerViewAdapter adapter = createAdapter(stores);

        List<GroceryStore> updatedStores = new ArrayList<GroceryStore>();
        GroceryStore updatedStore1 = new GroceryStore(ARBITRARY_STORE_NAME + 1);
        updatedStores.add(updatedStore1);

        GroceryStoresRecyclerViewAdapter adapterSpy = spy(adapter);
        adapterSpy.setStores(updatedStores);

        verify(adapterSpy).notifyDataSetChanged();
    }

    @Test
    public void whenTheStoresAreSetThenTheItemCountIsUpdated() {
        GroceryStore store = new GroceryStore(ARBITRARY_STORE_NAME);
        stores.add(store);
        GroceryStoresRecyclerViewAdapter adapter = createAdapter(stores);

        List<GroceryStore> updatedStores = new ArrayList<GroceryStore>();
        GroceryStore updatedStore1 = new GroceryStore(ARBITRARY_STORE_NAME + 1);
        GroceryStore updatedStore2 = new GroceryStore(ARBITRARY_STORE_NAME + 2);
        updatedStores.add(updatedStore1);
        updatedStores.add(updatedStore2);

        adapter.setStores(updatedStores);

        assertEquals(updatedStores.size(), adapter.getItemCount());
    }

    @Test
    public void whenTheViewHolderIsCreatedThenTheGroceryStoreListViewHolderIsNotNull() {
        RecyclerView viewGroup = getRecyclerView();
        GroceryStoresRecyclerViewAdapter adapter = new GroceryStoresRecyclerViewAdapter(stores);

        GroceryStoreListViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, -1);

        assertNotNull(viewHolder);
    }

    @Test
    public void givenAStoreWhenTheViewHolderIsBoundThenTheTextViewIsSet() {
        GroceryStore store = new GroceryStore(ARBITRARY_STORE_NAME);
        stores.add(store);
        GroceryStoresRecyclerViewAdapter adapter = createAdapter(stores);
        RecyclerView recyclerView = getRecyclerView();
        GroceryStoreListViewHolder viewHolder = adapter.onCreateViewHolder(recyclerView, -1);

        adapter.onBindViewHolder(viewHolder, 0);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.stores_text_view);
        assertEquals(reminderText.getText(), ARBITRARY_STORE_NAME);
    }

    @Test
    public void givenMultipleStoresWhenViewHolderIsBoundWithAnArbitraryPositionThenTheTextViewIsSet() {
        GroceryStore store = new GroceryStore(ARBITRARY_STORE_NAME);
        stores.add(store);

        String expectedStore = ARBITRARY_STORE_NAME + 1;
        GroceryStore secondStore = new GroceryStore(expectedStore);
        stores.add(secondStore);

        GroceryStoresRecyclerViewAdapter adapter = new GroceryStoresRecyclerViewAdapter(stores);
        RecyclerView recyclerView = getRecyclerView();
        GroceryStoreListViewHolder viewHolder = adapter.onCreateViewHolder(recyclerView, -1);
        adapter.onBindViewHolder(viewHolder, 1);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.stores_text_view);
        assertEquals(reminderText.getText(), ARBITRARY_STORE_NAME + 1);
    }

    private GroceryStoresRecyclerViewAdapter createAdapter(List<GroceryStore> groceryStores) {
        return new GroceryStoresRecyclerViewAdapter(groceryStores);
    }

    private RecyclerView getRecyclerView() {
        RecyclerView viewGroup = (RecyclerView)activity.findViewById(R.id.stores_recycler_view);
        viewGroup.setLayoutManager(new LinearLayoutManager(activity));

        return viewGroup;
    }
}
