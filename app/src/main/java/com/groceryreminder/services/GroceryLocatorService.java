package com.groceryreminder.services;

import android.app.IntentService;
import android.content.Intent;

import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.TypeParam;
import se.walkercrou.places.Types;

public class GroceryLocatorService extends IntentService {

    @Inject
    GooglePlacesInterface googlePlaces;

    public GroceryLocatorService(String name) {
        super(name);
        ((ReminderApplication)getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        Param[] params = new Param[] {groceryStoreType};
        googlePlaces.getPlacesByRadar(0, 0, 0, 50, params);
    }
}
