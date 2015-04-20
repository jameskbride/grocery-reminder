package com.groceryreminder.espresso;

import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.groceryreminder.R;
import com.groceryreminder.injection.EspressoReminderApplication;
import com.groceryreminder.views.reminders.RemindersActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import se.walkercrou.places.GooglePlacesInterface;
import se.walkercrou.places.Param;
import se.walkercrou.places.Place;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RemindersActivityTest extends ActivityInstrumentationTestCase2<RemindersActivity>{

    private static final String ARBITRARY_REMINDER = "test";
    private RemindersActivity remindersActivity;

    public RemindersActivityTest() {
        super(RemindersActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        remindersActivity = getActivity();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testWhenAReminderIsAddedThenItIsDisplayedInTheList() {
        addArbitraryReminder();
        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testGivenAReminderHasBeenAddedWhenTheDeviceIsRotatedItIsStillDisplayed() {
        addArbitraryReminder();
        remindersActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testGivenAReminderHashBeenAddedWhenItIsSwipedLeftThenItIsDismissed() {
        addArbitraryReminder();
        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.matches(isDisplayed()));

        onView(withText(ARBITRARY_REMINDER)).perform(swipeLeft());

        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.doesNotExist());
    }

    @Test
    public void testGivenAReminderHashBeenAddedWhenItIsSwipedRightThenItIsDismissed() {
        addArbitraryReminder();
        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.matches(isDisplayed()));

        onView(withText(ARBITRARY_REMINDER)).perform(swipeRight());

        onView(withText(ARBITRARY_REMINDER)).check(ViewAssertions.doesNotExist());
    }

    @Test
    public void testWhenTheStoresActionBarButtonIsPressedThenTheGroceryStoresCanBeViewed() {
        onView(withId(R.id.action_find_stores)).perform(click());

        onView(withText(getActivity().getApplication().getString(R.string.store_list_title)))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testWhenTheStoresActionBarButtonIsPressedThenTheGroceryStoresAreListed() {
        EspressoReminderApplication app = (EspressoReminderApplication)getInstrumentation().getTargetContext().getApplicationContext();

        List<Place> places = new ArrayList<Place>();
        Place arbitraryPlace = createDefaultGooglePlace();
        places.add(arbitraryPlace);
        GooglePlacesInterface googlePlacesMock = app.getEspressoRemoteResourcesModule().getGooglePlaces();
        when(googlePlacesMock.getNearbyPlacesRankedByDistance(anyDouble(), anyDouble(), (Param[]) anyVararg())).thenReturn(places);

        onView(withId(R.id.action_find_stores)).perform(click());

        onView(withText(getActivity().getApplication().getString(R.string.store_list_title)))
                .check(ViewAssertions.matches(isDisplayed()));

        verify(googlePlacesMock).getNearbyPlacesRankedByDistance(anyDouble(), anyDouble(), (Param[]) anyVararg());
    }

    private Place createDefaultGooglePlace() {
        Place place = new Place();
        place.setName("test");
        place.setLatitude(0.0);
        place.setLongitude(1.1);
        place.setPlaceId("test_id");

        return place;
    }

    private void addArbitraryReminder() {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.add_reminder_edit)).perform(typeText(ARBITRARY_REMINDER));
        onView(withId(R.id.add_reminder_button)).perform(click());
    }
}
