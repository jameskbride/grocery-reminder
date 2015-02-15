package com.groceryreminder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddReminderFragment extends Fragment {


    private OnAddReminderListener onAddReminderListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_reminder_fragment, container, false);
        wireAddReminderButton(view);
        wireReminderText(view);

        return view;
    }

    private void wireReminderText(final View view) {
        EditText reminderText = (EditText)view.findViewById(R.id.add_reminder_edit);
        reminderText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button addReminderButton = (Button)view.findViewById(R.id.add_reminder_button);
                boolean enableAddReminderButton = s.length() > 0 ? true : false;
                addReminderButton.setEnabled(enableAddReminderButton);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void wireAddReminderButton(View view) {
        Button addReminderButton = (Button)view.findViewById(R.id.add_reminder_button);
        addReminderButton.setEnabled(false);
        addReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText reminderEditText = (EditText)getActivity().findViewById(R.id.add_reminder_edit);
                String reminderText = reminderEditText.getText().toString();
                onAddReminderListener.addReminder(reminderText);
            }
        });
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
