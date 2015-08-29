package com.groceryreminder.injection;

import java.util.List;

import dagger.ObjectGraph;

public class ReminderObjectGraph {
    protected ObjectGraph graph;

    private static ReminderObjectGraph instance;

    private ReminderObjectGraph() {
    }

    public static ReminderObjectGraph getInstance() {
        if (instance == null) {
            instance = new ReminderObjectGraph();
        }

        return instance;
    }

    public void inject(Object context) {
        graph.inject(context);
    }

    public void createObjectGraph(List<Object> modules) {
        graph = ObjectGraph.create(modules.toArray());
    }
}