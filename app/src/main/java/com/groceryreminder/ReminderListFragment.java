package com.groceryreminder;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.List;

public class ReminderListFragment extends ListFragment {

    private List<Reminder> reminders;

    public static ReminderListFragment newInstance(List<Reminder> reminders) {
        ReminderListFragment fragment = new ReminderListFragment();
        Bundle args = new Bundle();
        args.putSerializable("REMINDERS_KEY", (java.io.Serializable) reminders);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.reminders = (List<Reminder>)getArguments().getSerializable("REMINDERS_KEY");
        }

        // TODO: Change Adapter to display your content
        setListAdapter(new ArrayAdapter<Reminder>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, reminders));
    }


}
