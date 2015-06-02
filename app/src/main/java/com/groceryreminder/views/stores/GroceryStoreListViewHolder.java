package com.groceryreminder.views.stores;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.groceryreminder.R;
import com.groceryreminder.models.GroceryStore;

import java.text.DecimalFormat;

public class GroceryStoreListViewHolder extends RecyclerView.ViewHolder {
    private static final double MILES_TO_METERS_MULTIPLE = 0.00062137;
    private final TextView storeNameText;
    private final TextView storeDistanceText;

    public GroceryStoreListViewHolder(View itemView) {
        super(itemView);
        this.storeNameText = (TextView)itemView.findViewById(R.id.stores_text_view);
        this.storeDistanceText = (TextView)itemView.findViewById(R.id.store_distance);
    }

    public void bind(GroceryStore store) {
        storeNameText.setText(store.getName());
        double miles = store.getDistance() * MILES_TO_METERS_MULTIPLE;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        storeDistanceText.setText(decimalFormat.format(miles) + " mi");
    }
}
