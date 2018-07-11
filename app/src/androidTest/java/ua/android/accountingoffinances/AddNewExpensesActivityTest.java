package ua.android.accountingoffinances;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ua.android.accountingoffinances.views.AddNewExpensesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;


@RunWith(AndroidJUnit4.class)

public class AddNewExpensesActivityTest {

    @Rule
    public ActivityTestRule<AddNewExpensesActivity> addNewExpensesActivityActivityTestRule  =
            new ActivityTestRule<AddNewExpensesActivity>(AddNewExpensesActivity.class);

    @Test
    public void checkAddExpenses()throws Exception{

        onView(withId(R.id.textViewCategory)).check(matches(withText("")));
        onView(withId(R.id.editTextAddSum)).check(matches(withText("")));

        onView(withId(R.id.relativeLayout)).perform(click());

        onView(withId(R.id.my_recycler_view)).perform(click());

        onView(withId(R.id.textViewCategory)).check(matches(not(withText(""))));

        onView(withId(R.id.editTextAddSum)).check(matches(withText("")));

        onView(withId(R.id.action_btn_ADD)).perform(click());

        onView(withText(R.string.toast_sum_is_empty)).inRoot(withDecorView(CoreMatchers.not(is(
                addNewExpensesActivityActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }
}
