package com.example.volunity.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.example.volunity.R;
import com.example.volunity.Database_config.User.UserDBContract;
import com.example.volunity.Database_config.User.UserHelper;
import com.example.volunity.Database_config.User.UserMappingHelper;
import com.example.volunity.Models.User; // Pastikan model User ada

import org.mindrot.jbcrypt.BCrypt; // Import BCrypt untuk verifikasi password

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;

    private UserHelper userHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi UserHelper
        userHelper = UserHelper.getInstance(this);

        // Inisialisasi komponen UI
        etLoginEmail = findViewById(R.id.et_login_email);
        etLoginPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);

        // Set OnClickListener untuk Tombol Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        // Set OnClickListener untuk Tautan Register
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigasi ke RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                // Tidak perlu finish() di sini karena pengguna mungkin ingin kembali ke LoginActivity
            }
        });
    }

    /**
     * Menangani proses login pengguna.
     * Melakukan validasi input, mencari pengguna di database,
     * dan memverifikasi password yang di-hash.
     */
    private void performLogin() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        // --- Validasi Input ---
        if (TextUtils.isEmpty(email)) {
            etLoginEmail.setError("Email is required");
            etLoginEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.setError("Enter a valid email address");
            etLoginEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etLoginPassword.setError("Password is required");
            etLoginPassword.requestFocus();
            return;
        }

        // --- Cari pengguna di database berdasarkan email ---
        // Anda perlu menambahkan metode 'queryByEmail' di UserHelper
        // Jika belum ada, Anda bisa membuatnya seperti ini di UserHelper.java:
        /*
        public Cursor queryByEmail(String email) {
            ensureOpen();
            return sqLiteDatabase.query(
                    TABLE_NAME,
                    null, // All columns
                    UserDBContract.UserColumns.EMAIL + " = ?", // Selection clause
                    new String[]{email}, // Selection argument
                    null, // No groupBy
                    null, // No having
                    null // No orderBy
            );
        }
        */

        User user = null;
        Cursor cursor = null;
        try {
            userHelper.open(); // Pastikan database terbuka
            cursor = userHelper.queryByEmail(email); // Panggil metode baru ini

            if (cursor != null && cursor.moveToFirst()) {
                // Pengguna ditemukan, map ke objek User
                user = UserMappingHelper.mapCursorToObject(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Selalu tutup cursor
            }
            userHelper.close(); // Tutup database setelah selesai operasi query
        }

        if (user != null) {
            // Pengguna ditemukan, sekarang verifikasi password
            // BCrypt.checkpw(plainTextPassword, hashedPasswordFromDatabase)
            boolean passwordMatches = BCrypt.checkpw(password, user.getPassword());

            if (passwordMatches) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                // Navigasi ke Main Activity (atau Dashboard Activity)
                // Ganti MainActivity.class dengan aktivitas utama aplikasi Anda
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Hapus semua aktivitas sebelumnya dari stack
                startActivity(intent);
                finish(); // Tutup LoginActivity
            } else {
                Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_LONG).show();
                etLoginPassword.setError("Incorrect password");
                etLoginPassword.requestFocus();
            }
        } else {
            // Pengguna tidak ditemukan
            Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_LONG).show();
            etLoginEmail.setError("Email not registered");
            etLoginEmail.requestFocus();
        }
    }

    // Manajemen siklus hidup database helper
    // Opsional, tapi disarankan jika Anda memiliki banyak interaksi DB di aktivitas ini
    @Override
    protected void onResume() {
        super.onResume();
        userHelper.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userHelper.close();
    }
}