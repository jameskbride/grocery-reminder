package com.groceryreminder;

import dagger.Module;

@Module(
    includes = {
        AndroidModule.class
    },
    injects = MainActivity.class,
    complete = false
)
public class DaggerModule {
}
