package com.groceryreminder.injection.views;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.groceryreminder.injection.ReminderApplication;

public class ReminderFragmentBaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ReminderApplication)getApplication()).inject(this);
    }
}
