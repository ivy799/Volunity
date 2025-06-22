package com.example.volunity.Database_config.UserDetail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.volunity.Database_config.DatabaseHelper; // Pastikan ini adalah jalur yang benar
import com.example.volunity.Database_config.User.UserDBContract; // Untuk referensi Foreign Key USER_ID
import com.example.volunity.Database_config.City.CityDBContract; // Untuk referensi Foreign Key CITY_ID
import com.example.volunity.Database_config.Province.ProvinceDBContract; // Untuk referensi Foreign Key PROVINCE_ID
import com.example.volunity.Models.UserDetail;

import java.text.ParseException;


public class UserDetailHelper {
    public static final String TABLE_NAME = UserDetailDBContract.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqLiteDatabase;
    public static volatile UserDetailHelper instance;
    private boolean isOpen = false;

    private UserDetailHelper(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static UserDetailHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (instance == null) {
                    instance = new UserDetailHelper(context);
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
     * Queries all user detail records from the database.
     * @return A Cursor containing all user detail records, ordered by ID in ascending order.
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
                UserDetailDBContract.UserDetailColums._ID + " ASC" // Order by ID ascending
        );
    }

    /**
     * Searches for a user detail record by its primary ID.
     * @param id The primary ID of the user detail entry to search for.
     * @return A Cursor containing the user detail record if found, otherwise an empty cursor.
     */
    public Cursor search(int id) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                UserDetailDBContract.UserDetailColums._ID + " = ?", // Selection clause
                new String[]{String.valueOf(id)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Queries a user detail record by its associated User ID.
     * Since user_id is likely unique for user details, this will typically return 0 or 1 record.
     * @param userId The USER_ID of the user detail to search for.
     * @return A Cursor containing the user detail record for the given User ID, or empty.
     */
    public Cursor queryByUserId(int userId) {
        ensureOpen();
        return sqLiteDatabase.query(
                TABLE_NAME,
                null, // All columns
                UserDetailDBContract.UserDetailColums.USER_ID + " = ?", // Selection clause
                new String[]{String.valueOf(userId)}, // Selection argument
                null, // No groupBy
                null, // No having
                null // No orderBy
        );
    }

    /**
     * Inserts a new user detail record into the database.
     * @param values A ContentValues object containing the user detail's data.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insert(ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    /**
     * Updates an existing user detail record in the database.
     * @param id The primary ID (as a String) of the user detail to update.
     * @param values A ContentValues object containing the new data for the user detail.
     * @return The number of rows affected.
     */
    public long update(String id, ContentValues values) {
        ensureOpen();
        return sqLiteDatabase.update(TABLE_NAME, values,
                UserDetailDBContract.UserDetailColums._ID + " = ?",
                new String[]{id});
    }

    /**
     * Deletes a user detail record from the database by its primary ID.
     * @param id The primary ID (as a String) of the user detail to delete.
     * @return The number of rows affected.
     */
    public long deleteById(String id) {
        ensureOpen();
        return sqLiteDatabase.delete(TABLE_NAME,
                UserDetailDBContract.UserDetailColums._ID + " = ?",
                new String[]{id});
    }

    /**
     * Deletes a user detail record from the database by its USER_ID.
     * Useful if user_id is a unique key for details and you want to delete by it.
     * @param userId The USER_ID of the user detail to delete.
     * @return The number of rows affected.
     */
    public long deleteByUserId(int userId) {
        ensureOpen();
        return sqLiteDatabase.delete(TABLE_NAME,
                UserDetailDBContract.UserDetailColums.USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public UserDetail searchByUserId(int userId) throws ParseException {
        ensureOpen();
        UserDetail userDetails = null;
        Cursor cursor = sqLiteDatabase.query(
                TABLE_NAME,
                null,
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            userDetails = UserDetailMappingHelper.mapCursorToObject(cursor);
            cursor.close();
        }
        return userDetails;
    }

    public boolean existsForUserId(int userId) {
        ensureOpen();
        Cursor cursor = sqLiteDatabase.query(
                TABLE_NAME,
                new String[]{"user_id"},
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );
        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        return exists;
    }

    // 3. Insert user_details
    public long insertUserDetails(UserDetail details) {
        ensureOpen();
        ContentValues values = new ContentValues();
        values.put("user_id", details.getUserId());
        values.put("name", details.getName());
        values.put("photo_profile", details.getPhoto_profile());
        values.put("gender", details.getGender());
        if (details.getDateOfBirth() != null)
            values.put("date_of_birth", details.getDateOfBirth().getTime()); // as long
        values.put("city_id", details.getCityId());
        values.put("province_id", details.getProvinceId());
        return sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    // 4. Update user_details berdasarkan userId
    public int updateUserDetails(UserDetail details) {
        ensureOpen();
        ContentValues values = new ContentValues();
        values.put("name", details.getName());
        values.put("photo_profile", details.getPhoto_profile());
        values.put("gender", details.getGender());
        if (details.getDateOfBirth() != null)
            values.put("date_of_birth", details.getDateOfBirth().getTime());
        values.put("city_id", details.getCityId());
        values.put("province_id", details.getProvinceId());
        return sqLiteDatabase.update(
                TABLE_NAME,
                values,
                "user_id = ?",
                new String[]{String.valueOf(details.getUserId())}
        );
    }
}