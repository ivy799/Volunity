package com.example.volunity.Database_config.Registration_form; // Adjust package as needed

import android.database.Cursor;

import com.example.volunity.Models.Registration_form; // Assuming your RegistrationForm model is in this package
import com.example.volunity.Database_config.Registration_form.RegistrationFormDBContract; // Import your RegistrationFormDBContract

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class RegistrationFormMappingHelper {

    // Define formatter for timestamp strings.
    // Ensure this pattern matches how your SQLite database stores CURRENT_TIMESTAMP.
    // Common formats: "yyyy-MM-dd HH:mm:ss" or "yyyy-MM-dd HH:mm:ss.SSS"
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * Maps a Cursor object containing registration form data to an ArrayList of RegistrationForm objects.
     *
     * @param cursor The Cursor containing the registration form data retrieved from the database.
     * @return An ArrayList of RegistrationForm objects.
     * @throws DateTimeParseException If there's an issue parsing the timestamp string from the database.
     */
    public static ArrayList<Registration_form> mapCursorToArrayList(Cursor cursor) throws DateTimeParseException {
        ArrayList<Registration_form> forms = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns._ID));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.USER_ID));
            int activityId = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.ACTIVITY_ID));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.ADDRESS));
            int cityId = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.CITY_ID));
            int provinceId = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.PROVINCE_ID));
            String reasons = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.REASONS));
            String experiences = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.EXPERIENCES));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.STATUS));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.CREATED_AT));

            Timestamp createdAtTimestamp = null;
            if (createdAt != null) {
                createdAtTimestamp = Timestamp.valueOf(createdAt);
            }

            forms.add(new Registration_form(
                    id,
                    userId,
                    activityId,
                    address,
                    cityId,
                    provinceId,
                    reasons,
                    experiences,
                    status,
                    createdAtTimestamp // Pass as Timestamp object
            ));
        }
        return forms;
    }

    /**
     * Maps a Cursor object to a single RegistrationForm object.
     * Use this if you expect only one registration form record (e.g., when searching by ID).
     *
     * @param cursor The Cursor containing the registration form data.
     * @return A RegistrationForm object if a record is found, or null if the cursor is empty.
     * @throws DateTimeParseException If there's an issue parsing the timestamp string from the database.
     */
    public static Registration_form mapCursorToObject(Cursor cursor) throws DateTimeParseException {
        Registration_form form = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns._ID));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.USER_ID));
            int activityId = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.ACTIVITY_ID));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.ADDRESS));
            int cityId = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.CITY_ID));
            int provinceId = cursor.getInt(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.PROVINCE_ID));
            String reasons = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.REASONS));
            String experiences = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.EXPERIENCES));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.STATUS));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(RegistrationFormDBContract.RegistrationFormColumns.CREATED_AT));

            Timestamp createdAtTimestamp = null;
            if (createdAt != null) {
                createdAtTimestamp = Timestamp.valueOf(createdAt);
            }

            form = new Registration_form(
                    id,
                    userId,
                    activityId,
                    address,
                    cityId,
                    provinceId,
                    reasons,
                    experiences,
                    status,
                    createdAtTimestamp
            );
        }
        return form;
    }
}