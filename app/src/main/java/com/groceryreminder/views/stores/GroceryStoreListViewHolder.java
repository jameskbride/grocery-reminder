package com.groceryreminder.views.stores;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.groceryreminder.R;
import com.groceryreminder.models.GroceryStore;

public class GroceryStoreListViewHolder extends RecyclerView.ViewHolder {
    private final TextView storeNameText;

    public GroceryStoreListViewHolder(View itemView) {
        super(itemView);
        this.storeNameText = (TextView)itemView.findViewById(R.id.stores_text_view);
    }

    public void bind(GroceryStore store) {
        storeNameText.setText(store.getName());
    }
}
