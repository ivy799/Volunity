package com.example.volunity.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.volunity.Activities.Landing.Landing1Activity;
import com.example.volunity.Fragments.KegiatanFragmentOrganizer;
import com.example.volunity.R;
import com.example.volunity.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Make the activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        preferenceManager = new PreferenceManager(this);

        // Delay and then check user status
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserStatus();
            }
        }, SPLASH_DURATION);
    }

    private void checkUserStatus() {
        if (preferenceManager.isUserLoggedIn()) {
            String role = preferenceManager.getUserRole();
            Intent intent;
            if ("Volunteer".equalsIgnoreCase(role)) {
                intent = new Intent(this, HomeVolunteerActivity.class);
            } else {
                intent = new Intent(this, KegiatanFragmentOrganizer.class);
            }
            startActivity(intent);
        } else if (preferenceManager.isFirstTimeLaunch()) {
            // User baru -> ke Landing Page 1
            Intent intent = new Intent(SplashActivity.this, Landing1Activity.class);
            startActivity(intent);
        } else {
            // User sudah pernah lihat onboarding tapi belum login -> ke Login
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}