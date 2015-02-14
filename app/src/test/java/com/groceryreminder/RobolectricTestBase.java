package com.groceryreminder;

import org.robolectric.shadows.ShadowLog;

public class RobolectricTestBase {

    public void setUp() {
        ShadowLog.stream = System.out;
    }
}
