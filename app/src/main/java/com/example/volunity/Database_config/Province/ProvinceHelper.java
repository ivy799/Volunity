package com.example.volunity.Database_config.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.volunity.Database_config.DatabaseHelper; // Ensure this path is correct for your main DatabaseHelper

public class ProvinceHelper {
    public static final String TABLE_NAME = ProvinceDBContract.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqLiteDatabase;
    public static volatile ProvinceHelper instance;
    private boolean isOpen = false;

    private ProvinceHelper(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static ProvinceHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (instance == null) {
                    instance = new ProvinceHelper(context);
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
     * Queries all province records from the database.
     * @return A Cursor containing all province records, ordered by ID in ascending order.
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
                ProvinceDBContract.ProvinceColumns._ID + " ASC" // Order by ID ascending
        );
    }

    /**
     * Searches for a province record by its ID.
     * @param id The ID of the province to search for.
     * @return A Cursor containing the province record if found, otherwise an empty cursor.
     */
    public Cursor search(int id) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                ProvinceDBContract.ProvinceColumns._ID + " = ?", // Selection clause
                new String[]{String.valueOf(id)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Inserts a new province record into the database.
     * @param values A ContentValues object containing the province's data (e.g., name).
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insert(ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    /**
     * Updates an existing province record in the database.
     * @param id The ID (as a String) of the province to update.
     * @param values A ContentValues object containing the new data for the province.
     * @return The number of rows affected.
     */
    public long update(String id, ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.update(TABLE_NAME, values,
                ProvinceDBContract.ProvinceColumns._ID + " = ?",
                new String[]{id});
    }

    /**
     * Deletes a province record from the database by its ID.
     * @param id The ID (as a String) of the province to delete.
     * @return The number of rows affected.
     */
    public long deleteById(String id) {
        ensureOpen();
        return sqLiteDatabase.delete(TABLE_NAME,
                ProvinceDBContract.ProvinceColumns._ID + " = ?",
                new String[]{id});
    }
}