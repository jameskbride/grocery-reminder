package com.groceryreminder;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class ReminderListFragment extends ListFragment {

    private static final String REMINDERS_KEY = "REMINDERS_KEY";
    private List<Reminder> reminders;

    public static ReminderListFragment newInstance(List<Reminder> reminders) {
        ReminderListFragment fragment = new ReminderListFragment();
        Bundle args = new Bundle();
        args.putSerializable(REMINDERS_KEY, (java.io.Serializable) reminders);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReminderListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.reminders = (List<Reminder>)getArguments().getSerializable(REMINDERS_KEY);
        }

        // TODO: Change Adapter to display your content
        setListAdapter(new ArrayAdapter<Reminder>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, reminders));
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
