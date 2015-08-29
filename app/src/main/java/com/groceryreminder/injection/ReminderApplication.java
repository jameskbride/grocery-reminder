package com.groceryreminder.injection;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public class ReminderApplication extends Application {

    protected ReminderObjectGraph reminderObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        reminderObjectGraph = ReminderObjectGraph.getInstance();
        reminderObjectGraph.createObjectGraph(getModules());
    }

    public List<Object> getModules() {
        List<Object> modules = new ArrayList<Object>();
        modules.add(new AndroidModule(this));
        modules.add(new RemoteResourcesModule(this));
        modules.add(new ReminderModule());

        return modules;
    }

    public void inject(Object object) {
        reminderObjectGraph.inject(object);
    }
}
