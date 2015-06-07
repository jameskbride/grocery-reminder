package com.groceryreminder.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroceryStoreTest {

    @Test
    public void givenADistanceThenTheDistanceShouldBeSet() {
        GroceryStore store = new GroceryStore("a name", 1.5, 1.0, 2.0);

        assertEquals(1.5, store.getDistance(), 0.001);
    }

    @Test
    public void givenALatitudeThenTheLatitudeShouldBeSet() {
        GroceryStore store = new GroceryStore("a name", 1.5, 1.0, 2.0);

        assertEquals(1.0, store.getLatitude(), 0.001);
    }

}
