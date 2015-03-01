package com.groceryreminder.services;

import android.app.IntentService;
import android.content.Intent;

import com.groceryreminder.injection.ReminderApplication;

import javax.inject.Inject;

import se.walkercrou.places.GooglePlacesInterface;

public class GroceryLocatorService extends IntentService {

    @Inject
    GooglePlacesInterface googlePlaces;

    public GroceryLocatorService(String name) {
        super(name);
        ((ReminderApplication)getApplication()).inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        googlePlaces.getPlacesByRadar(0, 0, 0, 50);
    }
}
