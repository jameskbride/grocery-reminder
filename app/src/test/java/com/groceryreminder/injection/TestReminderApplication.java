package com.groceryreminder.injection;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public class TestReminderApplication extends ReminderApplication {

    private TestReminderModule testReminderModule;
    private TestAndroidModule testAndroidModule;

    @Override
    public void onCreate() {
        List<Object> modules = getModules();
        graph = ObjectGraph.create(modules.toArray());
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<Object>();
        modules.add(getTestReminderModule());
        modules.add(getTestAndroidModule());
        return modules;
    }

    public TestReminderModule getTestReminderModule() {
        if (testReminderModule == null) {
            this.testReminderModule = new TestReminderModule();
        }

        return testReminderModule;
    }

    public TestAndroidModule getTestAndroidModule() {
        if (testAndroidModule == null) {
            testAndroidModule = new TestAndroidModule();
        }

        return testAndroidModule;
    }
}
