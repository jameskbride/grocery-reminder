package com.groceryreminder.views.stores;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groceryreminder.R;
import com.groceryreminder.models.GroceryStore;

import java.util.List;

public class GroceryStoreListFragment extends Fragment {

    public static final String STORES_KEY = "grocery_stores";
    private static final String TAG = "StoreListFragment";
    private List<GroceryStore> stores;

    public static GroceryStoreListFragment newInstance(List<GroceryStore> stores) {
        GroceryStoreListFragment fragment = new GroceryStoreListFragment();
        Bundle args = new Bundle();
        args.putSerializable(STORES_KEY, (java.io.Serializable) stores);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.stores = (List<GroceryStore>)getArguments().getSerializable(STORES_KEY);
        }
        Log.d(TAG, "In onCreateView");
        View root = inflater.inflate(R.layout.grocery_stores_list_fragment, container, false);
        wireListView(root);

        return root;
    }

    private void wireListView(View root) {
        RecyclerView list = (RecyclerView)root.findViewById(R.id.stores_recycler_view);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(new GroceryStoresRecyclerViewAdapter(stores));
    }
}
