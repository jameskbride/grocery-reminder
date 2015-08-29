package com.groceryreminder.injection;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

public class TestReminderApplication extends ReminderApplication {

    private TestReminderModule testReminderModule;
    private TestRemoteResourcesModule testRemoteResourcesModule;
    private TestAndroidModule testAndroidModule;

    @Override
    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<Object>();
        modules.add(getTestReminderModule());
        modules.add(getTestRemoteResourcesModule());
        modules.add(getTestAndroidModule());
        return modules;
    }

    public TestReminderModule getTestReminderModule() {
        if (testReminderModule == null) {
            this.testReminderModule = new TestReminderModule();
        }

        return testReminderModule;
    }

    public TestRemoteResourcesModule getTestRemoteResourcesModule() {
        if (testRemoteResourcesModule == null) {
            this.testRemoteResourcesModule = new TestRemoteResourcesModule();
        }

        return testRemoteResourcesModule;
    }

    public TestAndroidModule getTestAndroidModule() {
        if (testAndroidModule == null) {
            testAndroidModule = new TestAndroidModule();
        }

        return testAndroidModule;
    }
}
