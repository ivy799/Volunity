package com.example.volunity.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.volunity.Fragments.VolunteerFavFragment;
import com.example.volunity.Fragments.VolunteerFragment;
import com.example.volunity.Fragments.VolunteerProfileFragment;
import com.example.volunity.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VolunteerActivity2 extends AppCompatActivity {

    private int loggedInUserId = -1; // Menginisialisasi dengan nilai default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volunteer2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ambil userId dari intent jika ada
        if (getIntent().hasExtra("USER_ID")) {
            loggedInUserId = getIntent().getIntExtra("USER_ID", -1);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId(); // Ambil ID item yang dipilih

            if (itemId == R.id.home) {
                // Gunakan metode newInstance() untuk mengirim userId ke fragment
                // Anda perlu menambahkan metode newInstance() di VolunteerFragment
                selectedFragment = VolunteerFragment.newInstance(loggedInUserId);
            } else if (itemId == R.id.favorite) {
                // Gunakan metode newInstance() untuk mengirim userId ke fragment
                // Anda perlu menambahkan metode newInstance() di VolunteerFavFragment
                selectedFragment = VolunteerFavFragment.newInstance(loggedInUserId);
            } else if (itemId == R.id.profile) {
                // Gunakan metode newInstance() untuk mengirim userId ke fragment
                // Anda perlu menambahkan metode newInstance() di VolunteerProfileFragment
                selectedFragment = VolunteerProfileFragment.newInstance(loggedInUserId);
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });

        // Tampilkan fragment default saat activity dibuat
        // Pastikan USER_ID diteruskan saat pertama kali memuat fragment home
        loadFragment(VolunteerFragment.newInstance(loggedInUserId));
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        // Tambahkan addToBackStack(null) jika Anda ingin pengguna dapat menekan tombol kembali
        // untuk kembali ke fragment sebelumnya di navigasi bawah.
        // ft.addToBackStack(null);
        ft.commit();
    }
}