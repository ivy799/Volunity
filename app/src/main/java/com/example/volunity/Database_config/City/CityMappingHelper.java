package com.example.volunity.Database_config.City; // Adjust package as needed

import android.database.Cursor;

import com.example.volunity.Models.City; // Assuming your City model is in this package
import com.example.volunity.Database_config.City.CityDBContract; // Import your CityDBContract

import java.util.ArrayList;

public class CityMappingHelper {

    /**
     * Maps a Cursor object containing city data to an ArrayList of City objects.
     *
     * @param cursor The Cursor containing the city data retrieved from the database.
     * @return An ArrayList of City objects.
     */
    public static ArrayList<City> mapCursorToArrayList(Cursor cursor) {
        ArrayList<City> cities = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(CityDBContract.CityColumns._ID));
            int provinceId = cursor.getInt(cursor.getColumnIndexOrThrow(CityDBContract.CityColumns.PROVINCE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(CityDBContract.CityColumns.NAME));

            cities.add(new City(
                    id,
                    name,
                    provinceId

            ));
        }
        return cities;
    }

    /**
     * Maps a Cursor object to a single City object.
     * Use this if you expect only one city record (e.g., when searching by ID).
     *
     * @param cursor The Cursor containing the city data.
     * @return A City object if a record is found, or null if the cursor is empty.
     */
    public static City mapCursorToObject(Cursor cursor) {
        City city = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(CityDBContract.CityColumns._ID));
            int provinceId = cursor.getInt(cursor.getColumnIndexOrThrow(CityDBContract.CityColumns.PROVINCE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(CityDBContract.CityColumns.NAME));

            city = new City(
                    id,
                    name,
                    provinceId
            );
        }
        return city;
    }
}