package com.example.volunity.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.volunity.Activities.AddActivity;
import com.example.volunity.Adapter.ActivityAdapter;
import com.example.volunity.Database_config.Activity.ActivityHelper;
import com.example.volunity.Database_config.Activity.ActivityMappingHelper;
import com.example.volunity.Database_config.User.UserHelper;
import com.example.volunity.Database_config.User.UserMappingHelper;
import com.example.volunity.Models.Activity;
import com.example.volunity.Models.User;
import com.example.volunity.databinding.FragmentKegiatanOrganizerBinding;
import com.example.volunity.uihelper.UiHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// --- PASTIKAN IMPORT INI ADA ---
import com.example.volunity.Database_config.City.CityHelper;     // <-- Tambahkan ini
import com.example.volunity.Database_config.Province.ProvinceHelper; // <-- Tambahkan ini
// --- AKHIR PASTIKAN IMPORT INI ADA ---

import java.util.ArrayList;

public class KegiatanFragmentOrganizer extends Fragment {

    private FragmentKegiatanOrganizerBinding binding;
    private FloatingActionButton fabAddActivity;
    private ActivityHelper activityHelper;
    private UserHelper userHelper;
    private ActivityAdapter activityAdapter;

    // --- DEKLARASI HELPER BARU ---
    private CityHelper cityHelper;     // Deklarasi CityHelper
    private ProvinceHelper provinceHelper; // Deklarasi ProvinceHelper
    // --- AKHIR DEKLARASI HELPER BARU ---

    private static final String ARG_USER_ID = "user_id";
    private int currentUserId = -1; // Variabel untuk menyimpan ID pengguna yang sedang login

    public static KegiatanFragmentOrganizer newInstance(int userId) {
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        KegiatanFragmentOrganizer fragment = new KegiatanFragmentOrganizer();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentKegiatanOrganizerBinding.inflate(inflater, container, false);

        // Ambil ID pengguna dari argumen
        if (getArguments() != null) {
            currentUserId = getArguments().getInt(ARG_USER_ID, -1);
        }

        initViews();
        initDatabase();
        initListeners();
        loadUserData(); // Untuk memuat data detail pengguna, bukan aktivitasnya
        loadActivityList(); // Ini akan memuat aktivitas berdasarkan currentUserId

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // --- BUKA HELPER DI ONRESUME ---
        activityHelper.open();
        userHelper.open();     // Pastikan userHelper juga dibuka
        cityHelper.open();     // Buka CityHelper
        provinceHelper.open(); // Buka ProvinceHelper
        // --- AKHIR BUKA HELPER DI ONRESUME ---

        // Penting: Muat ulang aktivitas setiap kali fragment "aktif" kembali
        loadActivityList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // --- TUTUP HELPER DI ONDESTROYVIEW ---
        if (userHelper != null) userHelper.close();
        if (activityHelper != null) activityHelper.close();
        if (cityHelper != null) cityHelper.close();     // Tutup CityHelper
        if (provinceHelper != null) provinceHelper.close(); // Tutup ProvinceHelper
        // --- AKHIR TUTUP HELPER DI ONDESTROYVIEW ---
        binding = null;
    }

    private void initViews() {
        fabAddActivity = binding.fabAddActivity;

        // --- PERBAIKAN PENTING DI SINI ---
        // Inisialisasi CityHelper dan ProvinceHelper terlebih dahulu
        cityHelper = CityHelper.getInstance(requireContext());
        provinceHelper = ProvinceHelper.getInstance(requireContext());

        // Inisialisasi ActivityAdapter dengan passing semua parameter yang diperlukan
        activityAdapter = new ActivityAdapter(requireContext(), cityHelper, provinceHelper);
        // --- AKHIR PERBAIKAN PENTING ---

        binding.rvActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvActivities.setAdapter(activityAdapter);

        // Opsional: Tambahkan TextView di layout Anda (misal: dengan id tv_no_activities_message)
        // untuk menampilkan pesan jika tidak ada aktivitas.
        // binding.tvNoActivitiesMessage.setVisibility(View.GONE); // Sembunyikan secara default
    }

    private void initDatabase() {
        userHelper = UserHelper.getInstance(requireContext());
        // activityHelper diinisialisasi di onViewCreated
        activityHelper = ActivityHelper.getInstance(requireContext()); // Pastikan ini juga ada jika belum
        // Note: Helper dibuka di onResume, bukan di initDatabase
    }

    private void initListeners() {
        binding.fabAddActivity.setOnClickListener(v -> {
            UiHelper.applyiOSButtonAnimation(v, () -> {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                if (currentUserId != -1) {
                    intent.putExtra("USER_ID", currentUserId); // Kirim ID organizer ke AddActivity
                }
                startActivity(intent);
            });
        });
    }

    private void loadUserData() {
        if (currentUserId == -1) {
            showError("ID pengguna tidak ditemukan.");
            return;
        }
        // UserHelper akan dibuka di onResume
        if (!userHelper.isOpen()) { // Pastikan userHelper sudah dibuka sebelum digunakan
            showError("Database pengguna belum siap.");
            return;
        }
        try (Cursor cursor = userHelper.search(currentUserId)) {
            if (cursor != null && cursor.moveToFirst()) {
                User user = UserMappingHelper.mapCursorToObject(cursor);
                if (user != null) {
                    // Data pengguna berhasil dimuat. Anda bisa menampilkan info user di UI jika perlu.
                    // Contoh: binding.tvWelcomeMessage.setText("Selamat datang, " + user.getName());
                } else {
                    showError("Detail pengguna tidak ditemukan.");
                }
            } else {
                showError("Pengguna tidak ditemukan di database.");
            }
        }
    }

    private void loadActivityList() {
        // ActivityHelper akan dibuka di onResume
        if (!activityHelper.isOpen()) { // Pastikan activityHelper sudah dibuka sebelum digunakan
            showError("Database aktivitas belum siap.");
            return;
        }
        if (currentUserId == -1) {
            showError("ID organizer tidak valid untuk memuat aktivitas.");
            activityAdapter.setData(new ArrayList<>()); // Kosongkan daftar jika ID tidak valid
            return;
        }

        try (Cursor cursor = activityHelper.queryByOrganizerId(currentUserId)) {
            ArrayList<Activity> list = ActivityMappingHelper.mapCursorToArrayList(cursor);
            activityAdapter.setData(list);

            if (list.isEmpty()) {
                Toast.makeText(requireContext(), "Belum ada kegiatan yang Anda buat.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            showError("Gagal memuat daftar aktivitas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}