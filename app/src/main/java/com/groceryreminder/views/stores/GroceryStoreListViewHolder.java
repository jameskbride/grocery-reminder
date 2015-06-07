package com.groceryreminder.views.stores;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.groceryreminder.R;
import com.groceryreminder.models.GroceryStore;

import java.text.DecimalFormat;

public class GroceryStoreListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private static final double MILES_TO_METERS_MULTIPLE = 0.00062137;
    private final TextView storeNameText;
    private final TextView storeDistanceText;
    private final Context context;
    private GroceryStore store;

    public GroceryStoreListViewHolder(View itemView, Context context) {
        super(itemView);
        this.storeNameText = (TextView)itemView.findViewById(R.id.stores_text_view);
        this.storeDistanceText = (TextView)itemView.findViewById(R.id.store_distance);
        this.context = context;
    }

    public void bind(GroceryStore store) {
        this.store = store;
        storeNameText.setText(store.getName());
        double miles = store.getDistance() * MILES_TO_METERS_MULTIPLE;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        storeDistanceText.setText(decimalFormat.format(miles) + " mi");
    }

    @Override
    public void onClick(View v) {
        Uri geoUri = Uri.parse("geo:" + store.getLatitude() + "," + store.getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        context.startActivity(mapIntent);
    }
}
