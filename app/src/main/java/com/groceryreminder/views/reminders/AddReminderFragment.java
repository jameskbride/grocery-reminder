package com.groceryreminder.views.reminders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.groceryreminder.R;

public class AddReminderFragment extends Fragment {


    private OnReminderDataChangeListener onReminderDataChangeListener;

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
                boolean enableAddReminderButton = s.toString().trim().length() > 0 ? true : false;
                addReminderButton.setEnabled(enableAddReminderButton);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void wireAddReminderButton(final View view) {
        Button addReminderButton = (Button)view.findViewById(R.id.add_reminder_button);
        addReminderButton.setEnabled(false);
        addReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText reminderEditText = (EditText)view.findViewById(R.id.add_reminder_edit);
                Log.d("AddReminderFragment", "Edit text is null: " + (reminderEditText == null ? true : false));
                Log.d("AddReminderFragment", "Edit text is: " + reminderEditText.getText());
                String reminderText = reminderEditText.getText().toString();
                reminderEditText.setText("");
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(reminderEditText.getWindowToken(), 0);
                onReminderDataChangeListener.addReminder(reminderText);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.onReminderDataChangeListener = (OnReminderDataChangeListener)activity;
    }

    public static AddReminderFragment newInstance() {
        AddReminderFragment fragment = new AddReminderFragment();

        return fragment;
    }

    public OnReminderDataChangeListener getOnReminderDataChangeListener() {
        return onReminderDataChangeListener;
    }
}
