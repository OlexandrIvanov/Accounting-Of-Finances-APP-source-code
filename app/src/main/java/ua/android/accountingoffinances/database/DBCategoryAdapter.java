package ua.android.accountingoffinances.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import ua.android.accountingoffinances.model.Category;

/** A class that provides work with table categories in database. */

public class DBCategoryAdapter extends DatabaseConnectivity{

    public DBCategoryAdapter(Context context) {
        super(context);
    }

    /** Add a {@link Category} to db. */
    public long insert(Category category) {
        long resultId = -2;

        try {
            open();
            //saving items
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, category.getName());
            resultId = db.insert(TABLE_NAME_CATEGORIES, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return resultId;
    }

    /** Delete a {@link Category} in db. */
    public long delete(Category category) {
        long resultNumberOfDeletedRows = -1;

        try {
            open();
            String whereClause = CATEGORY_ID+"=?";
            String[] whereArgs = new String[] { String.valueOf(category.getId()) };
            resultNumberOfDeletedRows = db.delete(TABLE_NAME_CATEGORIES, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return resultNumberOfDeletedRows;
    }

    /** Update a {@link Category} in db. */
    public int update(Category category) {
        int resultCount = -2;

        try {
            open();
            //saving items
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, category.getName());

            String selection = CATEGORY_ID+"=?";
            String[] selectionArgs =  new String[]{String.valueOf(category.getId())};

            resultCount = db.update(
                    TABLE_NAME_CATEGORIES,
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
     * Returns a {@link Category} by id.
     *
     * @param id The Category's id you want to get.
     */
    public Category getCategory(int id){
        Category category = new Category();
        try {
            open();

            String selection = CATEGORY_ID+"=?";
            String[] selectionArgs =  new String[]{String.valueOf(id)};

            Cursor cursor = db.query(
                    TABLE_NAME_CATEGORIES,
                    null,
                    selection,
                    selectionArgs, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                category = cursorToCategory(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return category;
    }

    /** Returns a list of all Categories. */
    public ArrayList<Category> getCategories(){
        ArrayList<Category> listCategories = new ArrayList<>();
        try {
            open();

            Cursor cursor = db.query(
                    TABLE_NAME_CATEGORIES,
                    null,
                    null,
                    null, null, null, CATEGORY_ID);
            while (cursor.moveToNext()) {
                listCategories.add(cursorToCategory(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return listCategories;
    }

    /** Delete all rows in table categories. */
    public void deleteAll() {
        try {
            open();
            db.delete(TABLE_NAME_CATEGORIES, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * Retrieve a {@link Category} from {@link Cursor}.
     *
     * @param cursor The Cursor you want to process.
     */
    private Category cursorToCategory(Cursor cursor) {
        Category category = new Category();
        if (cursor.getCount() > 0) {
            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
        }
        return category;
    }
}
