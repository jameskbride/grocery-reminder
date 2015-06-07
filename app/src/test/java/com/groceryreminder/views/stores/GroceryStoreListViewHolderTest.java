package com.groceryreminder.views.stores;

import android.content.Context;
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
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.stores_fragment_container, GroceryStoreListFragment.newInstance(stores), "tag")
                .commit();
    }

    private RecyclerView getRecyclerView() {
        RecyclerView viewGroup = (RecyclerView)activity.findViewById(R.id.stores_recycler_view);
        viewGroup.setLayoutManager(new LinearLayoutManager(activity));

        return viewGroup;
    }

    @Test
    public void givenStoresListViewWhenAStoreIsBoundThenTheStoreNameTextIsSet() {
        RecyclerView recyclerView = getRecyclerView();
        Context context = recyclerView.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.store_viewholder, recyclerView, false);
        GroceryStore store = new GroceryStore(ARBITRARY_STORE_NAME, 0.0, 0.0, 0.0);

        GroceryStoreListViewHolder viewHolder = new GroceryStoreListViewHolder(view, context);
        viewHolder.bind(store);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.stores_text_view);
        assertEquals("test", reminderText.getText());
    }

    @Test
    public void givenStoresListViewWhenAStoreIsBoundThenTheStoreDistanceTextIsSet() {
        RecyclerView recyclerView = getRecyclerView();
        Context context = recyclerView.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.store_viewholder, recyclerView, false);
        GroceryStore store = new GroceryStore(ARBITRARY_STORE_NAME, 2414.02, 0.0, 0.0);

        GroceryStoreListViewHolder viewHolder = new GroceryStoreListViewHolder(view, context);
        viewHolder.bind(store);

        TextView reminderText = (TextView)viewHolder.itemView.findViewById(R.id.store_distance);
        assertEquals("1.5 mi", reminderText.getText());
    }

    @Test
    public void givenAStoreIsBoundWhenTheStoreViewHolderIsClickedThenTheMapApplicationIsLaunched() {
        RecyclerView recyclerView = getRecyclerView();
        Context context = recyclerView.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.store_viewholder, recyclerView, false);
        double latitude = 0.0;
        double longitude = 1.0;
        GroceryStore store = new GroceryStore(ARBITRARY_STORE_NAME, 2414.02, latitude, longitude);

        GroceryStoreListViewHolder viewHolder = new GroceryStoreListViewHolder(view, context);
        viewHolder.bind(store);

        viewHolder.onClick(view);

        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        ShadowIntent shadowIntent = Shadows.shadowOf(shadowActivity.peekNextStartedActivity());
        assertTrue(shadowIntent.getData().toString().contains("geo:" + latitude + "," + longitude));
    }
}
