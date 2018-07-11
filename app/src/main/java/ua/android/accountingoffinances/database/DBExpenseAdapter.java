package ua.android.accountingoffinances.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Calendar;

import ua.android.accountingoffinances.model.Category;
import ua.android.accountingoffinances.model.Expense;
import ua.android.accountingoffinances.util.Util;

/** A class that provides work with table expenses in database. */

public class DBExpenseAdapter extends DatabaseConnectivity{

    public DBExpenseAdapter(Context context) {
        super(context);
    }

    /** Add an {@link Expense} to db. */
    public long insert(Expense expense) {
        long resultId = -2;
        Expense tempExpense = getExpenseByCategoryByDay(expense.getDate(), expense.getCategory().getId());
        if (tempExpense.getCategory() != null){// if Expense for such category for this day already exists, then update
            expense.setId(tempExpense.getId());
            resultId = update(expense);
        }else {
            try {
                open();

                //saving items
                ContentValues contentValues = new ContentValues();
                contentValues.put(AMOUNT, expense.getAmount());
                contentValues.put(DATE, expense.getDate());
                contentValues.put(CATEGORY, expense.getCategory().getId());
                resultId = db.insert(TABLE_NAME_EXPENSES, null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }

        return resultId;
    }

    /** Delete an {@link Expense} in db. */
    public long delete(Expense expense) {
        long resultNumberOfDeletedRows = -1;

        try {
            open();
            String whereClause = EXPENSE_ID+"=?";
            String[] whereArgs = new String[] { String.valueOf(expense.getId()) };
            resultNumberOfDeletedRows = db.delete(TABLE_NAME_EXPENSES, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return resultNumberOfDeletedRows;
    }

    /** Update an {@link Expense} in db. */
    public int update(Expense expense) {
        int resultCount = -2;
        try {
            open();
            //saving items
            ContentValues contentValues = new ContentValues();
            contentValues.put(AMOUNT, expense.getAmount());
            contentValues.put(DATE, expense.getDate());
            contentValues.put(CATEGORY, expense.getCategory().getId());

            String selection = EXPENSE_ID + "=?";
            String[] selectionArgs =  new String[]{String.valueOf(expense.getId())};

            resultCount = db.update(
                    TABLE_NAME_EXPENSES,
                    contentValues,
                    selection,
                    selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return resultCount;
    }

    /**
     * Returns an {@link Expense} by id.
     *
     * @param id The Expense's id you want to get.
     */
    public Expense getExpense(int id){
        Expense expense = new Expense();
        try {
            open();

            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder
                    .setTables(TABLE_NAME_EXPENSES
                            + " INNER JOIN "
                            + TABLE_NAME_CATEGORIES
                            + " ON "
                            + CATEGORY
                            + " = "
                            + (TABLE_NAME_CATEGORIES + "." + CATEGORY_ID));

            String[] projection = new String[] {
                    TABLE_NAME_EXPENSES + "."+EXPENSE_ID,
                    AMOUNT,
                    DATE,
                    CATEGORY,
                    TABLE_NAME_CATEGORIES + "."
                            + NAME };
            String selection = TABLE_NAME_EXPENSES + "."+EXPENSE_ID+"=?";
            String[] selectionArgs =  new String[]{String.valueOf(id)};

            // Get cursor
            Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null,
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
                expense = cursorToExpense(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return expense;
    }

    /**
     * Returns an {@link Expense} by day for specific Category.
     *
     * @param dateTimeMillis The date when expense was added.
     * @param category The Category of Expense.
     */
    public Expense getExpenseByCategoryByDay(long dateTimeMillis, int category){
        long[] bounds = Util.getDayBounds(dateTimeMillis);
        ArrayList<Expense> listExpenses = getExpenses(bounds[0], bounds[1], category);
        if (!listExpenses.isEmpty()){
            return listExpenses.get(0);
        }
        return new Expense();
    }

    /** Returns a list of all Expenses. */
    public ArrayList<Expense> getExpenses(){
        ArrayList<Expense> listExpenses = new ArrayList<>();
        try {
            open();

            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder
                    .setTables(TABLE_NAME_EXPENSES
                            + " INNER JOIN "
                            + TABLE_NAME_CATEGORIES
                            + " ON "
                            + CATEGORY
                            + " = "
                            + (TABLE_NAME_CATEGORIES + "." + CATEGORY_ID));

            // Get cursor
            Cursor cursor = queryBuilder.query(db, new String[] {
                            TABLE_NAME_EXPENSES + "."+EXPENSE_ID,
                            AMOUNT,
                            DATE,
                            CATEGORY,
                            TABLE_NAME_CATEGORIES + "."
                                    + NAME }, null, null, null, null,
                    DATE);

            while (cursor.moveToNext()) {
                listExpenses.add(cursorToExpense(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return listExpenses;
    }

    /** Returns a list of Expenses in the interval. *
     *
     * @param firstDate The first date in the interval.
     * @param secondDate The second date in the interval.
     */
    private ArrayList<Expense> getExpenses(long firstDate, long secondDate, int category){
        ArrayList<Expense> listExpenses = new ArrayList<>();
        try {
            open();

            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder
                    .setTables(TABLE_NAME_EXPENSES
                            + " INNER JOIN "
                            + TABLE_NAME_CATEGORIES
                            + " ON "
                            + CATEGORY
                            + " = "
                            + (TABLE_NAME_CATEGORIES + "." + CATEGORY_ID));

            String selection = DATE + " BETWEEN " + firstDate + " AND " + secondDate;
            if (category != -1){
                selection = "("+selection+")" + " AND " + CATEGORY +"="+category;
            }

            Cursor cursor = queryBuilder.query(db, new String[] {
                            TABLE_NAME_EXPENSES + "."+EXPENSE_ID,
                            AMOUNT,
                            DATE,
                            CATEGORY,
                            TABLE_NAME_CATEGORIES + "."
                                    + NAME }, selection, null, null, null,
                    DATE);

            while (cursor.moveToNext()) {
                listExpenses.add(cursorToExpense(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return listExpenses;
    }


    /** Returns a list of Expenses by day. *
     *
     * @param dateTimeMillis The date which belong to interval of day.
     */
    public ArrayList<Expense> getExpensesByDay(long dateTimeMillis){
        long[] bounds = Util.getDayBounds(dateTimeMillis);
        return getExpenses(bounds[0], bounds[1], -1);
    }

    /** Returns a list of Expenses by month. *
     *
     * @param dateTimeMillis The date which belong to interval of month.
     */
    public ArrayList<Expense> getExpensesByMonth(long dateTimeMillis){
        long[] bounds = Util.getMonthBounds(dateTimeMillis);
        return getExpenses(bounds[0], bounds[1], -1);
    }

    /** Returns a sum of Expenses by month. *
     *
     * @param dateTimeMillis The date which belong to interval of month.
     */
    public double getSumExpensesByMonth(long dateTimeMillis){
        long[] bounds = Util.getMonthBounds(dateTimeMillis);
        return getSumExpensesByPeriod(bounds[0], bounds[1]);
    }

    /** Returns an ArrayMap<Category, Double> of sums of Expenses by Categories per month. *
     *
     * @param dateTimeMillis The date which belong to interval of month.
     */
    public ArrayMap<Category, Double> getSumExpensesByCategoriesByMonth(long dateTimeMillis){
        long[] bounds = Util.getMonthBounds(dateTimeMillis);
        return getSumExpensesByCategoriesByPeriod(bounds[0], bounds[1]);
    }

    /** Returns a sum of Expenses per day. *
     *
     * @param dateTimeMillis The date which belong to interval of month.
     */
    public double getSumExpensesByDay(long dateTimeMillis){
        long[] bounds = Util.getDayBounds(dateTimeMillis);
        return getSumExpensesByPeriod(bounds[0], bounds[1]);
    }

    /** Returns a list of sums of Expenses by Categories by day. *
     *
     * @param dateTimeMillis The date which belong to interval of month.
     */
    public ArrayMap<Category, Double> getSumExpensesByCategoriesByDay(long dateTimeMillis){
        long[] bounds = Util.getDayBounds(dateTimeMillis);
        return getSumExpensesByCategoriesByPeriod(bounds[0], bounds[1]);
    }

    /** Returns a sum of Expenses in the interval. *
     *
     * @param firstDate The first date in the interval.
     * @param secondDate The second date in the interval.
     */
    private double getSumExpensesByPeriod(long firstDate, long secondDate){
        double result = 0;
        try {
            open();

            Cursor cur = db.rawQuery("SELECT SUM("+AMOUNT+") FROM "+TABLE_NAME_EXPENSES+
                    " WHERE "+DATE +" BETWEEN "+firstDate+" AND "+secondDate, null);

            if(cur.moveToFirst()) {
                result = cur.getDouble(0);
                cur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return result;
    }

    /** Returns ArrayMap<Category, Double> sums of Expenses in the interval by categories. *
     *
     * @param firstDate The first date in the interval.
     * @param secondDate The second date in the interval.
     */
    private ArrayMap<Category, Double> getSumExpensesByCategoriesByPeriod(long firstDate, long secondDate){
        ArrayMap<Category, Double> sumMap = new ArrayMap<>();
        try {
            open();

            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder
                    .setTables(TABLE_NAME_EXPENSES
                            + " INNER JOIN "
                            + TABLE_NAME_CATEGORIES
                            + " ON "
                            + CATEGORY
                            + " = "
                            + (TABLE_NAME_CATEGORIES + "." + CATEGORY_ID));

            // Get cursor
            Cursor cursor = queryBuilder.query(db, new String[] {
                            "SUM("+AMOUNT+")",
                            CATEGORY,
                            TABLE_NAME_CATEGORIES + "."
                                    + NAME },
                    DATE +" BETWEEN "+firstDate+" AND "+secondDate, null,
                    CATEGORY, null,
                    CATEGORY);

            for (int i = 0; i < cursor.getCount(); i ++){
                cursor.moveToNext();
                sumMap.put(new Category(cursor.getInt(1), cursor.getString(2)),
                        cursor.getDouble(0));
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return sumMap;
    }

    /** Delete all rows in table expenses. */
    public void deleteAll() {
        try {
            open();
            db.delete(TABLE_NAME_EXPENSES, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * Retrieve a {@link Expense} from {@link Cursor}.
     *
     * @param cursor The Cursor you want to process.
     */
    private Expense cursorToExpense(Cursor cursor) {
        Expense expense = new Expense();

        if (cursor.getCount() > 0) {
            expense.setId(cursor.getInt(0));
            expense.setAmount(cursor.getDouble(1));
            expense.setDate(cursor.getLong(2));

            Category category = new Category(cursor.getInt(3), cursor.getString(4));

            expense.setCategory(category);
        }
        return expense;
    }

}
