package com.groceryreminder;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ReminderFragmentBaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ReminderApplication)getApplication()).inject(this);
    }
}
