package com.groceryreminder.espresso;

import android.content.pm.ActivityInfo;
import android.support.test.espresso.assertion.ViewAssertions;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.groceryreminder.R;
import com.groceryreminder.views.MainActivity;

import java.lang.String;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
public class MainActivityEspressoTest extends ActivityInstrumentationTestCase2<MainActivity>{

    private static final String ARBITRARY_REMINDER = "test";
    private MainActivity mainActivity;

    public MainActivityEspressoTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
    }

    public void testWhenAReminderIsAddedThenItIsDisplayedInTheList() {
        addArbitraryReminder();
        onView(withText("test")).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testGivenAReminderHasBeenAddedWhenTheDeviceIsRotatedItIsStillDisplayed() {
        addArbitraryReminder();
        mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.matches(isDisplayed()));
    }

    public void testGivenAReminderHashBeenAddedWhenItIsSwipedLeftThenItIsDismissed() {
        addArbitraryReminder();
        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.matches(isDisplayed()));

        onView(withText(ARBITRARY_REMINDER)).perform(swipeLeft());

        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.doesNotExist());
    }

    public void testGivenAReminderHashBeenAddedWhenItIsSwipedRightThenItIsDismissed() {
        addArbitraryReminder();
        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.matches(isDisplayed()));

        onView(withText(ARBITRARY_REMINDER)).perform(swipeRight());

        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.doesNotExist());
    }

    private void addArbitraryReminder() {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.add_reminder_edit)).perform(typeText(ARBITRARY_REMINDER));
        onView(withId(R.id.add_reminder_button)).perform(click());
    }
}
