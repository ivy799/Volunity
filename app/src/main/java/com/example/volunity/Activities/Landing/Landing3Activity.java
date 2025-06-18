package com.example.volunity.Activities.Landing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.volunity.Activities.LoginActivity;
import com.example.volunity.Activities.RegisterActivity;
import com.example.volunity.Activities.SetRoleActivity;
import com.example.volunity.R;

public class Landing3Activity extends AppCompatActivity {

    private TextView btnLogin;
    private TextView btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing3);

        // Hide action bar for fullscreen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Login Activity
                Intent intent = new Intent(Landing3Activity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Register Activity
                Intent intent = new Intent(Landing3Activity.this, SetRoleActivity.class);
                startActivity(intent);
            }
        });
    }
}