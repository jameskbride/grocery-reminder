package com.groceryreminder.models;

import com.groceryreminder.models.Reminder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReminderTest {

    @Test
    public void givenAReminderWithTextWhenToStringIsCalledThenTheTextIsReturned() {
        Reminder reminder = new Reminder("test");

        assertEquals("test", reminder.toString());
    }
}
