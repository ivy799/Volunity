package com.example.volunity.Activities;

import android.content.Intent; // Tambahkan import ini
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


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    // --- KODE BARU UNTUK ID PENGGUNA YANG LOGIN ---
    private int loggedInUserId = -1; // Inisialisasi dengan nilai default -1 (tidak valid)
    // --- AKHIR KODE BARU ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- KODE BARU UNTUK MENGAMBIL ID PENGGUNA DARI INTENT ---
        // Ambil ID pengguna dari Intent yang mungkin dikirim dari LoginActivity
        if (getIntent().hasExtra("USER_ID")) {
            loggedInUserId = getIntent().getIntExtra("USER_ID", -1);
        }
        // --- AKHIR KODE BARU ---


        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_profile_organizer) {
                    // --- KODE BARU UNTUK MEMUAT FRAGMENT DENGAN ID ---
                    // Pastikan ID pengguna valid sebelum meneruskannya
                    if (loggedInUserId != -1) {
                        loadFragment(ProfileFragmentOrganizer.newInstance(loggedInUserId));
                    } else {
                        // Jika ID tidak valid, muat fragment tanpa ID.
                        // Fragment akan menampilkan pesan "ID Pengguna tidak ditemukan."
                        loadFragment(new ProfileFragmentOrganizer());
                    }
                    // --- AKHIR KODE BARU ---
                    return true;
                } else if (itemId == R.id.navigation_kegiatan) {
                    loadFragment(new KegiatanFragmentOrganizer());
                    return true;
                }
                return false;
            }
        });

        // Set fragment awal saat aplikasi pertama kali dibuka
        if (savedInstanceState == null) {
            // --- KODE BARU UNTUK MEMUAT FRAGMENT DENGAN ID SAAT STARTUP ---
            if (loggedInUserId != -1) {
                loadFragment(ProfileFragmentOrganizer.newInstance(loggedInUserId));
            } else {
                // Fallback jika ID pengguna tidak valid saat startup
                loadFragment(new ProfileFragmentOrganizer());
            }
            // --- AKHIR KODE BARU ---
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.fragmentContainer.getId(), fragment);
        fragmentTransaction.commit();
    }
}