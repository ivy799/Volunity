package com.example.volunity.Database_config.Registration_form;

import android.provider.BaseColumns;

public class RegistrationFormDBContract {
    public static String TABLE_NAME = "registration_forms";

    public static final class RegistrationFormColumns implements BaseColumns {
        public static String USER_ID = "user_id";
        public static String ACTIVITY_ID = "activity_id";
        public static String ADDRESS = "address";
        public static String CITY_ID = "city_id";
        public static String PROVINCE_ID = "province_id";
        public static String REASONS = "reasons";
        public static String EXPERIENCES = "experiences";
        public static String STATUS = "status";
        public static String CREATED_AT = "created_at";
    }
}
