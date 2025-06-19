package com.example.volunity.Database_config.Activity; // Adjust package as needed

import android.database.Cursor;

import com.example.volunity.Models.Activity; // Assuming your Activity model is in this package
import com.example.volunity.Database_config.Activity.ActivityDBContract; // Import your ActivityDBContract

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActivityMappingHelper {

    /**
     * Maps a Cursor object containing activity data to an ArrayList of Activity objects.
     *
     * @param cursor The Cursor containing the activity data retrieved from the database.
     * @return An ArrayList of Activity objects.
     * @throws ParseException If there's an issue parsing the date string from the database.
     */
    public static ArrayList<Activity> mapCursorToArrayList(Cursor cursor) throws ParseException {
        ArrayList<Activity> activities = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Or whatever date format you use
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        while (cursor.moveToNext()) {
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
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.CREATED_AT)); // As string
            String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.UPDATED_AT)); // As string

            LocalDateTime activityDate = null;
            if (dateString != null) {
                activityDate = LocalDateTime.parse(dateString, formatter);
            }

            Timestamp createdAtTimestamp = null;
            Timestamp updatedAtTimestamp = null;

            if (createdAt != null) {
                createdAtTimestamp = Timestamp.valueOf(createdAt);
            }

            if (updatedAt != null) {
                updatedAtTimestamp = Timestamp.valueOf(updatedAt);
            }


            activities.add(new Activity(
                    id,
                    organizerId,
                    image,
                    title,
                    address,
                    cityId,
                    provinceId,
                    activityDate, // Pass as Date object
                    maxPeople,
                    description,
                    createdAtTimestamp,
                    updatedAtTimestamp
            ));
        }
        return activities;
    }

    /**
     * Maps a Cursor object to a single Activity object.
     * Use this if you expect only one activity record (e.g., when searching by ID).
     *
     * @param cursor The Cursor containing the activity data.
     * @return An Activity object if a record is found, or null if the cursor is empty.
     * @throws ParseException If there's an issue parsing the date string from the database.
     */
    public static Activity mapCursorToObject(Cursor cursor) throws ParseException {
        Activity activity = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Or whatever date format you use

        if (cursor.moveToFirst()) {
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
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.CREATED_AT));
            String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.UPDATED_AT));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


            LocalDateTime activityDate = null;
            if (dateString != null) {
                activityDate = LocalDateTime.parse(dateString, formatter);
            }

            Timestamp createdAtTimestamp = null;
            Timestamp updatedAtTimestamp = null;

            if (createdAt != null) {
                createdAtTimestamp = Timestamp.valueOf(createdAt);
            }

            if (updatedAt != null) {
                updatedAtTimestamp = Timestamp.valueOf(updatedAt);
            }


            activity = new Activity(
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
                    createdAtTimestamp,
                    updatedAtTimestamp
            );
        }
        return activity;
    }
}