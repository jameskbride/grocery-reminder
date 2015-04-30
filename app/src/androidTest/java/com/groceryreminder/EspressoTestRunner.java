package com.groceryreminder;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import com.groceryreminder.injection.EspressoReminderApplication;

public class EspressoTestRunner extends AndroidJUnitRunner {

    public EspressoTestRunner() {
        super();
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, EspressoReminderApplication.class.getName(), context);
    }
}
