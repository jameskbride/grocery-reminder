package com.groceryreminder.testUtils;

import android.content.ContentValues;

import com.groceryreminder.data.ReminderContract;

public class ReminderValuesBuilder {

    private ContentValues locationValues;

    public ReminderValuesBuilder() {
        this.locationValues = new ContentValues();
    }

    public ReminderValuesBuilder createDefaultReminderValues() {
        locationValues.put(ReminderContract.Reminders.DESCRIPTION, ReminderContract.Reminders.DESCRIPTION);

        return this;
    }

    public ReminderValuesBuilder withDescription(String description) {
        locationValues.put(ReminderContract.Reminders.DESCRIPTION, description);

        return this;
    }

    public ContentValues build() {
        return locationValues;
    }
}
