package com.groceryreminder.injection.views;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.groceryreminder.injection.ReminderApplication;

public class ReminderFragmentBaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (shouldInject()) {
            ((ReminderApplication)getApplication()).inject(this);
        }
    }

    protected boolean shouldInject() {
        return true;
    }
}
