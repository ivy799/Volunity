package com.example.volunity.Fragments; // Pastikan nama package sesuai

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.volunity.R;
import com.example.volunity.databinding.FragmentKegiatanOrganizerBinding; // Import kelas binding yang benar

public class KegiatanFragmentOrganizer extends Fragment {

    private FragmentKegiatanOrganizerBinding binding; // Deklarasi variabel binding

    public KegiatanFragmentOrganizer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout menggunakan View Binding
        binding = FragmentKegiatanOrganizerBinding.inflate(inflater, container, false);
        View root = binding.getRoot(); // Dapatkan root view

        // Di sini Anda bisa menambahkan logika untuk menginisialisasi view di fragment_kegiatan.xml
        // Contoh: binding.otherTextView.setText("Daftar Kegiatan");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Penting: Kosongkan binding untuk mencegah memory leak
    }
}