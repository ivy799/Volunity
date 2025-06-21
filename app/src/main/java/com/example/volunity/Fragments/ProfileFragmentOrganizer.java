package com.example.volunity.Fragments;

import android.content.Intent; // Import untuk Intent
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.volunity.Activities.LoginActivity;
import com.example.volunity.Database_config.User.UserHelper;
import com.example.volunity.Database_config.User.UserMappingHelper;
import com.example.volunity.Models.User;
import com.example.volunity.R;
import com.example.volunity.databinding.FragmentProfileOrganizerBinding;
import com.example.volunity.uihelper.UiHelper;
import com.example.volunity.Activities.DetailProfileActivity; // Import DetailProfileActivity

public class ProfileFragmentOrganizer extends Fragment {

    private FragmentProfileOrganizerBinding binding;
    private UserHelper userHelper;
    // uiHelper tidak perlu dideklarasikan sebagai anggota kelas jika metode-metodenya statis,
    // tapi tidak masalah jika Anda ingin mendeklarasikannya.
    // private UiHelper uiHelper; // Tidak diperlukan jika semua method di UiHelper statis

    private static final String ARG_USER_ID = "user_id";

    public ProfileFragmentOrganizer() {
        // Required empty public constructor
    }

    public static ProfileFragmentOrganizer newInstance(int userId) {
        ProfileFragmentOrganizer fragment = new ProfileFragmentOrganizer();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileOrganizerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // userHelper sudah diinisialisasi
        userHelper = UserHelper.getInstance(getContext());
        userHelper.open();

        // Panggil method untuk menampilkan data pengguna
        displayUserData();

        // --- KODE BARU: Mengatur Logout pada card_logout ---
        binding.cardLogout.setOnClickListener(v -> {
            UiHelper.applyiOSButtonAnimation(v, () -> {
                // 1. Hapus data login jika Anda menyimpan session di SharedPreferences, tambahkan di sini
                // Contoh: getContext().getSharedPreferences("login_session", Context.MODE_PRIVATE).edit().clear().apply();

                // 2. Arahkan user ke MainActivity (atau LoginActivity jika ada)
                Intent intent = new Intent(getActivity(), LoginActivity.class); // Ganti ke LoginActivity jika ada
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Hapus backstack
                startActivity(intent);

                // 3. Optional: Tutup fragment/activity ini
                requireActivity().finish();
            });
        });

        // --- KODE BARU: Mengatur OnClickListener untuk card_detail ---
        binding.cardDetail.setOnClickListener(v -> {
            // Menerapkan animasi iOS-style
            UiHelper.applyiOSButtonAnimation(v, () -> {
                // Aksi setelah animasi selesai: Pindah ke DetailProfileActivity
                Intent intent = new Intent(getActivity(), DetailProfileActivity.class);
                // Opsional: Teruskan ID pengguna ke DetailProfileActivity jika diperlukan
                int userId = -1;
                if (getArguments() != null) {
                    userId = getArguments().getInt(ARG_USER_ID, -1);
                }
                if (userId != -1) {
                    intent.putExtra("USER_ID", userId);
                }
                startActivity(intent);
            });
        });
        // --- AKHIR KODE BARU ---

        return root;
    }

    private void displayUserData() {
        int userId = -1;

        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_USER_ID, -1);
        }

        if (userId == -1) {
            binding.textUserName.setText("ID Pengguna tidak ditemukan.");
            binding.textUserEmail.setText("Tidak dapat memuat data.");
            return;
        }

        if (userHelper.isOpen()) {
            Cursor cursor = userHelper.search(userId);

            if (cursor != null) {
                User user = UserMappingHelper.mapCursorToObject(cursor);

                if (user != null) {
                    if ("organizer".equalsIgnoreCase(user.getRole())) {
                        binding.textUserName.setText(user.getName());
                        binding.textUserEmail.setText(user.getEmail());
                    } else {
                        binding.textUserName.setText("Akses Ditolak: Bukan Organizer");
                        binding.textUserEmail.setText("Role tidak sesuai.");
                    }
                } else {
                    binding.textUserName.setText("User tidak ditemukan.");
                    binding.textUserEmail.setText("");
                }
                cursor.close();
            }
        } else {
            binding.textUserName.setText("Database tidak siap.");
            binding.textUserEmail.setText("Coba lagi nanti.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userHelper != null) {
            userHelper.close();
        }
        binding = null;
    }
}