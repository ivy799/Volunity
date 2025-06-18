package com.example.volunity.Activities.Landing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.volunity.R;

public class Landing2Activity extends AppCompatActivity {

    private ImageView btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing2);

        // Hide action bar for fullscreen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void setupClickListeners() {
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Landing Page 3
                Intent intent = new Intent(Landing2Activity.this, Landing3Activity.class);
                startActivity(intent);
            }
        });
    }

}