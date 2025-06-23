package com.example.volunity.Activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.volunity.Database_config.Activity.ActivityHelper;
import com.example.volunity.Database_config.Activity.ActivityDBContract;
import com.example.volunity.Database_config.Activity.ActivityMappingHelper;
import com.example.volunity.Database_config.City.CityHelper;
import com.example.volunity.Database_config.City.CityDBContract;
import com.example.volunity.Database_config.Province.ProvinceHelper;
import com.example.volunity.Database_config.Province.ProvinceDBContract;
import com.example.volunity.Database_config.User.UserHelper;
import com.example.volunity.Database_config.User.UserDBContract;
import com.example.volunity.Models.Activity;
import com.example.volunity.Models.City;
import com.example.volunity.Models.Province;
import com.example.volunity.Models.User;
import com.example.volunity.R;
import com.example.volunity.databinding.ActivityDetailActivityBinding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailActivity2 extends AppCompatActivity {

    private ActivityHelper activityHelper;
    private ProvinceHelper provinceHelper;
    private CityHelper cityHelper;
    private UserHelper userHelper;
    private ActivityDetailActivityBinding binding;
    private String currentActivityId;
    private boolean isEditMode = false;
    private String selectedCategory;
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categoryList;

    private List<Province> provinces;
    private List<City> cities;
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<City> cityAdapter;
    private Province selectedProvince;
    private City selectedCity;

    private int loggedInUserId;

    private final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
    private final DateTimeFormatter PARSE_DATE_FORMATTER_FOR_EDIT = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBinding();
        initHelpers();
        initDropdowns();
        openDatabases();
        loadIntentData();
        setupListeners();
        setupCategoryView();
        setEditMode(false);
        binding.btnEdit.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.btnSimpan.setVisibility(View.GONE);
    }

    private void initBinding() {
        binding = ActivityDetailActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initHelpers() {
        activityHelper = ActivityHelper.getInstance(this);
        provinceHelper = ProvinceHelper.getInstance(this);
        cityHelper = CityHelper.getInstance(this);
        userHelper = UserHelper.getInstance(this);
    }

    private void initDropdowns() {
        provinces = new ArrayList<>();
        cities = new ArrayList<>();

        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, provinces);
        binding.autoProvince.setAdapter(provinceAdapter);

        binding.autoCity.setAdapter(cityAdapter);

        binding.autoProvince.setOnItemClickListener((parent, view, position, id) -> {
            if (isEditMode) {
                selectedProvince = (Province) parent.getItemAtPosition(position);
                if (selectedProvince != null) {
                    loadCitiesForDropdown(String.valueOf(selectedProvince.getId()));
                    binding.autoCity.setText("", false);
                    selectedCity = null;
                }
            }
        });

        binding.autoCity.setOnItemClickListener((parent, view, position, longId) -> {
            if (isEditMode) {
                selectedCity = (City) parent.getItemAtPosition(position);
            }
        });
    }

    private void openDatabases() {
        activityHelper.open();
        provinceHelper.open();
        cityHelper.open();
        userHelper.open();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityHelper.open();
        provinceHelper.open();
        cityHelper.open();
        userHelper.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityHelper.close();
        provinceHelper.close();
        cityHelper.close();
        userHelper.close();
    }

    private void loadIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String activityId = extras.getString("activity_id");
            loggedInUserId = extras.getInt("logged_in_user_id", -1);

            displayLoggedInUserRole();

            if (activityId != null) {
                currentActivityId = activityId;
                loadAndDisplayActivityDetails(activityId);
            } else {
                displayError("ID Aktivitas tidak ditemukan.");
            }
        } else {
            displayError("Tidak ada data yang diterima.");
        }
    }


    private void displayLoggedInUserRole() {
        if (binding.tvLoggedInUserId != null) {
            if (loggedInUserId != -1) {
                binding.tvLoggedInUserId.setText("User ID Login: " + loggedInUserId);
            } else {
                binding.tvLoggedInUserId.setText("User ID Login: Tidak Ditemukan");
                Log.w("DetailActivity2", "Logged in user ID not found in intent (value -1).");
            }
        } else {
            Log.e("DetailActivity2", "TextView with id tv_logged_in_user_id not found in layout.");
        }

        if (binding.tvRole != null) {
            String userRole = getUserRoleById(loggedInUserId);
            if (userRole != null) {
                binding.tvRole.setText("Role: " + userRole);
            } else {
                binding.tvRole.setText("Role: Tidak Ditemukan");
                Log.w("DetailActivity2", "User role not found for ID: " + loggedInUserId);
            }
        } else {
            Log.e("DetailActivity2", "TextView with id tv_role not found in layout.");
        }
    }

    private String getUserRoleById(int userId) {
        if (!userHelper.isOpen()) {
            Log.e("DetailActivity2", "Database user belum siap saat getUserRoleById.");
            return null;
        }
        try (Cursor userCursor = userHelper.search(userId)) {
            if (userCursor != null && userCursor.moveToFirst()) {
                int roleColumnIndex = userCursor.getColumnIndex(UserDBContract.UserColumns.ROLE);
                if (roleColumnIndex != -1) {
                    return userCursor.getString(roleColumnIndex);
                } else {
                    Log.e("DetailActivity2", "Kolom ROLE tidak ditemukan di UserDBContract atau di database.");
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e("DetailActivity2", "Error getting user role for ID " + userId + ": " + e.getMessage(), e);
            return null;
        }
    }


    private void loadAndDisplayActivityDetails(String activityId) {
        if (!activityHelper.isOpen()) {
            displayError("Database aktivitas belum siap.");
            return;
        }

        try (Cursor cursor = activityHelper.search(Integer.parseInt(activityId))) {
            if (cursor != null && cursor.moveToFirst()) {
                Activity activity = ActivityMappingHelper.mapCursorToObject(cursor);

                if (activity != null) {
                    binding.tvDetailId.setText("Activity ID: " + activity.getId());
                    binding.etNamaKegiatan.setText(activity.getTitle());
                    binding.etDescription.setText(activity.getDescription());
                    binding.etCategory.setText(activity.getCategory() != null ? activity.getCategory() : "");
                    selectedCategory = activity.getCategory();

                    if (activity.getDate() != null) {
                        if (activity.getDate() instanceof LocalDate) {
                            binding.etDate.setText(((LocalDate) activity.getDate()).format(DISPLAY_DATE_FORMATTER));
                        } else {
                            binding.etDate.setText("Tanggal tidak valid");
                            Log.e("DetailActivity2", "Tipe data tanggal bukan LocalDate untuk ID: " + activityId);
                        }
                    } else {
                        binding.etDate.setText("Tanggal tidak tersedia");
                        Log.w("DetailActivity2", "Activity date is null for ID: " + activityId);
                    }

                    if (activity.getImage() != null) {
                        try {
                            Uri imageUri = Uri.parse(activity.getImage());
                            binding.etImage.setImageURI(imageUri);
                            binding.uriImage.setText("sumber: " + imageUri.toString());
                            binding.uriImage.setVisibility(android.view.View.VISIBLE);
                        } catch (Exception e) {
                            binding.etImage.setImageResource(R.drawable.pic_preview);
                            Log.e("DetailActivity2", "Error loading image: " + e.getMessage());
                        }
                    } else {
                        binding.etImage.setImageResource(R.drawable.pic_preview);
                    }

                    displayCityAndProvince(activity.getCityId(), activity.getProvinceId());
                    displayOrganizerName(activity.getOrganizerId());

                    binding.etAddress.setText(activity.getAddress());
                    if (activity.getMaxPeople() != null) {
                        binding.etMaxPeople.setText(String.valueOf(activity.getMaxPeople()));
                    } else {
                        binding.etMaxPeople.setText("Tidak terbatas");
                    }

                } else {
                    displayError("Data aktivitas tidak dapat dimuat.");
                    Log.e("DetailActivity2", "Activity object is null after mapping for ID: " + activityId);
                }
            } else {
                displayError("Aktivitas dengan ID " + activityId + " tidak ditemukan.");
                Log.w("DetailActivity2", "No activity found with ID: " + activityId);
            }
        } catch (NumberFormatException e) {
            displayError("ID Aktivitas tidak valid.");
            Log.e("DetailActivity2", "Invalid Activity ID format: " + activityId, e);
        } catch (Exception e) {
            displayError("Terjadi kesalahan saat memuat detail aktivitas: " + e.getMessage());
            Log.e("DetailActivity2", "Unexpected error loading activity details for ID: " + activityId, e);
        }
        displayLoggedInUserRole();
    }

    private void displayOrganizerName(int organizerId) {
        if (!userHelper.isOpen()) {
            Log.e("DetailActivity2", "Database user belum siap saat displayOrganizerName.");
            binding.etNamaPenyelenggara.setText("Penyelenggara tidak tersedia (DB error)");
            return;
        }
        try (Cursor userCursor = userHelper.search(organizerId)) {
            if (userCursor != null && userCursor.moveToFirst()) {
                String organizerName = userCursor.getString(userCursor.getColumnIndexOrThrow(UserDBContract.UserColumns.USERNAME));
                binding.etNamaPenyelenggara.setText(organizerName);
            } else {
                binding.etNamaPenyelenggara.setText("Penyelenggara tidak dikenal");
            }
        } catch (Exception e) {
            binding.etNamaPenyelenggara.setText("Error memuat penyelenggara");
            Log.e("DetailActivity2", "Error loading organizer name for ID " + organizerId + ": " + e.getMessage());
        }
    }

    private void displayCityAndProvince(int cityId, int provinceId) {
        if (cityHelper.isOpen()) {
            try (Cursor cityCursor = cityHelper.search(cityId)) {
                if (cityCursor != null && cityCursor.moveToFirst()) {
                    String cityName = cityCursor.getString(cityCursor.getColumnIndexOrThrow(CityDBContract.CityColumns.NAME));
                    binding.autoCity.setText(cityName);
                    selectedCity = new City(cityId, cityName, provinceId);
                } else {
                    binding.autoCity.setText("Kota tidak dikenal");
                    selectedCity = null;
                }
            } catch (Exception e) {
                binding.autoCity.setText("Error memuat kota");
                selectedCity = null;
                Log.e("DetailActivity2", "Error loading city: " + e.getMessage());
            }
        } else {
            binding.autoCity.setText("DB Kota tidak siap");
            selectedCity = null;
        }

        if (provinceHelper.isOpen()) {
            try (Cursor provinceCursor = provinceHelper.search(provinceId)) {
                if (provinceCursor != null && provinceCursor.moveToFirst()) {
                    String provinceName = provinceCursor.getString(provinceCursor.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns.NAME));
                    binding.autoProvince.setText(provinceName);
                    selectedProvince = new Province(provinceId, provinceName);
                } else {
                    binding.autoProvince.setText("Provinsi tidak dikenal");
                    selectedProvince = null;
                }
            } catch (Exception e) {
                binding.autoProvince.setText("Error memuat provinsi");
                selectedProvince = null;
                Log.e("DetailActivity2", "Error loading province: " + e.getMessage());
            }
        } else {
            binding.autoProvince.setText("DB Provinsi tidak siap");
            selectedProvince = null;
        }

        loadProvincesForDropdown();
        if (selectedProvince != null) {
            loadCitiesForDropdown(String.valueOf(selectedProvince.getId()));
        }
    }

    private void displayError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e("DetailActivity2", message);
    }

    private void setupListeners() {
        binding.arrowBack.setOnClickListener(v -> finish());

        binding.btnEdit.setOnClickListener(v -> {
            isEditMode = true;
            setEditMode(true);
            binding.btnSimpan.setText("Perbarui");
            Toast.makeText(this, "Mode Edit Diaktifkan", Toast.LENGTH_SHORT).show();
        });

        binding.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        binding.btnSimpan.setOnClickListener(v -> {
            if (isEditMode) {
                updateActivity();
            } else {
                finish();
                Toast.makeText(this, "Tekan tombol Edit untuk mengubah data.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.etDate.setOnClickListener(v -> {
            if (isEditMode) {
                LocalDate currentDisplayedDate;
                try {
                    currentDisplayedDate = LocalDate.parse(binding.etDate.getText().toString(), DISPLAY_DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    currentDisplayedDate = LocalDate.now();
                }

                new DatePickerDialog(this,
                        (pickerView, year, month, dayOfMonth) -> {
                            LocalDate newDate = LocalDate.of(year, month + 1, dayOfMonth);
                            binding.etDate.setText(newDate.format(DISPLAY_DATE_FORMATTER));
                        },
                        currentDisplayedDate.getYear(),
                        currentDisplayedDate.getMonthValue() - 1,
                        currentDisplayedDate.getDayOfMonth()
                ).show();
            }
        });

        binding.autoProvince.setOnClickListener(v -> {
            if (isEditMode) {
                binding.autoProvince.showDropDown();
            }
        });
        binding.autoCity.setOnClickListener(v -> {
            if (isEditMode) {
                binding.autoCity.showDropDown();
            }
        });
    }

    private void setEditMode(boolean enable) {
        binding.etNamaKegiatan.setEnabled(enable);
        binding.etNamaPenyelenggara.setEnabled(false);
        binding.etAddress.setEnabled(enable);
        binding.autoProvince.setEnabled(enable);
        binding.autoCity.setEnabled(enable);
        binding.etDate.setEnabled(enable);
        binding.etMaxPeople.setEnabled(enable);
        binding.etDescription.setEnabled(enable);
        binding.etCategory.setEnabled(enable);
        binding.btnSimpan.setEnabled(enable);
        binding.etImage.setClickable(enable);
    }

    private void updateActivity() {
        if (currentActivityId == null) {
            displayError("ID Aktivitas tidak ditemukan untuk diperbarui.");
            return;
        }

        String namaKegiatan = binding.etNamaKegiatan.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String dateString = binding.etDate.getText().toString().trim();
        String maxPeopleString = binding.etMaxPeople.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String category = binding.etCategory.getText().toString().trim();


        if (namaKegiatan.isEmpty() || address.isEmpty() || dateString.isEmpty() || description.isEmpty() || category.isEmpty() || selectedProvince == null || selectedCity == null) {
            Toast.makeText(this, "Semua field wajib diisi dan provinsi/kota harus dipilih!", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer maxPeople = null;
        if (!maxPeopleString.isEmpty()) {
            try {
                maxPeople = Integer.parseInt(maxPeopleString);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Jumlah Maksimum harus berupa angka.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        LocalDate activityDate = null;
        try {
            activityDate = LocalDate.parse(dateString, PARSE_DATE_FORMATTER_FOR_EDIT);
        } catch (DateTimeParseException e) {
            Toast.makeText(this, "Format tanggal tidak valid. Pastikan format 'DD Bulan YYYY'.", Toast.LENGTH_LONG).show();
            Log.e("DetailActivity2", "Date parsing error during update: " + e.getMessage());
            return;
        }

        int provinceId = selectedProvince.getId();
        int cityId = selectedCity.getId();

        Uri imageUri = null;
        if (binding.uriImage.getVisibility() == android.view.View.VISIBLE && !binding.uriImage.getText().toString().isEmpty()) {
            try {
                String uriText = binding.uriImage.getText().toString().replace("sumber: ", "");
                if (!uriText.isEmpty()) {
                    imageUri = Uri.parse(uriText);
                }
            } catch (Exception e) {
                Log.e("DetailActivity2", "Error parsing image URI from text view: " + e.getMessage());
            }
        }

        ContentValues values = new ContentValues();
        values.put(ActivityDBContract.ActivityColumns.TITLE, namaKegiatan);
        values.put(ActivityDBContract.ActivityColumns.ADDRESS, address);
        values.put(ActivityDBContract.ActivityColumns.DATE, activityDate.toString());
        values.put(ActivityDBContract.ActivityColumns.MAX_PEOPLE, maxPeople);
        values.put(ActivityDBContract.ActivityColumns.DESCRIPTION, description);
        values.put(ActivityDBContract.ActivityColumns.CATEGORY, category);
        values.put(ActivityDBContract.ActivityColumns.PROVINCE_ID, provinceId);
        values.put(ActivityDBContract.ActivityColumns.CITY_ID, cityId);
        if (imageUri != null) {
            values.put(ActivityDBContract.ActivityColumns.IMAGE, imageUri.toString());
        }
        values.put(ActivityDBContract.ActivityColumns.UPDATED_AT, LocalDateTime.now().toString());

        try {
            long result = activityHelper.update(currentActivityId, values);
            if (result > 0) {
                Toast.makeText(this, "Kegiatan berhasil diperbarui.", Toast.LENGTH_SHORT).show();
                isEditMode = false;
                setEditMode(false);
                binding.btnSimpan.setText("Simpan");
                loadAndDisplayActivityDetails(currentActivityId);
            } else {
                displayError("Gagal memperbarui kegiatan.");
            }
        } catch (Exception e) {
            displayError("Terjadi kesalahan saat memperbarui: " + e.getMessage());
            Log.e("DetailActivity2", "Error updating activity: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus kegiatan ini? Tindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteActivity();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteActivity() {
        if (currentActivityId == null) {
            displayError("ID Aktivitas tidak ditemukan untuk dihapus.");
            return;
        }

        try {
            long result = activityHelper.delete(currentActivityId);
            if (result > 0) {
                Toast.makeText(this, "Kegiatan berhasil dihapus.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                displayError("Gagal menghapus kegiatan.");
            }
        } catch (Exception e) {
            displayError("Terjadi kesalahan saat menghapus: " + e.getMessage());
            Log.e("DetailActivity2", "Error deleting activity: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDatabases();
        binding = null;
    }

    private void closeDatabases() {
        if (activityHelper != null) activityHelper.close();
        if (provinceHelper != null) provinceHelper.close();
        if (cityHelper != null) cityHelper.close();
        if (userHelper != null) userHelper.close();
    }

    private void loadProvincesForDropdown() {
        if (!provinceHelper.isOpen()) provinceHelper.open();
        try (Cursor cursor = provinceHelper.queryAll()) {
            provinces.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns.NAME));
                    provinces.add(new Province(id, name));
                } while (cursor.moveToNext());
            }
            provinceAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("DetailActivity2", "Error loading provinces for dropdown: " + e.getMessage());
            Toast.makeText(this, "Gagal memuat daftar provinsi.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCitiesForDropdown(String provinceId) {
        if (!cityHelper.isOpen()) cityHelper.open();
        try (Cursor cursor = cityHelper.queryByProvinceId(Integer.parseInt(provinceId))) {
            cities.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(CityDBContract.CityColumns._ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CityDBContract.CityColumns.NAME));
                    int provId = cursor.getInt(cursor.getColumnIndexOrThrow(CityDBContract.CityColumns.PROVINCE_ID));
                    cities.add(new City(id, name, provId));
                } while (cursor.moveToNext());
            }
            cityAdapter.notifyDataSetChanged();
            binding.autoCity.setEnabled(!cities.isEmpty());
        } catch (Exception e) {
            Log.e("DetailActivity2", "Error loading cities for dropdown: " + e.getMessage());
            Toast.makeText(this, "Gagal memuat daftar kota.", Toast.LENGTH_SHORT).show();
            binding.autoCity.setEnabled(false);
        }
    }

    private void setupCategoryView() {
        categoryList = new ArrayList<>();
        categoryList.add("Lingkungan");
        categoryList.add("Pendidikan");
        categoryList.add("Kesehatan");
        categoryList.add("Sosial");
        categoryList.add("Olahraga");
        categoryList.add("Teknologi");
        categoryList.add("Lainnya");

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.etCategory.setEnabled(false);

        binding.etCategory.setOnClickListener(v -> {
            if (isEditMode) {
                showCategoryDialog();
            }
        });
    }

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Kategori");
        builder.setSingleChoiceItems(categoryList.toArray(new String[0]), categoryList.indexOf(binding.etCategory.getText().toString()), (dialog, which) -> {
            selectedCategory = categoryList.get(which);
            binding.etCategory.setText(selectedCategory);
            dialog.dismiss();
        });
        builder.show();
    }
}