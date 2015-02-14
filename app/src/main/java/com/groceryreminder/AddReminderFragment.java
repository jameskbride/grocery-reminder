package com.groceryreminder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddReminderFragment extends Fragment {


    private OnAddReminderListener onAddReminderListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_reminder_fragment, container, false);
        Button addReminderButton = (Button)view.findViewById(R.id.add_reminder_button);
        addReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText reminderEditText = (EditText)getActivity().findViewById(R.id.add_reminder_edit);
                String reminderText = reminderEditText.getText().toString();
                onAddReminderListener.addReminder(reminderText);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.onAddReminderListener = (OnAddReminderListener)activity;
    }

    public static AddReminderFragment newInstance() {
        AddReminderFragment fragment = new AddReminderFragment();

        return fragment;
    }

    public OnAddReminderListener getOnAddReminderListener() {
        return onAddReminderListener;
    }
}
