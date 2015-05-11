package com.groceryreminder.models;

import java.io.Serializable;

public class Reminder implements Serializable {

    private String text;
    private long id;

    public Reminder(long id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reminder reminder = (Reminder) o;

        if (id != reminder.id) return false;
        return !(text != null ? !text.equals(reminder.text) : reminder.text != null);

    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return text;
    }
}
