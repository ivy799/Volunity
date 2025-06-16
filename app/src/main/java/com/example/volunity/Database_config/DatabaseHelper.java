package com.example.volunity.Database_config;

import android.database.sqlite.SQLiteOpenHelper;

import com.example.volunity.Database_config.Activity.ActivityDBContract;
import com.example.volunity.Database_config.City.CityDBContract;
import com.example.volunity.Database_config.Favorite.FavoriteDBContract;
import com.example.volunity.Database_config.Province.ProvinceDBContract;
import com.example.volunity.Database_config.Registration_form.RegistrationFormDBContract;
import com.example.volunity.Database_config.User.UserDBContract;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "volunity.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(android.content.Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String SQL_CREATE_TABLE_USERS =
            String.format(
                    "CREATE TABLE %s ("
                            + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "%s TEXT NOT NULL, "
                            + "%s TEXT NOT NULL UNIQUE, "
                            + "%s TEXT, "
                            + "%s TEXT NOT NULL, "
                            + "%s TEXT NOT NULL, "
                            + "%s TEXT DEFAULT CURRENT_TIMESTAMP)",
                    UserDBContract.TABLE_NAME,
                    UserDBContract.UserColumns._ID,
                    UserDBContract.UserColumns.USERNAME,
                    UserDBContract.UserColumns.EMAIL,
                    UserDBContract.UserColumns.PHONE_NUMBER,
                    UserDBContract.UserColumns.PASSWORD,
                    UserDBContract.UserColumns.ROLE,
                    UserDBContract.UserColumns.CREATED_AT
            );

    public static final String SQL_CREATE_TABLE_PROVINCES =
            String.format(
                    "CREATE TABLE %s ("
                            + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "%s TEXT NOT NULL UNIQUE)",
                    ProvinceDBContract.TABLE_NAME,
                    ProvinceDBContract.ProvinceColumns._ID,
                    ProvinceDBContract.ProvinceColumns.NAME
            );

    public static final String SQL_CREATE_TABLE_CITIES =
            String.format(
                    "CREATE TABLE %s ("
                            + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "%s INTEGER NOT NULL, "
                            + "%s TEXT NOT NULL UNIQUE, "
                            + "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                    CityDBContract.TABLE_NAME,
                    CityDBContract.CityColumns._ID,
                    CityDBContract.CityColumns.PROVINCE_ID,
                    CityDBContract.CityColumns.NAME,
                    CityDBContract.CityColumns.PROVINCE_ID,
                    ProvinceDBContract.TABLE_NAME,
                    ProvinceDBContract.ProvinceColumns._ID
            );

    public static final String SQL_CREATE_TABLE_ACTIVITIES =
            String.format(
                    "CREATE TABLE %s ("
                            + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "%s TEXT, "
                            + "%s TEXT NOT NULL, "
                            + "%s TEXT NOT NULL, "
                            + "%s INTEGER NOT NULL, "
                            + "%s INTEGER NOT NULL, "
                            + "%s TEXT NOT NULL, "
                            + "%s INTEGER NOT NULL, "
                            + "%s TEXT, "
                            + "%s TEXT DEFAULT CURRENT_TIMESTAMP, "
                            + "%s TEXT DEFAULT CURRENT_TIMESTAMP, "
                            + "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE, "
                            + "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                    ActivityDBContract.TABLE_NAME,
                    ActivityDBContract.ActivityColumns._ID,
                    ActivityDBContract.ActivityColumns.IMAGE,
                    ActivityDBContract.ActivityColumns.TITLE,
                    ActivityDBContract.ActivityColumns.ADDRESS,
                    ActivityDBContract.ActivityColumns.CITY_ID,
                    ActivityDBContract.ActivityColumns.PROVINCE_ID,
                    ActivityDBContract.ActivityColumns.DATE,
                    ActivityDBContract.ActivityColumns.MAX_PEOPLE,
                    ActivityDBContract.ActivityColumns.DESCRIPTION,
                    ActivityDBContract.ActivityColumns.CREATED_AT,
                    ActivityDBContract.ActivityColumns.UPDATED_AT,
                    ActivityDBContract.ActivityColumns.CITY_ID,
                    CityDBContract.TABLE_NAME,
                    CityDBContract.CityColumns._ID,
                    ActivityDBContract.ActivityColumns.PROVINCE_ID,
                    ProvinceDBContract.TABLE_NAME,
                    ProvinceDBContract.ProvinceColumns._ID
            );

    public static final String SQL_CREATE_TABLE_REGISTRATION_FORMS =
            String.format(
                    "CREATE TABLE %s ("
                            + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "%s INTEGER NOT NULL, "
                            + "%s INTEGER NOT NULL, "
                            + "%s TEXT NOT NULL, "
                            + "%s INTEGER NOT NULL, "
                            + "%s INTEGER NOT NULL, "
                            + "%s TEXT, "
                            + "%s TEXT, "
                            + "%s TEXT NOT NULL, "
                            + "%s TEXT DEFAULT CURRENT_TIMESTAMP, "
                            + "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE, "
                            + "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE, "
                            + "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE, "
                            + "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                    RegistrationFormDBContract.TABLE_NAME,
                    RegistrationFormDBContract.RegistrationFormColumns._ID,
                    RegistrationFormDBContract.RegistrationFormColumns.USER_ID,
                    RegistrationFormDBContract.RegistrationFormColumns.ACTIVITY_ID,
                    RegistrationFormDBContract.RegistrationFormColumns.ADDRESS,
                    RegistrationFormDBContract.RegistrationFormColumns.CITY_ID,
                    RegistrationFormDBContract.RegistrationFormColumns.PROVINCE_ID,
                    RegistrationFormDBContract.RegistrationFormColumns.REASONS,
                    RegistrationFormDBContract.RegistrationFormColumns.EXPERIENCES,
                    RegistrationFormDBContract.RegistrationFormColumns.STATUS,
                    RegistrationFormDBContract.RegistrationFormColumns.CREATED_AT,
                    RegistrationFormDBContract.RegistrationFormColumns.USER_ID,
                    UserDBContract.TABLE_NAME,
                    UserDBContract.UserColumns._ID,
                    RegistrationFormDBContract.RegistrationFormColumns.ACTIVITY_ID,
                    ActivityDBContract.TABLE_NAME,
                    ActivityDBContract.ActivityColumns._ID,
                    RegistrationFormDBContract.RegistrationFormColumns.CITY_ID,
                    CityDBContract.TABLE_NAME,
                    CityDBContract.CityColumns._ID,
                    RegistrationFormDBContract.RegistrationFormColumns.PROVINCE_ID,
                    ProvinceDBContract.TABLE_NAME,
                    ProvinceDBContract.ProvinceColumns._ID
            );

    public static final String SQL_CREATE_TABLE_FAVORITES =
            String.format(
                    "CREATE TABLE %s ("
                            + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "%s INTEGER NOT NULL, "
                            + "%s INTEGER NOT NULL, "
                            + "UNIQUE(%s, %s), "
                            + "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE, "
                            + "FOREIGN KEY (%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                    FavoriteDBContract.TABLE_NAME,
                    FavoriteDBContract.FavoriteColumns._ID,
                    FavoriteDBContract.FavoriteColumns.USER_ID,
                    FavoriteDBContract.FavoriteColumns.ACTIVITIES_ID,
                    FavoriteDBContract.FavoriteColumns.USER_ID,
                    FavoriteDBContract.FavoriteColumns.ACTIVITIES_ID,
                    FavoriteDBContract.FavoriteColumns.USER_ID,
                    UserDBContract.TABLE_NAME,
                    UserDBContract.UserColumns._ID,
                    FavoriteDBContract.FavoriteColumns.ACTIVITIES_ID,
                    ActivityDBContract.TABLE_NAME,
                    ActivityDBContract.ActivityColumns._ID
            );




    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USERS);
        db.execSQL(SQL_CREATE_TABLE_PROVINCES);
        db.execSQL(SQL_CREATE_TABLE_CITIES);
        db.execSQL(SQL_CREATE_TABLE_ACTIVITIES);
        db.execSQL(SQL_CREATE_TABLE_REGISTRATION_FORMS);
        db.execSQL(SQL_CREATE_TABLE_FAVORITES);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserDBContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProvinceDBContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CityDBContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ActivityDBContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RegistrationFormDBContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteDBContract.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(android.database.sqlite.SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
