package com.groceryreminder;

import java.io.Serializable;

public class Reminder implements Serializable {

    private String text;
    public Reminder(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
