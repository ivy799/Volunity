package com.example.volunity.Database_config.City;

import android.provider.BaseColumns;

public class CityDBContract {
    public static String TABLE_NAME = "cities";

    public static final class CityColumns implements BaseColumns {
        public static String PROVINCE_ID = "province_id";
        public static String NAME = "name";
    }
}
