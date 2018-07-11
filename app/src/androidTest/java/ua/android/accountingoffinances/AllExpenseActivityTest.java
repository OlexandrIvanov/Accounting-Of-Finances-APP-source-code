package ua.android.accountingoffinances;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ua.android.accountingoffinances.views.AllExpenseActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class AllExpenseActivityTest {

   @Rule
    public ActivityTestRule<AllExpenseActivity> allExpenseActivityActivityTestRule  =
            new ActivityTestRule<AllExpenseActivity>(AllExpenseActivity.class);

    @Test
    public void checkTextViewValues()throws Exception{
        onView(withId(R.id.textViewMonthlyExpenses)).check(matches(withText(R.string.button_monthly_expenses)));

        onView(withId(R.id.textView_Expenses_Per_Day)).check(matches(withText(R.string.button_expenses_per_day)));

        onView(withId(R.id.textView_New_Expenses)).check(matches(withText(R.string.button_new_Expenses)));
        onView(withId(R.id.textView_New_Expenses)).perform(click());
        onView(withId(R.id.action_btn_ADD)).perform(click());

        onView(withText(R.string.toast_select_a_category)).inRoot(withDecorView(not(is(allExpenseActivityActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }







}
