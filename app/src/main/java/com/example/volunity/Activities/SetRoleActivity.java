package com.example.volunity.Activities;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;

import androidx.core.graphics.Insets;

import androidx.core.view.ViewCompat;

import androidx.core.view.WindowInsetsCompat;

import com.example.volunity.R;

public class SetRoleActivity extends AppCompatActivity {

    private LinearLayout cardVolunteer, cardOrganizer;

    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_role);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardVolunteer = findViewById(R.id.card_volunteer);
        cardOrganizer = findViewById(R.id.card_organizer);
        tvLoginLink = findViewById(R.id.tv_login_link);

        // Set click listener untuk card Relawan
        cardVolunteer.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                // Arahkan ke RegisterActivity dengan role Volunteer
                Intent intent = new Intent(SetRoleActivity.this, RegisterActivity.class);
                intent.putExtra("SELECTED_ROLE", "Volunteer");
                startActivity(intent);
            }
        });

        // Set click listener untuk card Penyelenggara
        cardOrganizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arahkan ke RegisterActivity dengan role Organizer
                Intent intent = new Intent(SetRoleActivity.this, RegisterActivity.class);
                intent.putExtra("SELECTED_ROLE", "Organizer");
                startActivity(intent);
            }
        });

        // Set click listener untuk link login
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembali ke LoginActivity
                Intent intent = new Intent(SetRoleActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Kembali ke LoginActivity saat tombol back ditekan
        Intent intent = new Intent(SetRoleActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}