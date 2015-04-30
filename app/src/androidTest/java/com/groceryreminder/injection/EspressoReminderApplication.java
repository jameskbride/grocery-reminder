package com.groceryreminder.injection;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public class EspressoReminderApplication extends ReminderApplication {

    private AndroidModule androidModule;
    private EspressoReminderModule reminderModule;
    private EspressoRemoteResourcesModule espressoRemoteResourcesModule;

    @Override
    public void onCreate() {
        graph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<Object>();
        modules.add(getAndroidModule());
        modules.add(getEspressoRemoteResourcesModule());
        modules.add(getReminderModule());

        return modules;
    }

    public void inject(Object object) {
        graph.inject(object);
    }

    public AndroidModule getAndroidModule() {
        if (this.androidModule == null) {
            this.androidModule = new AndroidModule(this);
        }

        return this.androidModule;
    }

    public EspressoReminderModule getReminderModule() {
        if (this.reminderModule == null) {
            this.reminderModule = new EspressoReminderModule();
        }

        return this.reminderModule;
    }

    public EspressoRemoteResourcesModule getEspressoRemoteResourcesModule() {
        if (this.espressoRemoteResourcesModule == null) {
            this.espressoRemoteResourcesModule = new EspressoRemoteResourcesModule();
        }

        return this.espressoRemoteResourcesModule;
    }
}
