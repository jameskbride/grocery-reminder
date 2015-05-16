package com.groceryreminder.views.stores;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.groceryreminder.BuildConfig;
import com.groceryreminder.R;
import com.groceryreminder.RobolectricTestBase;
import com.groceryreminder.models.GroceryStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class GroceryStoreListViewHolderTest extends RobolectricTestBase {

    private static final String ARBITRARY_STORE_NAME = "test";
    private FragmentActivity activity;
    private List<GroceryStore> stores;

    @Before
    public void setUp() {
        super.setUp();

        activity = Robolectric.buildActivity(FragmentActivity.class).create().start().get();
        activity.setContentView(R.layout.grocery_stores_activity);
        //TODO This should be removed once the activity is completed test-drove with CursorLoader.
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.stores_fragment_container, GroceryStoreListFragment.newInstance(stores), "tag")
                .commit();
    }

    @Test
    public void givenStoresListViewWhenAStoreIsBoundThenTheStoreNameTextIsSet() {
        RecyclerView recyclerView = getRecyclerView();
        View view = LayoutInflater.from(recyclerView.getContext()).inflate(R.layout.store_viewholder, recyclerView, false);
        GroceryStore store = new GroceryStore(ARBITRARY_STORE_NAME);

        GroceryStoreListViewHolder viewHolder = new GroceryStoreListViewHolder(view);
        viewHolder.bind(store);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.stores_text_view);
        assertEquals("test", reminderText.getText());
    }

    private RecyclerView getRecyclerView() {
        RecyclerView viewGroup = (RecyclerView)activity.findViewById(R.id.stores_recycler_view);
        viewGroup.setLayoutManager(new LinearLayoutManager(activity));

        return viewGroup;
    }

}
