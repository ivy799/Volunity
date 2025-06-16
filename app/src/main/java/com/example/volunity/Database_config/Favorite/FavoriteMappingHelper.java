package com.example.volunity.Database_config.Favorite; // Adjust package as needed

import android.database.Cursor;

import com.example.volunity.Models.Favorite; // Assuming your Favorite model is in this package
import com.example.volunity.Database_config.Favorite.FavoriteDBContract; // Import your FavoriteDBContract

import java.util.ArrayList;

public class FavoriteMappingHelper {

    /**
     * Maps a Cursor object containing favorite data to an ArrayList of Favorite objects.
     *
     * @param cursor The Cursor containing the favorite data retrieved from the database.
     * @return An ArrayList of Favorite objects.
     */
    public static ArrayList<Favorite> mapCursorToArrayList(Cursor cursor) {
        ArrayList<Favorite> favorites = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDBContract.FavoriteColumns._ID));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDBContract.FavoriteColumns.USER_ID));
            int activityId = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDBContract.FavoriteColumns.ACTIVITIES_ID));

            favorites.add(new Favorite(
                    id,
                    userId,
                    activityId
            ));
        }
        return favorites;
    }

    /**
     * Maps a Cursor object to a single Favorite object.
     * Use this if you expect only one favorite record (e.g., when searching by ID).
     *
     * @param cursor The Cursor containing the favorite data.
     * @return A Favorite object if a record is found, or null if the cursor is empty.
     */
    public static Favorite mapCursorToObject(Cursor cursor) {
        Favorite favorite = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDBContract.FavoriteColumns._ID));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDBContract.FavoriteColumns.USER_ID));
            int activityId = cursor.getInt(cursor.getColumnIndexOrThrow(FavoriteDBContract.FavoriteColumns.ACTIVITIES_ID));

            favorite = new Favorite(
                    id,
                    userId,
                    activityId
            );
        }
        return favorite;
    }
}