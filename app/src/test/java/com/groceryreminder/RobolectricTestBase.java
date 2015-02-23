package com.groceryreminder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;

import org.robolectric.shadows.ShadowLog;

public class RobolectricTestBase {

    public void setUp() {
        ShadowLog.stream = System.out;
    }

    public void startFragment(FragmentActivity parentActivity, Fragment fragment) {
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();
    }

    public void performRobolectricMeasureAndLayoutHack(RecyclerView reminderRecyclerView) {
        reminderRecyclerView.measure(0, 0);
        reminderRecyclerView.layout(0, 0, 100, 10000);
    }
}
