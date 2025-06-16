package com.example.volunity.Database_config.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.volunity.Database_config.DatabaseHelper;

public class UserHelper {
    public static final String TABLE_NAME = UserDBContract.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqLiteDatabase;
    public static volatile UserHelper instance;
    private boolean isOpen = false;

    private UserHelper(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static UserHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (instance == null) {
                    instance = new UserHelper(context);
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
     * Queries all user records from the database.
     * @return A Cursor containing all user records, ordered by ID in ascending order.
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
                UserDBContract.UserColumns._ID + " ASC" // Order by ID ascending
        );
    }

    /**
     * Searches for a user record by their ID.
     * @param id The ID of the user to search for.
     * @return A Cursor containing the user record if found, otherwise an empty cursor.
     */
    public Cursor search(int id) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                UserDBContract.UserColumns._ID + " = ?", // Selection clause
                new String[]{String.valueOf(id)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Inserts a new user record into the database.
     * @param values A ContentValues object containing the user's data.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insert(ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    /**
     * Updates an existing user record in the database.
     * @param id The ID of the user to update.
     * @param values A ContentValues object containing the new data for the user.
     * @return The number of rows affected.
     */
    public long update(String id, ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.update(TABLE_NAME, values,
                UserDBContract.UserColumns._ID + " = ?",
                new String[]{id});
    }

    /**
     * Deletes a user record from the database by their ID.
     * @param id The ID of the user to delete.
     * @return The number of rows affected.
     */
    public long deleteById(String id) {
        ensureOpen();
        return sqLiteDatabase.delete(TABLE_NAME,
                UserDBContract.UserColumns._ID + " = ?",
                new String[]{id});
    }
}
