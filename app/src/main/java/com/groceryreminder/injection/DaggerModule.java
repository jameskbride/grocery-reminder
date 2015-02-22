package com.groceryreminder.injection;

import com.groceryreminder.views.MainActivity;

import dagger.Module;

@Module(
    includes = {
        AndroidModule.class
    },
    injects = {
      MainActivity.class
    },
    complete = false
)
public class DaggerModule {
}
