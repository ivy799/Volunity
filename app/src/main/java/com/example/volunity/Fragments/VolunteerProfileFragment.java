package com.example.volunity.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.volunity.Activities.LoginActivity; // Ganti dengan path LoginActivity kamu
import com.example.volunity.R;

public class VolunteerProfileFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private int userId;

    public VolunteerProfileFragment() {}

    public static VolunteerProfileFragment newInstance(int userId) {
        VolunteerProfileFragment fragment = new VolunteerProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_profile, container, false);

        // LOGOUT LOGIC
        LinearLayout cardLogout = view.findViewById(R.id.card_logout);
        cardLogout.setOnClickListener(v -> {
            // Bersihkan session/login state (SharedPreferences atau lainnya)
            requireActivity()
                    .getSharedPreferences("user_session", getContext().MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Toast.makeText(getContext(), "Sukses logout", Toast.LENGTH_SHORT).show();

            // Kembali ke LoginActivity dan tutup semua activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}