package com.groceryreminder;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.List;

public class ReminderListFragment extends ListFragment {

    private static final String REMINDERS_KEY = "REMINDERS_KEY";
    private List<Reminder> reminders;
    private OnAddReminderRequestListener onAddReminderRequestListener;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.reminders = (List<Reminder>)getArguments().getSerializable(REMINDERS_KEY);
        }
        Log.d("ReminderListFragment","In onCreateView");
        // TODO: Change Adapter to display your content
        View root = inflater.inflate(R.layout.reminder_list_fragment, container, false);
        ListView list = wireListView(root);
        wireAddReminderRequestButton(root, list);

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("ReminderListFragment", "In onAttach");
        onAddReminderRequestListener = (OnAddReminderRequestListener)activity;
    }

    private ListView wireListView(View root) {
        ListView list = (ListView)root.findViewById(android.R.id.list);
        list.setAdapter(new ArrayAdapter<Reminder>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, reminders));
        return list;
    }

    private void wireAddReminderRequestButton(View root, ListView list) {
        FloatingActionButton fab = (FloatingActionButton)root.findViewById(R.id.fab);
        fab.attachToListView(list);
        fab.setOnClickListener(new RequestAddReminderClickListener(this.onAddReminderRequestListener));
    }

    public OnAddReminderRequestListener getOnAddReminderRequestListener() {
        return onAddReminderRequestListener;
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
    }
}
