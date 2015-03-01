package com.groceryreminder.services;

import android.content.Intent;

import com.groceryreminder.RobolectricTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Place;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GroceryLocatorServiceTest extends RobolectricTestBase {

    private static final String ARBITRARY_SERVICE_NAME = "test";
    private GroceryLocatorService groceryLocatorService;

    @Before
    public void setUp() {
        groceryLocatorService = new GroceryLocatorService(ARBITRARY_SERVICE_NAME);
        groceryLocatorService.onCreate();
    }

    @Test
    public void testGivenAnIntentToFindLocationsWhenTheIntentIsHandledThenARadarSearchIsPerformed() {
        GooglePlacesInterface googlePlacesMock = getTestReminderModule().getGooglePlaces();

        groceryLocatorService.onHandleIntent(new Intent());

        verify(googlePlacesMock).getPlacesByRadar(anyDouble(), anyDouble(), anyDouble(), anyInt());
    }
}
