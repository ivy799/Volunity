package com.example.volunity.Database_config.User; // Adjust package as needed

import android.database.Cursor;

import com.example.volunity.Models.User; // Assuming your User model is in this package
import com.example.volunity.Database_config.User.UserDBContract; // Import your UserDBContract

import java.util.ArrayList;

public class UserMappingHelper {

    /**
     * Maps a Cursor object containing user data to an ArrayList of User objects.
     *
     * @param cursor The Cursor containing the user data retrieved from the database.
     * @return An ArrayList of User objects.
     */
    public static ArrayList<User> mapCursorToArrayList(Cursor cursor) {
        ArrayList<User> users = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.USERNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.EMAIL));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.PHONE_NUMBER));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.PASSWORD));
            String role = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.ROLE));

            users.add(new User(
                    id,
                    name,
                    email,
                    phoneNumber,
                    password,
                    role
            ));
        }
        return users;
    }

    /**
     * Maps a Cursor object to a single User object.
     * Use this if you expect only one user record (e.g., when searching by ID).
     *
     * @param cursor The Cursor containing the user data.
     * @return A User object if a record is found, or null if the cursor is empty.
     */
    public static User mapCursorToObject(Cursor cursor) {
        User user = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.USERNAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.EMAIL));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.PHONE_NUMBER));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.PASSWORD));
            String role = cursor.getString(cursor.getColumnIndexOrThrow(UserDBContract.UserColumns.ROLE));

            user = new User(
                    id,
                    name,
                    email,
                    phoneNumber,
                    password,
                    role
            );
        }
        return user;
    }
}