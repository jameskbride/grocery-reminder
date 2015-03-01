package com.groceryreminder.services;

import android.content.Intent;

import com.groceryreminder.RobolectricTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;
import se.walkercrou.places.TypeParam;
import se.walkercrou.places.Types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
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
    public void givenAnIntentWhenTheIntentIsHandledThenARadarSearchIsPerformed() {
        GooglePlacesInterface googlePlacesMock = getTestReminderModule().getGooglePlaces();

        groceryLocatorService.onHandleIntent(new Intent());

        verify(googlePlacesMock).getPlacesByRadar(anyDouble(), anyDouble(), anyDouble(), anyInt(), any(Param[].class));
    }

    @Test
    public void givenAnIntentWhenTheIntentIsHandledThenARadarSearchForGroceryStoresIsPerformed() {
        GooglePlacesInterface googlePlacesMock = getTestReminderModule().getGooglePlaces();

        Param groceryStoreType = Param.name(GooglePlacesInterface.STRING_TYPE).value(Types.TYPE_GROCERY_OR_SUPERMARKET);
        ArgumentCaptor<Param> paramsCaptor = ArgumentCaptor.forClass(Param.class);

        groceryLocatorService.onHandleIntent(new Intent());

        verify(googlePlacesMock).getPlacesByRadar(anyDouble(), anyDouble(), anyDouble(), anyInt(), paramsCaptor.capture());

        Param actualParams = paramsCaptor.getValue();
        assertEquals(actualParams, groceryStoreType);
    }
}
