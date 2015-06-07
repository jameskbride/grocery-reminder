package com.groceryreminder.views.stores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.groceryreminder.R;
import com.groceryreminder.models.GroceryStore;

import java.util.List;

public class GroceryStoresRecyclerViewAdapter extends RecyclerView.Adapter<GroceryStoreListViewHolder>{

    private List<GroceryStore> groceryStores;

    public GroceryStoresRecyclerViewAdapter(List<GroceryStore> groceryStores) {
        this.groceryStores = groceryStores;
    }

    @Override
    public GroceryStoreListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.store_viewholder, parent, false);
        return new GroceryStoreListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroceryStoreListViewHolder groceryStoreListViewHolder, int position) {
        groceryStoreListViewHolder.bind(groceryStores.get(position));
    }

    @Override
    public int getItemCount() {
        return groceryStores.size();
    }

    public void setStores(List<GroceryStore> groceryStoreList) {
        this.groceryStores.clear();
        this.groceryStores.addAll(groceryStoreList);
        notifyDataSetChanged();
    }
}
