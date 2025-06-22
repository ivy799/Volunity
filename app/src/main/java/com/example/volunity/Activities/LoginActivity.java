package com.example.volunity.Activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
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
import com.example.volunity.Database_config.User.UserMappingHelper;
import com.example.volunity.Models.User;

import org.mindrot.jbcrypt.BCrypt;
import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private TextView btnLogin;
    private TextView tvRegisterLink;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;

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

        userHelper = UserHelper.getInstance(this);

        etLoginEmail = findViewById(R.id.et_login_email);
        etLoginPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);

        ivTogglePassword = findViewById(R.id.iv_toggle_password);

        ivTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    etLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.ic_eye);
                } else {
                    etLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
                }
                isPasswordVisible = !isPasswordVisible;
                etLoginPassword.setSelection(etLoginPassword.getText().length());
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Arahkan ke SetRoleActivity terlebih dahulu
                Intent intent = new Intent(LoginActivity.this, SetRoleActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performLogin() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

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

        User user = null;
        Cursor cursor = null;
        try {
            userHelper.open();
            cursor = userHelper.queryByEmail(email);

            if (cursor != null && cursor.moveToFirst()) {
                user = UserMappingHelper.mapCursorToObject(cursor);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (user != null) {
            boolean passwordMatches = BCrypt.checkpw(password, user.getPassword());

            if (passwordMatches) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                Intent intent;
                // Periksa role user
                if ("Volunteer".equalsIgnoreCase(user.getRole())) {
                    intent = new Intent(LoginActivity.this, VolunteerActivity2.class);
                } else {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                }

                intent.putExtra("USER_ID", user.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_LONG).show();
                etLoginPassword.setError("Incorrect password");
                etLoginPassword.requestFocus();
            }
        } else {
            Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_LONG).show();
            etLoginEmail.setError("Email not registered");
            etLoginEmail.requestFocus();
        }
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