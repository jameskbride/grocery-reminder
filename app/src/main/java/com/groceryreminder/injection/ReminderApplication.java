package com.groceryreminder.injection;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public class ReminderApplication extends Application {

    protected ObjectGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();

        graph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<Object>();
        modules.add(new AndroidModule(this));
        modules.add(new ReminderModule(this));

        return modules;
    }

    public void inject(Object object) {
        graph.inject(object);
    }
}
