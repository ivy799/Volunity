package com.example.volunity.Database_config.City;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.volunity.Database_config.DatabaseHelper; // Ensure this path is correct for your main DatabaseHelper
import com.example.volunity.Database_config.Province.ProvinceDBContract; // Needed for potential joins or understanding foreign keys

public class CityHelper {
    public static final String TABLE_NAME = CityDBContract.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqLiteDatabase;
    public static volatile CityHelper instance;
    private boolean isOpen = false;

    private CityHelper(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static CityHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (instance == null) {
                    instance = new CityHelper(context);
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
     * Queries all city records from the database.
     * @return A Cursor containing all city records, ordered by ID in ascending order.
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
                CityDBContract.CityColumns._ID + " ASC" // Order by ID ascending
        );
    }

    /**
     * Queries city records by a specific province ID.
     * This is useful for getting all cities belonging to a certain province.
     * @param provinceId The ID of the province to filter by.
     * @return A Cursor containing city records for the given province ID.
     */
    public Cursor queryByProvinceId(int provinceId) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                CityDBContract.CityColumns.PROVINCE_ID + " = ?", // Selection clause
                new String[]{String.valueOf(provinceId)}, // Selection argument
                null, // No groupBy
                null, // No having
                CityDBContract.CityColumns.NAME + " ASC" // Order by city name
        );
    }

    /**
     * Searches for a city record by its ID.
     * @param id The ID of the city to search for.
     * @return A Cursor containing the city record if found, otherwise an empty cursor.
     */
    public Cursor search(int id) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                CityDBContract.CityColumns._ID + " = ?", // Selection clause
                new String[]{String.valueOf(id)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Inserts a new city record into the database.
     * Requires CityDBContract.CityColumns.PROVINCE_ID and CityDBContract.CityColumns.NAME in ContentValues.
     * @param values A ContentValues object containing the city's data.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insert(ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    /**
     * Updates an existing city record in the database.
     * @param id The ID (as a String) of the city to update.
     * @param values A ContentValues object containing the new data for the city.
     * @return The number of rows affected.
     */
    public long update(String id, ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.update(TABLE_NAME, values,
                CityDBContract.CityColumns._ID + " = ?",
                new String[]{id});
    }

    /**
     * Deletes a city record from the database by its ID.
     * @param id The ID (as a String) of the city to delete.
     * @return The number of rows affected.
     */
    public long deleteById(String id) {
        ensureOpen();
        return sqLiteDatabase.delete(TABLE_NAME,
                CityDBContract.CityColumns._ID + " = ?",
                new String[]{id});
    }
    public Cursor queryByName(String name) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null,
                CityDBContract.CityColumns.NAME + " = ?",
                new String[]{name},
                null,
                null,
                null
        );
    }
}