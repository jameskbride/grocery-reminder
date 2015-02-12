package com.groceryreminder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddReminderFragment extends Fragment {


    public OnAddReminderRequestListener getOnAddReminderRequestListener() {
        return onAddReminderRequestListener;
    }

    OnAddReminderRequestListener onAddReminderRequestListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onAddReminderRequestListener = (OnAddReminderRequestListener)activity;

    }

    public static AddReminderFragment newInstance() {
        AddReminderFragment fragment = new AddReminderFragment();

        return fragment;
    }
}
