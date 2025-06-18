package com.example.volunity.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "VolunityPrefs";
    private static final String KEY_FIRST_TIME = "isFirstTime";
    private static final String KEY_USER_LOGGED_IN = "isUserLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ROLE = "userRole";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(KEY_FIRST_TIME, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setUserLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_USER_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_USER_LOGGED_IN, false);
    }

    public void saveUserData(String name, String email, String role) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "User");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "volunteer");
    }

    public void clearUserData() {
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_ROLE);
        editor.apply();
    }

    public void clearPreferences() {
        editor.clear();
        editor.apply();
    }
}