package com.example.volunity.Database_config.Favorite;

import android.provider.BaseColumns;

public class FavoriteDBContract {
    public static String TABLE_NAME = "favorites";

    public static final class FavoriteColumns implements BaseColumns {
        public static String USER_ID = "user_id";
        public static String ACTIVITIES_ID = "activities_id";
    }
}
