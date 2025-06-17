package com.example.volunity.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.volunity.Database_config.User.UserHelper;
import com.example.volunity.Database_config.User.UserMappingHelper;
import com.example.volunity.Models.User;
import com.example.volunity.databinding.ActivityDetailProfileBinding; // binding class otomatis

public class DetailProfileActivity extends AppCompatActivity {
    private ActivityDetailProfileBinding binding;
    private UserHelper userHelper;
    @Nullable
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userHelper = UserHelper.getInstance(this);
        setupView();        // ambil ID, load user
        setupListeners();   // pasang listener (jika ada)
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (userHelper != null) userHelper.open();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (userHelper != null) userHelper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userHelper != null && userHelper.isOpen()) userHelper.close();
        binding = null; // hindari memory‑leak
    }
    private void setupView() {
        int userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId != -1) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(this,
                    "Gagal memuat profil: ID pengguna tidak ditemukan.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }
    private void setupListeners() {
        // Contoh placeholder:
        // binding.btnEdit.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
    }

    private void loadUserProfile(int userId) {
        Cursor cursor = null;
        try {
            userHelper.open();
            cursor = userHelper.search(userId);
            if (cursor != null) {
                currentUser = UserMappingHelper.mapCursorToObject(cursor);
            }
        } finally {
            if (cursor != null) cursor.close();
            userHelper.close();
        }

        if (currentUser != null) {
            populateUserData(currentUser);
        } else {
            Toast.makeText(this,
                    "Pengguna tidak ditemukan di database.",
                    Toast.LENGTH_LONG).show();
            populateUserData(null); // tampilkan N/A
        }
    }
    private void populateUserData(@Nullable User user) {
        if (user == null) {
            binding.tvDetailUsername.setText("N/A");
            binding.tvDetailEmail.setText("N/A");
            // binding.tvDetailPhone.setText("");
            return;
        }
        binding.tvDetailUsername.setText(user.getName());
        binding.tvDetailEmail.setText(user.getEmail());
        binding.tvUsername.setText(user.getName());
        binding.tvMail.setText(user.getEmail());
        binding.tvPhoneNumber.setText(user.getPhone_number());
    }
}
