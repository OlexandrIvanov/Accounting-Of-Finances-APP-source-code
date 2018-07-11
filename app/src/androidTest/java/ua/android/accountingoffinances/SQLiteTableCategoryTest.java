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
import ua.android.accountingoffinances.model.Category;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Instrumented test for testing the database, which will execute on an Android device.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SQLiteTableCategoryTest {
    private DBCategoryAdapter dbCategoryAdapter;

    @Before
    public void setUp(){
        Log.e("UNIT TEST", "UNIT TEST SET UP");
        dbCategoryAdapter = new DBCategoryAdapter(InstrumentationRegistry.getTargetContext());
        dbCategoryAdapter.open();
    }

    @After
    public void finish() {
        dbCategoryAdapter.close();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(dbCategoryAdapter);
    }

    @Test
    public void testShouldAddExpenseType() throws Exception {
        dbCategoryAdapter.insert(new Category(-1, "category_name"));
        List<Category> categoryList = dbCategoryAdapter.getCategories();

        assertThat(categoryList.size(), is(1));
        assertTrue(categoryList.get(0).getName().equals("category_name"));
    }

    @Test
    public void testDeleteAll() {
        dbCategoryAdapter.deleteAll();
        List<Category> categoryList = dbCategoryAdapter.getCategories();

        assertThat(categoryList.size(), is(0));
    }

    @Test
    public void testDeleteOnlyOne() {
        dbCategoryAdapter.insert(new Category(-1, "category_name"));
        List<Category> categoryList = dbCategoryAdapter.getCategories();

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

        dbCategoryAdapter.delete(categoryList.get(0));
        dbCategoryAdapter.delete(categoryList.get(1));

        categoryList = dbCategoryAdapter.getCategories();
        assertThat(categoryList.size(), is(1));
    }
}
