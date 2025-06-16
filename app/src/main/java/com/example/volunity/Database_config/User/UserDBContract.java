package com.example.volunity.Database_config.User;

import android.provider.BaseColumns;

public class UserDBContract {
    public static String TABLE_NAME = "users";

    public static final class UserColumns implements BaseColumns {
        public static String USERNAME = "username";
        public static String EMAIL = "email";
        public static String PHONE_NUMBER = "phone_number";
        public static String PASSWORD = "password";
        public static String ROLE = "role";
        public static String CREATED_AT = "created_at";
    }
}
