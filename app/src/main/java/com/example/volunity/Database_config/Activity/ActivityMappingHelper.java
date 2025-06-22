package com.example.volunity.Database_config.Activity;

import android.database.Cursor;
import com.example.volunity.Models.Activity;
import com.example.volunity.Database_config.Activity.ActivityDBContract;

import java.sql.Timestamp;
import java.time.LocalDate; // Perubahan: Import LocalDate
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // Import untuk penanganan kesalahan parsing
import java.util.ArrayList;
import java.util.Locale;

public class ActivityMappingHelper {

    // Formatter untuk created_at/updated_at (LocalDateTime)
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    // Perubahan: Formatter baru untuk LocalDate (sesuai dengan cara Anda menyimpan di DB, idealnya ISO_LOCAL_DATE)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // Format "yyyy-MM-dd"

    private static LocalDate sanitizeActivityDate(String value) { // Perubahan: Method baru untuk LocalDate
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(value, DATE_FORMATTER); // Perubahan: Gunakan DATE_FORMATTER
        } catch (DateTimeParseException e) {
            // Log the error and return null, or throw a more specific exception if needed
            e.printStackTrace();
            return null;
        }
    }

    private static LocalDateTime sanitizeCreatedAtUpdatedAt(String value) { // Perubahan: Method terpisah untuk LocalDateTime
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(value, DATETIME_FORMATTER); // Perubahan: Gunakan DATETIME_FORMATTER
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Timestamp toTimestamp(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            // This assumes the timestamp string is in a format that Timestamp.valueOf can parse directly
            // which is usually "yyyy-mm-dd hh:mm:ss[.fffffffff]"
            return Timestamp.valueOf(value);
        } catch (IllegalArgumentException e) { // Catch IllegalArgumentException for Timestamp.valueOf
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Activity> mapCursorToArrayList(Cursor cursor) {
        ArrayList<Activity> activities = new ArrayList<>();

        while (cursor.moveToNext()) {
            Activity activity = extractActivity(cursor);
            if (activity != null) {
                activities.add(activity);
            }
        }

        return activities;
    }

    public static Activity mapCursorToObject(Cursor cursor) {
        if (cursor.moveToFirst()) {
            return extractActivity(cursor);
        }
        return null;
    }

    private static Activity extractActivity(Cursor cursor) {
        try {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns._ID));
            int organizerId = cursor.getInt(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.ORGANIZER_ID));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.IMAGE));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.TITLE));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.ADDRESS));
            int cityId = cursor.getInt(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.CITY_ID));
            int provinceId = cursor.getInt(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.PROVINCE_ID));
            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.DATE));
            int maxPeople = cursor.getInt(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.MAX_PEOPLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.DESCRIPTION));

            // Tambahkan di bawah description:
            String category = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.CATEGORY));

            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.CREATED_AT));
            String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.UPDATED_AT));

            LocalDate activityDate = sanitizeActivityDate(dateString);
            Timestamp createdAtTimestamp = toTimestamp(createdAt);
            Timestamp updatedAtTimestamp = toTimestamp(updatedAt);

            // Pastikan konstruktor Activity menerima parameter category
            return new Activity(
                    id,
                    organizerId,
                    image,
                    title,
                    address,
                    cityId,
                    provinceId,
                    activityDate,
                    maxPeople,
                    description,
                    category, // <-- TAMBAHKAN DI SINI
                    createdAtTimestamp,
                    updatedAtTimestamp
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}