package com.example.volunity.Database_config.Activity;

import android.provider.BaseColumns;

public class ActivityDBContract {
    public static String TABLE_NAME = "activities";

    public static final class ActivityColumns implements BaseColumns {
        public static String ORGANIZER_ID = "organizer_id";
        public static String IMAGE = "image";
        public static String TITLE = "title";
        public static String ADDRESS = "address";
        public static String CITY_ID = "city_id";
        public static String PROVINCE_ID = "province_id";
        public static String DATE = "date";
        public static String MAX_PEOPLE = "max_people";
        public static String DESCRIPTION = "description";
        public static String CREATED_AT = "created_at";
        public static String UPDATED_AT = "updated_at";
    }
}
