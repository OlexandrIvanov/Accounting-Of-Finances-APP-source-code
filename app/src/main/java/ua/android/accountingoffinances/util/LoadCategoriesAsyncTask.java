package ua.android.accountingoffinances.util;

import android.os.AsyncTask;

import java.util.ArrayList;

import ua.android.accountingoffinances.database.DBCategoryAdapter;
import ua.android.accountingoffinances.model.Category;
import ua.android.accountingoffinances.views.AddNewCategoryActivity;

/**loading category list from the database using AsyncTask.*/

public class LoadCategoriesAsyncTask extends AsyncTask<Void,Void,ArrayList<Category>> {

    AddNewCategoryActivity addNewCategoryActivity;

    public LoadCategoriesAsyncTask(AddNewCategoryActivity addNewCategoryActivity) {
        this.addNewCategoryActivity = addNewCategoryActivity;
    }

    @Override
    protected ArrayList<Category> doInBackground(Void... voids) {

        DBCategoryAdapter dbCategoryAdapter = new DBCategoryAdapter(addNewCategoryActivity);


        return dbCategoryAdapter.getCategories();
    }

    @Override
    protected void onPostExecute(ArrayList<Category> result) {
        super.onPostExecute(result);

        addNewCategoryActivity.setAdapter(result);

    }

}
