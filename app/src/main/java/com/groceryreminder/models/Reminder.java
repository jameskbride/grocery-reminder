package com.groceryreminder.models;

import java.io.Serializable;

public class Reminder implements Serializable {

    private String text;
    public Reminder(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
