package com.example.volunity.Database_config.Province; // Adjust package as needed

import android.database.Cursor;

import com.example.volunity.Models.Province; // Assuming your Province model is in this package
import com.example.volunity.Database_config.Province.ProvinceDBContract; // Import your ProvinceDBContract

import java.util.ArrayList;

public class ProvinceMappingHelper {

    /**
     * Maps a Cursor object containing province data to an ArrayList of Province objects.
     *
     * @param cursor The Cursor containing the province data retrieved from the database.
     * @return An ArrayList of Province objects.
     */
    public static ArrayList<Province> mapCursorToArrayList(Cursor cursor) {
        ArrayList<Province> provinces = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns.NAME));

            provinces.add(new Province(
                    id,
                    name
            ));
        }
        return provinces;
    }

    /**
     * Maps a Cursor object to a single Province object.
     * Use this if you expect only one province record (e.g., when searching by ID).
     *
     * @param cursor The Cursor containing the province data.
     * @return A Province object if a record is found, or null if the cursor is empty.
     */
    public static Province mapCursorToObject(Cursor cursor) {
        Province province = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns._ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns.NAME));

            province = new Province(
                    id,
                    name
            );
        }
        return province;
    }
}