package ua.android.accountingoffinances;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ua.android.accountingoffinances.database.DBCategoryAdapter;
import ua.android.accountingoffinances.database.DBExpenseAdapter;
import ua.android.accountingoffinances.model.Category;
import ua.android.accountingoffinances.model.Expense;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * Instrumented test for testing the database, which will execute on an Android device.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SQLiteDBTest {
    private DBExpenseAdapter dbExpenseAdapter;
    private DBCategoryAdapter dbCategoryAdapter;

    @Before
    public void setUp(){
        Log.e("UNIT TEST", "UNIT TEST SET UP");
        dbExpenseAdapter = new DBExpenseAdapter(InstrumentationRegistry.getTargetContext());
        dbCategoryAdapter = new DBCategoryAdapter(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void finish() {}

    @Test
    public void testPreConditions() {
        assertNotNull(dbExpenseAdapter);
    }

    @Test
    public void testShouldAddExpenseType() throws Exception {

        dbCategoryAdapter.insert(new Category(-1, "category_name"));
        List<Category> categoryList = dbCategoryAdapter.getCategories();

        assertThat(categoryList.size(), is(1));
        assertTrue(categoryList.get(0).getName().equals("category_name"));

        long date = System.currentTimeMillis();
        dbExpenseAdapter.insert(new Expense(-1, 200.3, date, categoryList.get(0)));
        List<Expense> expenseList = dbExpenseAdapter.getExpenses();

        assertThat(expenseList.size(), is(1));
        assertTrue(expenseList.get(0).getAmount() == 200.3);
        assertTrue(expenseList.get(0).getDate() == date);
        assertTrue(expenseList.get(0).getCategory().getName().equals("category_name"));
    }

    @Test
    public void testDeleteAll() {
        dbExpenseAdapter.deleteAll();
        dbCategoryAdapter.deleteAll();
        List<Expense> expenseList = dbExpenseAdapter.getExpenses();
        List<Category> categoryList = dbCategoryAdapter.getCategories();

        assertThat(expenseList.size(), is(0));
        assertThat(categoryList.size(), is(0));
    }

    @Test
    public void testDeleteOnlyOne() {
        dbCategoryAdapter.insert(new Category(-1, "category_name"));
        List<Category> categoryList = dbCategoryAdapter.getCategories();

        long date = System.currentTimeMillis();
        dbExpenseAdapter.insert(new Expense(-1, 200.3, date, categoryList.get(0)));
        List<Expense> expenseList = dbExpenseAdapter.getExpenses();

        assertThat(expenseList.size(), is(1));

        dbExpenseAdapter.delete(expenseList.get(0));
        expenseList = dbExpenseAdapter.getExpenses();

        assertThat(expenseList.size(), is(0));

        assertThat(categoryList.size(), is(1));

        dbCategoryAdapter.delete(categoryList.get(0));
        categoryList = dbCategoryAdapter.getCategories();

        assertThat(categoryList.size(), is(0));
    }

    @Test
    public void testAddAndDelete() {
        dbCategoryAdapter.deleteAll();
        dbCategoryAdapter.insert(new Category(-1, "category_name1"));
        dbCategoryAdapter.insert(new Category(-1, "category_name2"));
        dbCategoryAdapter.insert(new Category(-1, "category_name3"));
        List<Category> categoryList = dbCategoryAdapter.getCategories();
        assertThat(categoryList.size(), is(3));

        dbExpenseAdapter.deleteAll();
        dbExpenseAdapter.insert(new Expense(-1, 100.1, System.currentTimeMillis(), categoryList.get(0)));
        dbExpenseAdapter.insert(new Expense(-1, 200.2, System.currentTimeMillis(), categoryList.get(1)));
        dbExpenseAdapter.insert(new Expense(-1, 300.3, System.currentTimeMillis(), categoryList.get(2)));

        List<Expense> expenseList = dbExpenseAdapter.getExpenses();
        assertThat(expenseList.size(), is(3));

        dbExpenseAdapter.delete(expenseList.get(0));
        dbExpenseAdapter.delete(expenseList.get(1));

        expenseList = dbExpenseAdapter.getExpenses();
        assertThat(expenseList.size(), is(1));

        dbCategoryAdapter.delete(categoryList.get(0));
        dbCategoryAdapter.delete(categoryList.get(1));

        categoryList = dbCategoryAdapter.getCategories();
        assertThat(categoryList.size(), is(1));

        dbExpenseAdapter.deleteAll();
        expenseList = dbExpenseAdapter.getExpenses();

        assertThat(expenseList.size(), is(0));
    }

    @Test
    public void testSumExpensesForMonth() {
        dbCategoryAdapter.deleteAll();
        dbCategoryAdapter.insert(new Category(-1, "category_name1"));
        dbCategoryAdapter.insert(new Category(-1, "category_name2"));
        dbCategoryAdapter.insert(new Category(-1, "category_name3"));
        List<Category> categoryList = dbCategoryAdapter.getCategories();
        assertThat(categoryList.size(), is(3));

        dbExpenseAdapter.deleteAll();
        dbExpenseAdapter.insert(new Expense(-1, 100.2, System.currentTimeMillis(), categoryList.get(0)));
        dbExpenseAdapter.insert(new Expense(-1, 200.3, System.currentTimeMillis(), categoryList.get(1)));
        dbExpenseAdapter.insert(new Expense(-1, 300.4, System.currentTimeMillis(), categoryList.get(2)));

        List<Expense> expenseList = dbExpenseAdapter.getExpensesByMonth(System.currentTimeMillis());
        assertThat(expenseList.size(), is(3));

        double sumByMonth = dbExpenseAdapter.getSumExpensesByMonth(System.currentTimeMillis());

        // use this method because float is not precise
        assertEquals("", sumByMonth, 600.9, 0.001);

        dbExpenseAdapter.delete(expenseList.get(2));

        expenseList = dbExpenseAdapter.getExpensesByMonth(System.currentTimeMillis());
        assertThat(expenseList.size(), is(2));

        sumByMonth = dbExpenseAdapter.getSumExpensesByMonth(System.currentTimeMillis());

        // use this method because float is not precise
        assertEquals("", sumByMonth, 300.5, 0.001);
    }
}
