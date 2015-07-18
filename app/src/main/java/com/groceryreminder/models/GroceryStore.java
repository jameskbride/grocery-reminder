package com.groceryreminder.models;

import java.io.Serializable;

public class GroceryStore implements Serializable {

    private String name;
    private double distance;
    private double latitude;
    private double longitude;

    public GroceryStore(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GroceryStore(String name, double distance, double latitude, double longitude) {
        this.name = name;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroceryStore store = (GroceryStore) o;

        if (Double.compare(store.distance, distance) != 0) return false;
        if (Double.compare(store.latitude, latitude) != 0) return false;
        if (Double.compare(store.longitude, longitude) != 0) return false;
        return !(name != null ? !name.equals(store.name) : store.name != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp = Double.doubleToLongBits(distance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
