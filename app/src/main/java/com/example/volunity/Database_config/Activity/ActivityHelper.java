package com.example.volunity.Database_config.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.volunity.Database_config.DatabaseHelper; // Ensure this path is correct
import com.example.volunity.Database_config.City.CityDBContract; // Needed for foreign key context
import com.example.volunity.Database_config.Province.ProvinceDBContract; // Needed for foreign key context

public class ActivityHelper {
    public static final String TABLE_NAME = ActivityDBContract.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqLiteDatabase;
    public static volatile ActivityHelper instance;
    private boolean isOpen = false;

    private ActivityHelper(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static ActivityHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (instance == null) {
                    instance = new ActivityHelper(context);
                }
            }
        }
        return instance;
    }

    public void open() {
        if (!isOpen || sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
            isOpen = true;
        }
    }

    public void close() {
        if (isOpen && sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
            isOpen = false;
        }
    }

    public boolean isOpen() {
        return isOpen && sqLiteDatabase != null && sqLiteDatabase.isOpen();
    }

    private void ensureOpen() {
        if (!isOpen()) {
            open();
        }
    }

    /**
     * Queries all activity records from the database.
     * @return A Cursor containing all activity records, ordered by ID in ascending order.
     */
    public Cursor queryAll() {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                null, // No selection
                null, // No selection arguments
                null, // No groupBy
                null, // No having
                ActivityDBContract.ActivityColumns._ID + " ASC" // Order by ID ascending
        );
    }

    /**
     * Searches for an activity record by its ID.
     * @param id The ID of the activity to search for.
     * @return A Cursor containing the activity record if found, otherwise an empty cursor.
     */
    public Cursor search(int id) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                ActivityDBContract.ActivityColumns._ID + " = ?", // Selection clause
                new String[]{String.valueOf(id)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Queries activity records by a specific city ID.
     * @param cityId The ID of the city to filter by.
     * @return A Cursor containing activity records for the given city ID, ordered by date.
     */
    public Cursor queryByCityId(int cityId) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                ActivityDBContract.ActivityColumns.CITY_ID + " = ?", // Selection clause
                new String[]{String.valueOf(cityId)}, // Selection argument
                null, // No groupBy
                null, // No having
                ActivityDBContract.ActivityColumns.DATE + " ASC" // Order by date ascending
        );
    }

    /**
     * Queries activity records by a specific province ID.
     * @param provinceId The ID of the province to filter by.
     * @return A Cursor containing activity records for the given province ID, ordered by date.
     */
    public Cursor queryByProvinceId(int provinceId) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                ActivityDBContract.ActivityColumns.PROVINCE_ID + " = ?", // Selection clause
                new String[]{String.valueOf(provinceId)}, // Selection argument
                null, // No groupBy
                null, // No having
                ActivityDBContract.ActivityColumns.DATE + " ASC" // Order by date ascending
        );
    }

    /**
     * Inserts a new activity record into the database.
     * @param values A ContentValues object containing the activity's data.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insert(ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    /**
     * Updates an existing activity record in the database.
     * @param id The ID (as a String) of the activity to update.
     * @param values A ContentValues object containing the new data for the activity.
     * @return The number of rows affected.
     */
    public long update(String id, ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.update(TABLE_NAME, values,
                ActivityDBContract.ActivityColumns._ID + " = ?",
                new String[]{id});
    }

    /**
     * Deletes an activity record from the database by its ID.
     * @param id The ID (as a String) of the activity to delete.
     * @return The number of rows affected.
     */
    public long deleteById(String id) {
        ensureOpen();
        return sqLiteDatabase.delete(TABLE_NAME,
                ActivityDBContract.ActivityColumns._ID + " = ?",
                new String[]{id});
    }
}