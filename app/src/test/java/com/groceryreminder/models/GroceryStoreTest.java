package com.groceryreminder.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroceryStoreTest {

    @Test
    public void givenADistanceThenTheDistanceShouldBeSet() {
        GroceryStore store = new GroceryStore("a name", 1.5);

        assertEquals(1.5, store.getDistance(), 0.001);
    }

}
