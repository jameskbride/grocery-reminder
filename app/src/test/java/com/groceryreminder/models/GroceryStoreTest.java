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

    @Test
    public void givenStoreAIsCloserWhenTwoStoresAreComparedThenStoreAIsGreater() {
        GroceryStore store1 = new GroceryStore("test", 1.0, 1.0, 1.0);
        GroceryStore store2 = new GroceryStore("test1", 0.0, 1.0, 1.0);

        assertEquals(1, store1.compareTo(store2));
    }

    @Test
    public void givenStoreBIsCloserWhenTwoStoresAreComparedThenStoreAIsLesser() {
        GroceryStore store1 = new GroceryStore("test", 0.0, 1.0, 1.0);
        GroceryStore store2 = new GroceryStore("test1", 1.0, 1.0, 1.0);

        assertEquals(-1, store1.compareTo(store2));
    }

    @Test
    public void givenBothStoresAreEquallyCloseWhenTheyAreComparedThenStoreAEqualsStoreB() {
        GroceryStore store1 = new GroceryStore("test", 0.0, 1.0, 1.0);
        GroceryStore store2 = new GroceryStore("test1", 0.0, 1.0, 1.0);

        assertEquals(0, store1.compareTo(store2));
    }

    @Test
    public void givenTheSecondStoreIsNullWhenTwoStoresAreComparedThenStoreAIsGreater() {
        GroceryStore store1 = new GroceryStore("test", 0.0, 1.0, 1.0);

        assertEquals(1, store1.compareTo(null));
    }

}
