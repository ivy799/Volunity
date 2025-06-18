package com.example.volunity.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.mindrot.jbcrypt.BCrypt;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhoneNumber, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private TextView btnRegister;
    private TextView tvLoginLink;

    private ImageView ivTogglePassword, ivToggleConfirmPassword;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private UserHelper userHelper;
    private String selectedRole;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userHelper = UserHelper.getInstance(this);

        // Ambil role yang dipilih dari SetRoleActivity
        selectedRole = getIntent().getStringExtra("SELECTED_ROLE");
        if (selectedRole == null) {
            selectedRole = "Volunteer"; // Default role jika tidak ada
        }

        // Untuk set welcoming text:
        TextView tvWelcoming = findViewById(R.id.welcoming_text);
        if ("Volunteer".equalsIgnoreCase(selectedRole)) {
            tvWelcoming.setText("Gabung jadi Relawan!");
        } else if ("Organizer".equalsIgnoreCase(selectedRole)) {
            tvWelcoming.setText("Daftarkan Kegiatanmu!");
        } else {
            tvWelcoming.setText("Selamat Datang!");
        }

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        cbTerms = findViewById(R.id.cb_terms);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        // Eye icon
        ivTogglePassword = findViewById(R.id.iv_toggle_password);
        ivToggleConfirmPassword = findViewById(R.id.iv_toggle_confirm_password);

        ivTogglePassword.setOnClickListener(view -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        ivToggleConfirmPassword.setOnClickListener(view -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnRegister.setOnClickListener(v -> registerUser());

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Input Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Nama is required");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Nomor telepon is required");
            etPhoneNumber.requestFocus();
            return;
        }
        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            etPhoneNumber.setError("Enter a valid phone number");
            etPhoneNumber.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm password is required");
            etConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Anda harus setuju dengan syarat dan ketentuan", Toast.LENGTH_LONG).show();
            return;
        }

        // Hash password menggunakan BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Prepare ContentValues for insertion
        ContentValues values = new ContentValues();
        values.put(UserDBContract.UserColumns.USERNAME, name);
        values.put(UserDBContract.UserColumns.EMAIL, email);
        values.put(UserDBContract.UserColumns.PHONE_NUMBER, phoneNumber);
        values.put(UserDBContract.UserColumns.PASSWORD, hashedPassword);
        values.put(UserDBContract.UserColumns.ROLE, selectedRole); // Gunakan role yang dipilih
        values.put(UserDBContract.UserColumns.CREATED_AT, LocalDateTime.now().format(TIMESTAMP_FORMATTER));

        // Insert data into database using UserHelper
        userHelper.open();
        long result = userHelper.insert(values);

        if (result > 0) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Email might already be registered.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Kembali ke SetRoleActivity saat tombol back ditekan
        Intent intent = new Intent(RegisterActivity.this, SetRoleActivity.class);
        startActivity(intent);
        finish();
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userHelper != null && userHelper.isOpen()) {
            userHelper.close();
        }
    }
}