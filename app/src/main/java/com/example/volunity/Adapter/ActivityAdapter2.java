package com.example.volunity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.volunity.Activities.DetailActivity2; // Mengarahkan ke DetailActivity2
import com.example.volunity.Database_config.City.CityDBContract;
import com.example.volunity.Database_config.City.CityHelper;
import com.example.volunity.Database_config.Province.ProvinceDBContract;
import com.example.volunity.Database_config.Province.ProvinceHelper;
import com.example.volunity.Models.Activity;
import com.example.volunity.R;
import com.example.volunity.databinding.ItemActivityBinding;
import com.example.volunity.uihelper.UiHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // Import untuk menangani error parsing
import java.util.ArrayList;
import java.util.Locale;

public class ActivityAdapter2 extends RecyclerView.Adapter<ActivityAdapter2.ViewHolder> {

    private ArrayList<Activity> activityList = new ArrayList<>();
    private final Context context;
    // Format tanggal untuk tampilan
    private final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));

    // Deklarasikan Helper untuk City dan Province
    private final CityHelper cityHelper;
    private final ProvinceHelper provinceHelper;
    private final int loggedInUserId; // Variabel untuk menyimpan ID pengguna yang login

    // Konstruktor yang menerima userId
    public ActivityAdapter2(Context context, CityHelper cityHelper, ProvinceHelper provinceHelper, int loggedInUserId) {
        this.context = context;
        this.cityHelper = cityHelper;
        this.provinceHelper = provinceHelper;
        this.loggedInUserId = loggedInUserId; // Inisialisasi userId
    }

    public void setData(ArrayList<Activity> list) {
        activityList.clear();
        activityList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActivityAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemActivityBinding binding = ItemActivityBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter2.ViewHolder holder, int position) {
        Activity activity = activityList.get(position);

        holder.binding.tvId.setText("ID: " + activity.getId());
        holder.binding.tvTitle.setText("Judul: " + activity.getTitle());

        // Memuat gambar menggunakan Glide
        Uri imageUri = null;
        try {
            if (activity.getImage() != null && !activity.getImage().isEmpty()) {
                imageUri = Uri.parse(activity.getImage());
            }
        } catch (IllegalArgumentException e) {
            Log.e("ActivityAdapter2", "Invalid URI for activity ID " + activity.getId() + ": " + activity.getImage(), e);
            // imageUri tetap null, akan memuat placeholder
        }

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
            try {
                // Pastikan activity.getDate() adalah LocalDate sebelum memformat
                if (activity.getDate() instanceof LocalDate) {
                    holder.binding.tvDate.setText("Waktu : " + ((LocalDate) activity.getDate()).format(DISPLAY_DATE_FORMATTER));
                } else {
                    holder.binding.tvDate.setText("Tanggal tidak valid (tipe salah)");
                    Log.e("ActivityAdapter2", "Tipe data tanggal bukan LocalDate untuk ID: " + activity.getId() + ". Tipe: " + activity.getDate().getClass().getName());
                }
            } catch (DateTimeParseException e) {
                holder.binding.tvDate.setText("Tanggal tidak valid (format salah)");
                Log.e("ActivityAdapter2", "Error parsing date for display for ID " + activity.getId() + ": " + activity.getDate().toString(), e);
            }
        } else {
            holder.binding.tvDate.setText("Tanggal tidak tersedia");
            Log.w("ActivityAdapter2", "Tanggal aktivitas null untuk ID: " + activity.getId());
        }

        String cityName = "N/A";
        String provinceName = "N/A";

        // Pastikan cityHelper dan provinceHelper tidak null dan terbuka
        if (cityHelper != null) {
            if (!cityHelper.isOpen()) cityHelper.open(); // Buka jika belum terbuka
            try (Cursor cityCursor = cityHelper.search(activity.getCityId())) {
                if (cityCursor != null && cityCursor.moveToFirst()) {
                    cityName = cityCursor.getString(cityCursor.getColumnIndexOrThrow(CityDBContract.CityColumns.NAME));
                } else {
                    Log.w("ActivityAdapter2", "City not found for ID: " + activity.getCityId());
                }
            } catch (Exception e) {
                Log.e("ActivityAdapter2", "Error fetching city name for ID " + activity.getCityId() + ": " + e.getMessage());
            }
        } else {
            Log.e("ActivityAdapter2", "CityHelper is null. Cannot fetch city name.");
        }

        if (provinceHelper != null) {
            if (!provinceHelper.isOpen()) provinceHelper.open(); // Buka jika belum terbuka
            try (Cursor provinceCursor = provinceHelper.search(activity.getProvinceId())) {
                if (provinceCursor != null && provinceCursor.moveToFirst()) {
                    provinceName = provinceCursor.getString(provinceCursor.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns.NAME));
                } else {
                    Log.w("ActivityAdapter2", "Province not found for ID: " + activity.getProvinceId());
                }
            } catch (Exception e) {
                Log.e("ActivityAdapter2", "Error fetching province name for ID " + activity.getProvinceId() + ": " + e.getMessage());
            }
        } else {
            Log.e("ActivityAdapter2", "ProvinceHelper is null. Cannot fetch province name.");
        }

        holder.binding.tvLocate.setText("Lokasi: " + cityName + ", " + provinceName);

        holder.binding.tvActivityItem.setOnClickListener(v -> {
            UiHelper.applyiOSButtonAnimation(holder.binding.tvActivityItem, () -> {
                Intent intent = new Intent(context, DetailActivity2.class);
                intent.putExtra("activity_id", String.valueOf(activity.getId()));
                // Mengirim ID pengguna yang login ke DetailActivity2
                intent.putExtra("logged_in_user_id", loggedInUserId);
                context.startActivity(intent);
            });
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemActivityBinding binding;

        public ViewHolder(ItemActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}