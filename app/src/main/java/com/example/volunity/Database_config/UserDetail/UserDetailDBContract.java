package com.example.volunity.Database_config.UserDetail;

import android.provider.BaseColumns;

public class UserDetailDBContract {
    public static String TABLE_NAME = "user_details";

    public static final class UserDetailColums implements BaseColumns{
        public static String USER_ID = "user_id";
        public static String CITY_ID = "city_id";
        public static String PROVINCE_ID = "province_id";
        public static String NAME = "name";
        public static String PHOTO_PROFILE = "photo_profile";
        public static String GENDER = "gender";
        public static String DATE_OF_BIRTH = "date_of_birth";
    }
}
