package com.example.volunity.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private EditText etSearchActivity;

    // --- DEKLARASI HELPER BARU ---
    private CityHelper cityHelper;
    private ProvinceHelper provinceHelper;

    private static final String ARG_USER_ID = "user_id";
    private int currentUserId = -1; // Variabel untuk menyimpan ID pengguna yang sedang login
    private String currentUserRole = "";

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


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        activityHelper.open();
        userHelper.open();
        cityHelper.open();
        provinceHelper.open();
        loadUserData();
        loadActivityList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userHelper != null) userHelper.close();
        if (activityHelper != null) activityHelper.close();
        if (cityHelper != null) cityHelper.close();
        if (provinceHelper != null) provinceHelper.close();
        binding = null;
    }

    private void initViews() {
        fabAddActivity = binding.fabAddActivity;

        cityHelper = CityHelper.getInstance(requireContext());
        provinceHelper = ProvinceHelper.getInstance(requireContext());
        etSearchActivity = binding.etSearchActivity;


        activityAdapter = new ActivityAdapter(requireContext(), cityHelper, provinceHelper);


        binding.rvActivities.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvActivities.setAdapter(activityAdapter);

    }

    private void initDatabase() {
        userHelper = UserHelper.getInstance(requireContext());
        activityHelper = ActivityHelper.getInstance(requireContext());

    }

    private void initListeners() {
        binding.fabAddActivity.setOnClickListener(v -> {
            UiHelper.applyiOSButtonAnimation(v, () -> {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                if (currentUserId != -1) {
                    intent.putExtra("USER_ID", currentUserId);
                }
                startActivity(intent);
            });
        });

        etSearchActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak perlu
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    loadActivityList();
                } else {
                    searchActivityByName(keyword);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak perlu
            }
        });
    }

    private void loadUserData() {
        if (currentUserId == -1) {
            showError("ID pengguna tidak ditemukan.");
            return;
        }

        if (!userHelper.isOpen()) {
            showError("Database pengguna belum siap.");
            return;
        }
        try (Cursor cursor = userHelper.search(currentUserId)) {
            if (cursor != null && cursor.moveToFirst()) {
                User user = UserMappingHelper.mapCursorToObject(cursor);
                if (user != null) {
                    currentUserRole = user.getRole();
                    System.out.println(currentUserRole);
                    updateFabVisibility();
                } else {
                    showError("Detail pengguna tidak ditemukan.");
                }
            } else {
                showError("Pengguna tidak ditemukan di database.");
            }
        }
    }

    private void updateFabVisibility() {
        if ("volunteer".equalsIgnoreCase(currentUserRole)) {
            fabAddActivity.setVisibility(View.GONE);
        } else {
            fabAddActivity.setVisibility(View.VISIBLE);
        }
    }

    private void loadActivityList() {

        if (!activityHelper.isOpen()) {
            showError("Database aktivitas belum siap.");
            return;
        }
        if (currentUserId == -1) {
            showError("ID organizer tidak valid untuk memuat aktivitas.");
            activityAdapter.setData(new ArrayList<>());
            return;
        }

        Cursor cursor = null;
        try {
            if ("volunteer".equalsIgnoreCase(currentUserRole)) {
                cursor = activityHelper.queryAll();
            } else {
                cursor = activityHelper.queryByOrganizerId(currentUserId);
            }
            ArrayList<Activity> list = ActivityMappingHelper.mapCursorToArrayList(cursor);
            activityAdapter.setData(list);

            if (list.isEmpty()) {
                Toast.makeText(requireContext(), "Tidak ada kegiatan yang tersedia.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            showError("Gagal memuat daftar aktivitas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void searchActivityByName(String keyword) {
        if (!activityHelper.isOpen()) {
            showError("Database aktivitas belum siap.");
            return;
        }
        if (currentUserId == -1) {
            showError("ID organizer tidak valid untuk pencarian aktivitas.");
            activityAdapter.setData(new ArrayList<>());
            return;
        }

        try (Cursor cursor = activityHelper.searchByNameAndOrganizerId(keyword, currentUserId)) {
            ArrayList<Activity> list = ActivityMappingHelper.mapCursorToArrayList(cursor);
            activityAdapter.setData(list);

            if (list.isEmpty()) {
                Toast.makeText(requireContext(), "Tidak ditemukan kegiatan dengan nama: " + keyword, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            showError("Gagal melakukan pencarian: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}