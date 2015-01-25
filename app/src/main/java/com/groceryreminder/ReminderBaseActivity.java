package com.groceryreminder;

import android.app.Activity;
import android.os.Bundle;

public abstract class ReminderBaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ReminderApplication)getApplication()).inject(this);
    }

}
