package com.example.volunity.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;

import com.example.volunity.Fragments.KegiatanFragmentOrganizer;
import com.example.volunity.Fragments.ProfileFragmentOrganizer;
import com.example.volunity.R;
import com.example.volunity.databinding.ActivityMainBinding;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import android.util.Log; // Tambahkan import Log untuk debugging

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int loggedInUserId = -1; // Inisialisasi dengan nilai default -1 (tidak valid)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Ambil ID pengguna dari Intent yang mungkin dikirim dari LoginActivity
        if (getIntent().hasExtra("USER_ID")) {
            loggedInUserId = getIntent().getIntExtra("USER_ID", -1);
            Log.d("MainActivity", "Logged in User ID diterima: " + loggedInUserId); // Debugging
        } else {
            Log.d("MainActivity", "Tidak ada USER_ID diterima dari Intent Login. ID default: " + loggedInUserId); // Debugging
        }

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_profile_organizer) {
                    if (loggedInUserId != -1) {
                        Log.d("MainActivity", "Memuat ProfileFragmentOrganizer dengan User ID: " + loggedInUserId);
                        loadFragment(ProfileFragmentOrganizer.newInstance(loggedInUserId));
                    } else {
                        Log.w("MainActivity", "User ID tidak valid, memuat ProfileFragmentOrganizer tanpa ID.");
                        loadFragment(new ProfileFragmentOrganizer());
                    }
                    return true;
                } else if (itemId == R.id.navigation_kegiatan) {
                    // >>> PERBAIKAN DI SINI <<<
                    // Sekarang kirim loggedInUserId ke KegiatanFragmentOrganizer
                    if (loggedInUserId != -1) {
                        Log.d("MainActivity", "Memuat KegiatanFragmentOrganizer dengan User ID: " + loggedInUserId);
                        loadFragment(KegiatanFragmentOrganizer.newInstance(loggedInUserId));
                    } else {
                        Log.w("MainActivity", "User ID tidak valid, memuat KegiatanFragmentOrganizer tanpa ID.");
                        loadFragment(new KegiatanFragmentOrganizer());
                    }
                    // >>> AKHIR PERBAIKAN <<<
                    return true;
                }
                return false;
            }
        });

        // Set fragment awal saat aplikasi pertama kali dibuka
        if (savedInstanceState == null) {
            // >>> PERBAIKAN DI SINI JUGA UNTUK FRAGMENT AWAL <<<
            // Sesuaikan dengan fragment mana yang ingin Anda tampilkan pertama kali
            // Misalnya, jika ingin menampilkan KegiatanFragmentOrganizer duluan:
            if (loggedInUserId != -1) {
                Log.d("MainActivity", "Memuat fragment awal (Kegiatan/Profile) dengan User ID: " + loggedInUserId);
                loadFragment(KegiatanFragmentOrganizer.newInstance(loggedInUserId)); // Atau ProfileFragmentOrganizer.newInstance(loggedInUserId)
            } else {
                Log.w("MainActivity", "User ID tidak valid saat startup, memuat fragment awal tanpa ID.");
                loadFragment(new KegiatanFragmentOrganizer()); // Atau new ProfileFragmentOrganizer()
            }
            // >>> AKHIR PERBAIKAN <<<
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.fragmentContainer.getId(), fragment);
        fragmentTransaction.commit();
    }
}