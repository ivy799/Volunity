package com.example.volunity.Database_config.Registration_form;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.volunity.Database_config.DatabaseHelper; // Ensure this path is correct for your main DatabaseHelper
import com.example.volunity.Database_config.User.UserDBContract; // Needed for foreign key context
import com.example.volunity.Database_config.Activity.ActivityDBContract; // Needed for foreign key context
import com.example.volunity.Database_config.City.CityDBContract; // Needed for foreign key context
import com.example.volunity.Database_config.Province.ProvinceDBContract; // Needed for foreign key context

public class RegistrationFormHelper {
    public static final String TABLE_NAME = RegistrationFormDBContract.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqLiteDatabase;
    public static volatile RegistrationFormHelper instance;
    private boolean isOpen = false;

    private RegistrationFormHelper(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static RegistrationFormHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (instance == null) {
                    instance = new RegistrationFormHelper(context);
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
     * Queries all registration form records from the database.
     * @return A Cursor containing all registration forms, ordered by ID in ascending order.
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
                RegistrationFormDBContract.RegistrationFormColumns._ID + " ASC" // Order by ID ascending
        );
    }

    /**
     * Searches for a registration form record by its ID.
     * @param id The ID of the registration form to search for.
     * @return A Cursor containing the registration form record if found, otherwise an empty cursor.
     */
    public Cursor search(int id) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                RegistrationFormDBContract.RegistrationFormColumns._ID + " = ?", // Selection clause
                new String[]{String.valueOf(id)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Queries registration forms by a specific user ID.
     * @param userId The ID of the user to filter by.
     * @return A Cursor containing registration forms for the given user ID.
     */
    public Cursor queryByUserId(int userId) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                RegistrationFormDBContract.RegistrationFormColumns.USER_ID + " = ?", // Selection clause
                new String[]{String.valueOf(userId)}, // Selection argument
                null, // No groupBy
                null, // No having
                RegistrationFormDBContract.RegistrationFormColumns.CREATED_AT + " DESC" // Order by most recent
        );
    }

    /**
     * Queries registration forms by a specific activity ID.
     * @param activityId The ID of the activity to filter by.
     * @return A Cursor containing registration forms for the given activity ID.
     */
    public Cursor queryByActivityId(int activityId) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                RegistrationFormDBContract.RegistrationFormColumns.ACTIVITY_ID + " = ?", // Selection clause
                new String[]{String.valueOf(activityId)}, // Selection argument
                null, // No groupBy
                null, // No having
                RegistrationFormDBContract.RegistrationFormColumns.CREATED_AT + " DESC" // Order by most recent
        );
    }

    /**
     * Queries registration forms by their status.
     * @param status The status to filter by (e.g., "pending", "approved", "rejected").
     * @return A Cursor containing registration forms with the specified status.
     */
    public Cursor queryByStatus(String status) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                RegistrationFormDBContract.RegistrationFormColumns.STATUS + " = ?", // Selection clause
                new String[]{status}, // Selection argument
                null, // No groupBy
                null, // No having
                RegistrationFormDBContract.RegistrationFormColumns.CREATED_AT + " DESC" // Order by most recent
        );
    }

    /**
     * Inserts a new registration form record into the database.
     * @param values A ContentValues object containing the form's data.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insert(ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    /**
     * Updates an existing registration form record in the database.
     * @param id The ID (as a String) of the registration form to update.
     * @param values A ContentValues object containing the new data for the form.
     * @return The number of rows affected.
     */
    public long update(String id, ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.update(TABLE_NAME, values,
                RegistrationFormDBContract.RegistrationFormColumns._ID + " = ?",
                new String[]{id});
    }

    /**
     * Deletes a registration form record from the database by its ID.
     * @param id The ID (as a String) of the registration form to delete.
     * @return The number of rows affected.
     */
    public long deleteById(String id) {
        ensureOpen();
        return sqLiteDatabase.delete(TABLE_NAME,
                RegistrationFormDBContract.RegistrationFormColumns._ID + " = ?",
                new String[]{id});
    }
}