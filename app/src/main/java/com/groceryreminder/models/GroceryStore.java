package com.groceryreminder.models;

import java.io.Serializable;

public class GroceryStore implements Serializable {
    private String name;

    public GroceryStore(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroceryStore store = (GroceryStore) o;

        if (name != null ? !name.equals(store.name) : store.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
