package com.example.volunity.Database_config.Favorite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.volunity.Database_config.DatabaseHelper; // Ensure this path is correct
import com.example.volunity.Database_config.User.UserDBContract; // Needed for foreign key context
import com.example.volunity.Database_config.Activity.ActivityDBContract; // Needed for foreign key context

public class FavoriteHelper {
    public static final String TABLE_NAME = FavoriteDBContract.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqLiteDatabase;
    public static volatile FavoriteHelper instance;
    private boolean isOpen = false;

    private FavoriteHelper(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static FavoriteHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (instance == null) {
                    instance = new FavoriteHelper(context);
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
     * Queries all favorite records from the database.
     * @return A Cursor containing all favorite entries, ordered by ID in ascending order.
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
                FavoriteDBContract.FavoriteColumns._ID + " ASC" // Order by ID ascending
        );
    }

    /**
     * Searches for a favorite record by its ID.
     * @param id The ID of the favorite entry to search for.
     * @return A Cursor containing the favorite record if found, otherwise an empty cursor.
     */
    public Cursor search(int id) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                FavoriteDBContract.FavoriteColumns._ID + " = ?", // Selection clause
                new String[]{String.valueOf(id)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Queries favorite activities for a specific user.
     * @param userId The ID of the user whose favorites to retrieve.
     * @return A Cursor containing favorite records for the given user ID.
     */
    public Cursor queryByUserId(int userId) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                FavoriteDBContract.FavoriteColumns.USER_ID + " = ?", // Selection clause
                new String[]{String.valueOf(userId)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Queries users who favorited a specific activity.
     * @param activityId The ID of the activity to check favorites for.
     * @return A Cursor containing favorite records for the given activity ID.
     */
    public Cursor queryByActivityId(int activityId) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                FavoriteDBContract.FavoriteColumns.ACTIVITIES_ID + " = ?", // Selection clause
                new String[]{String.valueOf(activityId)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Inserts a new favorite record into the database.
     * This method expects USER_ID and ACTIVITIES_ID in the ContentValues.
     * Due to the UNIQUE constraint, attempting to insert a duplicate (same USER_ID and ACTIVITIES_ID) will fail.
     * @param values A ContentValues object containing the USER_ID and ACTIVITIES_ID.
     * @return The row ID of the newly inserted row, or -1 if an error occurred (e.g., duplicate entry).
     */
    public long insert(ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    /**
     * Updates an existing favorite record in the database.
     * Note: Typically, favorites are inserted/deleted rather than updated,
     * but this method is provided for consistency.
     * @param id The ID (as a String) of the favorite record to update.
     * @param values A ContentValues object containing the new data.
     * @return The number of rows affected.
     */
    public long update(String id, ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.update(TABLE_NAME, values,
                FavoriteDBContract.FavoriteColumns._ID + " = ?",
                new String[]{id});
    }

    /**
     * Deletes a favorite record from the database by its ID.
     * @param id The ID (as a String) of the favorite record to delete.
     * @return The number of rows affected.
     */
    public long deleteById(String id) {
        ensureOpen();
        return sqLiteDatabase.delete(TABLE_NAME,
                FavoriteDBContract.FavoriteColumns._ID + " = ?",
                new String[]{id});
    }

    /**
     * Deletes a favorite record by USER_ID and ACTIVITY_ID.
     * This is useful for "unfavoriting" an activity directly.
     * @param userId The ID of the user.
     * @param activityId The ID of the activity.
     * @return The number of rows affected.
     */
    public long deleteByUserIdAndActivityId(int userId, int activityId) {
        ensureOpen();
        return sqLiteDatabase.delete(TABLE_NAME,
                FavoriteDBContract.FavoriteColumns.USER_ID + " = ? AND " +
                        FavoriteDBContract.FavoriteColumns.ACTIVITIES_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(activityId)});
    }
}