package ua.android.accountingoffinances.util;

import android.os.AsyncTask;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Calendar;

import ua.android.accountingoffinances.database.DBCategoryAdapter;
import ua.android.accountingoffinances.database.DBExpenseAdapter;
import ua.android.accountingoffinances.model.Category;
import ua.android.accountingoffinances.views.ExpenditureStatisticsActivity;

    /**loading Expenses list from the database using AsyncTask.*/

public class LoadExpensesAsyncTask extends AsyncTask<Void, Void, ArrayList<ArrayMap<Category, Double>>> {

    private ExpenditureStatisticsActivity activity;
    private int mode;
    private long timeInMillis;

//    List of Categories - used in table header
    private ArrayList<Category> categoryArrayList;

//    ArrayMap where Category - key and Double - value.
//    Double is the amount of expenses by specific category
//    Used in table footer in month expenses table and for whole table in daily expenses
    private ArrayMap<Category, Double> sumsByCategoryMap;

//    ArrayMap where Integer - key and Double - value.
//    Integer is the day of the month
//    Double is the amount of expenses by each day
//    Used for daily expense column in month expenses table
    private ArrayMap<Integer, Double> sumsByDayMap;

    public LoadExpensesAsyncTask(ExpenditureStatisticsActivity activity, int mode, long timeInMillis) {
        this.activity = activity;
        this.mode = mode;
        this.timeInMillis = timeInMillis;
    }

    @Override
    protected ArrayList<ArrayMap<Category, Double>> doInBackground(Void... params) {
        categoryArrayList = getCategories();

        ArrayList<ArrayMap<Category, Double>> sumsArrayList = new ArrayList<>();

        if (mode == Constants.MONTH_STATISTICS){
            DBExpenseAdapter dbExpenseAdapter = new DBExpenseAdapter(activity);
            sumsByCategoryMap = dbExpenseAdapter.getSumExpensesByCategoriesByMonth(timeInMillis);

            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(timeInMillis);
            int maxDay = date.getMaximum(Calendar.DAY_OF_MONTH);

            sumsByDayMap = new ArrayMap<>();
            for (int i = 1; i <= maxDay; i ++){
                date.set(Calendar.DAY_OF_MONTH, i);
                long tmpTime = date.getTimeInMillis();
                ArrayMap<Category, Double> tmpArrayMap = dbExpenseAdapter.getSumExpensesByCategoriesByDay(tmpTime);
                sumsArrayList.add(tmpArrayMap);
                if (tmpArrayMap.size()>0){
                    sumsByDayMap.put(i, dbExpenseAdapter.getSumExpensesByDay(tmpTime));
                }
            }
        }else {
            DBExpenseAdapter dbExpenseAdapter = new DBExpenseAdapter(activity);
            sumsArrayList.add(dbExpenseAdapter.getSumExpensesByCategoriesByDay(timeInMillis));
            sumsByCategoryMap = dbExpenseAdapter.getSumExpensesByCategoriesByDay(timeInMillis);
        }

        return sumsArrayList;
    }

    private ArrayList<Category> getCategories(){
        DBCategoryAdapter dbCategoryAdapter = new DBCategoryAdapter(activity);
        return dbCategoryAdapter.getCategories();
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayMap<Category, Double>> sumsArrayList) {
        super.onPostExecute(sumsArrayList);
        if (!isCancelled() && activity != null) {
            activity.fillData(categoryArrayList, sumsArrayList, sumsByCategoryMap, sumsByDayMap, timeInMillis);
        }
    }
}
