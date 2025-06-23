package com.example.volunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.database.Cursor; // Penting: Import Cursor

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.volunity.Activities.DetailActivity;
import com.example.volunity.Database_config.City.CityDBContract;     // Penting: Import CityDBContract
import com.example.volunity.Database_config.City.CityHelper;         // Penting: Import CityHelper
import com.example.volunity.Database_config.Province.ProvinceDBContract; // Penting: Import ProvinceDBContract
import com.example.volunity.Database_config.Province.ProvinceHelper; // Penting: Import ProvinceHelper
import com.example.volunity.Models.Activity;
import com.example.volunity.R;
import com.example.volunity.databinding.ItemActivityBinding;
import com.example.volunity.databinding.ItemActivityOrgBinding;
import com.example.volunity.uihelper.UiHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private ArrayList<Activity> activityList = new ArrayList<>();
    private final Context context;

    // DEFINISIKAN FORMATTER TANGGAL
    private final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID")); // Ditambah 'yyyy' untuk tahun

    // Deklarasikan Helper untuk City dan Province
    private final CityHelper cityHelper;
    private final ProvinceHelper provinceHelper;


    // Ubah konstruktor untuk menerima helper
    public ActivityAdapter(Context context, CityHelper cityHelper, ProvinceHelper provinceHelper) {
        this.context = context;
        this.cityHelper = cityHelper;
        this.provinceHelper = provinceHelper;
    }

    public void setData(ArrayList<Activity> list) {
        activityList.clear();
        activityList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemActivityOrgBinding binding = ItemActivityOrgBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ViewHolder holder, int position) {
        Activity activity = activityList.get(position);

        holder.binding.tvId.setText("ID: " + activity.getId());
        holder.binding.tvTitle.setText("Judul: " + activity.getTitle());

        // Memuat gambar menggunakan Glide
        Uri imageUri = Uri.parse(activity.getImage());
        if (imageUri != null) {
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.pic_preview)
                    .error(R.drawable.pic_preview)
                    .into(holder.binding.tvImage);
        } else {
            holder.binding.tvImage.setImageResource(R.drawable.pic_preview);
        }

        // Penanganan tanggal
        if (activity.getDate() != null) {
            holder.binding.tvDate.setText("Waktu : " + activity.getDate().format(DISPLAY_DATE_FORMATTER));
        } else {
            holder.binding.tvDate.setText("Tanggal tidak tersedia");
            Log.w("ActivityAdapter", "Tanggal aktivitas null untuk ID: " + activity.getId());
        }

        // --- SOLUSI LANGSUNG UNTUK LOKASI ---
        String cityName = "N/A";
        String provinceName = "N/A";

        // Pastikan helper terbuka sebelum melakukan kueri
        // Ini diasumsikan helper sudah dibuka di Activity/Fragment yang membuat adapter
        // Jika tidak, Anda perlu memanggil cityHelper.open() dan provinceHelper.open() di sini
        // Namun, ini bisa memperlambat performa
        if (!cityHelper.isOpen()) {
            Log.e("ActivityAdapter", "CityHelper is not open. Attempting to open.");
            cityHelper.open(); // Hati-hati: Membuka DB di onBindViewHolder bisa jadi overhead
        }
        if (!provinceHelper.isOpen()) {
            Log.e("ActivityAdapter", "ProvinceHelper is not open. Attempting to open.");
            provinceHelper.open(); // Hati-hati: Membuka DB di onBindViewHolder bisa jadi overhead
        }


        // Ambil nama kota
        try (Cursor cityCursor = cityHelper.search(activity.getCityId())) {
            if (cityCursor != null && cityCursor.moveToFirst()) {
                cityName = cityCursor.getString(cityCursor.getColumnIndexOrThrow(CityDBContract.CityColumns.NAME));
            } else {
                Log.w("ActivityAdapter", "City not found for ID: " + activity.getCityId());
            }
        } catch (Exception e) {
            Log.e("ActivityAdapter", "Error fetching city name for ID " + activity.getCityId() + ": " + e.getMessage());
        }

        // Ambil nama provinsi
        try (Cursor provinceCursor = provinceHelper.search(activity.getProvinceId())) {
            if (provinceCursor != null && provinceCursor.moveToFirst()) {
                provinceName = provinceCursor.getString(provinceCursor.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns.NAME));
            } else {
                Log.w("ActivityAdapter", "Province not found for ID: " + activity.getProvinceId());
            }
        } catch (Exception e) {
            Log.e("ActivityAdapter", "Error fetching province name for ID " + activity.getProvinceId() + ": " + e.getMessage());
        }

        holder.binding.tvLocate.setText("Lokasi: " + cityName + ", " + provinceName);


        holder.binding.tvActivityItem2.setOnClickListener(v -> {
            UiHelper.applyiOSButtonAnimation(holder.binding.tvActivityItem2, () -> {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("activity_id", String.valueOf(activity.getId()));
                context.startActivity(intent);
            });
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemActivityOrgBinding binding;

        public ViewHolder(ItemActivityOrgBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}