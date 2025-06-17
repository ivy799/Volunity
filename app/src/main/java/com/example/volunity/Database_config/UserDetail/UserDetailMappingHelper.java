package com.example.volunity.Database_config.UserDetail; // Sesuaikan package jika berbeda

import android.database.Cursor;

import com.example.volunity.Models.UserDetail; // Asumsikan model UserDetail ada di package ini
import com.example.volunity.Database_config.UserDetail.UserDetailDBContract; // Import UserDetailDBContract

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale; // Penting untuk SimpleDateFormat


public class UserDetailMappingHelper {

    // Format tanggal yang digunakan untuk menyimpan dan mengambil DateOfBirth
    // Pastikan ini cocok dengan format TEXT yang Anda gunakan di SQLite (misal: "YYYY-MM-DD")
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    /**
     * Memetakan objek Cursor yang berisi data detail pengguna ke ArrayList objek UserDetail.
     *
     * @param cursor Cursor yang berisi data detail pengguna yang diambil dari database.
     * @return ArrayList objek UserDetail.
     * @throws ParseException Jika ada masalah saat mengurai string tanggal dari database.
     */
    public static ArrayList<UserDetail> mapCursorToArrayList(Cursor cursor) throws ParseException {
        ArrayList<UserDetail> userDetails = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums._ID));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.USER_ID));
            int cityId = cursor.getInt(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.CITY_ID));
            int provinceId = cursor.getInt(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.PROVINCE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.NAME));
            String photoProfile = cursor.getString(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.PHOTO_PROFILE));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.GENDER));
            String dateOfBirthString = cursor.getString(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.DATE_OF_BIRTH));

            Date dateOfBirth = null;
            if (dateOfBirthString != null && !dateOfBirthString.isEmpty()) {
                dateOfBirth = DATE_FORMATTER.parse(dateOfBirthString);
            }

            userDetails.add(new UserDetail(
                    id,
                    userId,
                    cityId,
                    provinceId,
                    name,
                    photoProfile,
                    gender,
                    dateOfBirth // Lewatkan sebagai objek Date
            ));
        }
        return userDetails;
    }

    /**
     * Memetakan objek Cursor ke satu objek UserDetail.
     * Gunakan ini jika Anda hanya mengharapkan satu record detail pengguna (misalnya, saat mencari berdasarkan ID atau User ID).
     *
     * @param cursor Cursor yang berisi data detail pengguna.
     * @return Objek UserDetail jika record ditemukan, atau null jika cursor kosong.
     * @throws ParseException Jika ada masalah saat mengurai string tanggal dari database.
     */
    public static UserDetail mapCursorToObject(Cursor cursor) throws ParseException {
        UserDetail userDetail = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums._ID));
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.USER_ID));
            int cityId = cursor.getInt(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.CITY_ID));
            int provinceId = cursor.getInt(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.PROVINCE_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.NAME));
            String photoProfile = cursor.getString(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.PHOTO_PROFILE));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.GENDER));
            String dateOfBirthString = cursor.getString(cursor.getColumnIndexOrThrow(UserDetailDBContract.UserDetailColums.DATE_OF_BIRTH));

            Date dateOfBirth = null;
            if (dateOfBirthString != null && !dateOfBirthString.isEmpty()) {
                dateOfBirth = DATE_FORMATTER.parse(dateOfBirthString);
            }

            userDetail = new UserDetail(
                    id,
                    userId,
                    cityId,
                    provinceId,
                    name,
                    photoProfile,
                    gender,
                    dateOfBirth
            );
        }
        return userDetail;
    }
}