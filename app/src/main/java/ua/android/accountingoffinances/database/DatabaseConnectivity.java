package ua.android.accountingoffinances.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ua.android.accountingoffinances.R;

/** A class that provides work with database. */

public class DatabaseConnectivity {

    protected final Context context;

    private static DatabaseHelper dbHelper;
    SQLiteDatabase db;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DPHLocalDB";

    /**
     * Table names for table Expenses
     */
    static final String TABLE_NAME_EXPENSES = "expenses";
    static final String TABLE_NAME_CATEGORIES = "categories";

    /**
     * Column names for table Expenses
     */
    static final String EXPENSE_ID = "_id";
    static final String AMOUNT = "amount";
    static final String DATE = "date";
    static final String CATEGORY = "category_id";

    /**
     * Columns names for table Categories
     */
    static final String CATEGORY_ID = "_id";
    static final String NAME = "name";


    public DatabaseConnectivity(Context context) {
        this.context = context;
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
        }
    }

    /**
     * The query for creating table Expenses in database
     */
    private static final String CREATE_EXPENSES_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_EXPENSES
            + "("
            + EXPENSE_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AMOUNT
            + " REAL, "
            + DATE
            + " INTEGER, "
            + CATEGORY
            + " INTEGER, "
            +" FOREIGN KEY ("+CATEGORY+") REFERENCES "
            + TABLE_NAME_CATEGORIES + "("+ CATEGORY_ID +") ON DELETE CASCADE"
            +")";


    /**
     * The query for creating table Categories in database
     */
    private static final String CREATE_CATEGORIES_TABLE = "CREATE TABLE "
            + TABLE_NAME_CATEGORIES
            + " ("
            + CATEGORY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME
            + " INTEGER"
            +")";

    // ---opens the database---
    public DatabaseConnectivity open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    // ---closes the database---
    public void close() {
        dbHelper.close();
    }

    /** Class responsible for work with database such as creating, upgrading*/
    protected class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_CATEGORIES_TABLE);
            db.execSQL(CREATE_EXPENSES_TABLE);
            fillDefValues(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EXPENSES);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORIES);
                onCreate(db);
            }
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            db.setForeignKeyConstraintsEnabled(true);
        }

        /**
         * Fill default values in database tables.
         *
         * @param db The {@link SQLiteDatabase} with the help of which execute SQL commands.
         */
        private void fillDefValues(SQLiteDatabase db){
            ContentValues cv = new ContentValues();
            cv.put(NAME, context.getString(R.string.def_category));
            db.insert(TABLE_NAME_CATEGORIES, null, cv);
        }
    }

}
