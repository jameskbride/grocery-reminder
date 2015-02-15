package com.groceryreminder.injection.views;

import android.app.Activity;
import android.os.Bundle;

import com.groceryreminder.injection.ReminderApplication;

public abstract class ReminderBaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ReminderApplication)getApplication()).inject(this);
    }

}
