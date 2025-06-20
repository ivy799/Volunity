package com.example.volunity.Activities;

import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.volunity.Api.WilayahApiClient;
import com.example.volunity.Database_config.Activity.ActivityDBContract;
import com.example.volunity.Database_config.Activity.ActivityHelper;
import com.example.volunity.Database_config.City.CityDBContract;
import com.example.volunity.Database_config.City.CityHelper;
import com.example.volunity.Database_config.Province.ProvinceDBContract;
import com.example.volunity.Database_config.Province.ProvinceHelper;
import com.example.volunity.Database_config.User.UserHelper;
import com.example.volunity.Database_config.User.UserMappingHelper;
import com.example.volunity.Models.Province;
import com.example.volunity.Models.City;
import com.example.volunity.Models.User;
import com.example.volunity.R;
import com.example.volunity.databinding.ActivityAddActivityBinding;
import com.example.volunity.uihelper.UiHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.time.LocalDate; // Ganti LocalDateTime menjadi LocalDate jika hanya menyimpan tanggal
import java.time.LocalDateTime; // Tetap pakai ini jika mau simpan tanggal + waktu saat ini
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    private ActivityAddActivityBinding binding;
    private UserHelper userHelper;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> requestPermLauncher;
    private String requiredPermission;

    private WilayahApiClient wilayahApiClient;
    private List<Province> provinces;
    private List<City> cities;
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<City> cityAdapter;
    private Province selectedProvince;
    private City selectedCity;
    private ProvinceHelper provinceHelper;
    private CityHelper cityHelper;
    private ActivityHelper activityHelper;

    private Calendar calendar;
    // Ubah menjadi LocalDate jika hanya ingin tanggal tanpa waktu sama sekali di memori
    // Atau tetap LocalDateTime tapi gunakan LocalDate.atStartOfDay() untuk default waktu
    private LocalDate selectedDate; // Menggunakan LocalDate untuk hanya tanggal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userHelper = UserHelper.getInstance(this);
        provinceHelper = ProvinceHelper.getInstance(this);
        cityHelper = CityHelper.getInstance(this);
        activityHelper = ActivityHelper.getInstance(this);

        setupBinding();
        setupPermission();
        setupGalleryLauncher();
        setupPermissionRequester();
        setupListeners();
        setupView();
        setupWilayahApi();
        calendar = Calendar.getInstance();
        setupDropdowns();
        loadProvinces();
        selectedDate = LocalDate.now(); // Inisialisasi dengan tanggal saat ini
        updateDateEditText(); // Tampilkan tanggal default
    }

    @Override
    protected void onResume() {
        super.onResume();
        userHelper.open();
        provinceHelper.open();
        cityHelper.open();
        activityHelper.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userHelper.close();
        provinceHelper.close();
        cityHelper.close();
        activityHelper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        if (wilayahApiClient != null) wilayahApiClient.shutdown();
    }

    private void setupBinding() {
        binding = ActivityAddActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void setupPermission() {
        requiredPermission = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        startCrop(result.getData().getData());
                    }
                });
    }

    private void setupPermissionRequester() {
        requestPermLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) openGallery();
                    else showToast("Izin galeri ditolak — gambar tidak bisa dipilih.");
                });
    }

    private void setupListeners() {
        binding.etImage.setOnClickListener(v -> checkStoragePermission());
        binding.arrowBack.setOnClickListener(v -> finish());
        binding.etDate.setOnClickListener(v -> showDatePicker());
        binding.etDate.setKeyListener(null);
    }

    private void setupDropdowns() {
        provinces = new ArrayList<>();
        cities = new ArrayList<>();

        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, provinces);
        binding.autoProvince.setAdapter(provinceAdapter);

        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        binding.autoCity.setAdapter(cityAdapter);

        binding.autoProvince.setOnItemClickListener((parent, view, position, id) -> {
            selectedProvince = (Province) parent.getItemAtPosition(position);
            if (selectedProvince != null) {
                loadCities(String.valueOf(selectedProvince.getId()));
                binding.autoCity.setText("", false);
                binding.autoCity.setEnabled(false);
                selectedCity = null;
            }
        });

        binding.autoCity.setOnItemClickListener((parent, view, position, longId) ->
                selectedCity = (City) parent.getItemAtPosition(position));

        binding.autoCity.setEnabled(false);

        binding.btnSubmit.setOnClickListener(v -> UiHelper.applyiOSButtonAnimation(v, () -> {
            if (!validateFields()) return;
            saveToProvince();
            saveToCity();
            saveToActivity();
            showSuccessDialog("Kegiatan berhasil ditambahkan.");

        }));
    }

    private boolean validateFields() {
        if (selectedImageUri == null) {
            showTduWarning("Silakan pilih gambar terlebih dahulu.");
            return false;
        }
        if (TextUtils.isEmpty(binding.etNamaKegiatan.getText().toString().trim())) {
            showTduWarning("Nama kegiatan tidak boleh kosong.");
            return false;
        }
        if (TextUtils.isEmpty(binding.etAddress.getText().toString().trim())) {
            showTduWarning("Alamat kegiatan tidak boleh kosong.");
            return false;
        }
        if (selectedDate == null) { // Cek selectedDate
            showTduWarning("Tanggal kegiatan tidak boleh kosong.");
            return false;
        }
        if (TextUtils.isEmpty(binding.etDescription.getText().toString().trim())) {
            showTduWarning("Deskripsi tidak boleh kosong.");
            return false;
        }
        if (TextUtils.isEmpty(binding.autoCity.getText().toString().trim())) {
            showTduWarning("Kota tidak boleh kosong.");
            return false;
        }
        if (TextUtils.isEmpty(binding.etMaxPeople.getText().toString().trim())) {
            showTduWarning("Jumlah Maximum tidak boleh kosong.");
            return false;
        }
        try {
            Integer.parseInt(binding.etMaxPeople.getText().toString().trim());
        } catch (NumberFormatException e) {
            showTduWarning("Jumlah Maximum harus berupa angka.");
            return false;
        }
        return true;
    }

    private void saveToProvince() {
        String name = binding.autoProvince.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            binding.autoProvince.setError("Nama provinsi tidak boleh kosong.");
            binding.autoProvince.requestFocus();
            return;
        }
        Cursor cursor = provinceHelper.queryByName(name);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return;
        }
        if (cursor != null) cursor.close();
        ContentValues values = new ContentValues();
        values.put(ProvinceDBContract.ProvinceColumns.NAME, name);
        provinceHelper.insert(values);
    }

    private void saveToCity() {
        String name = binding.autoCity.getText().toString().trim();
        String nameProvince = binding.autoProvince.getText().toString().trim();

        if (TextUtils.isEmpty(name)) return;
        Cursor cursor = cityHelper.queryByName(name);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return;
        }
        if (cursor != null) cursor.close();

        Cursor cProv = provinceHelper.queryByName(nameProvince);
        if (cProv == null || !cProv.moveToFirst()) {
            if (cProv != null) cProv.close();
            return;
        }
        int provinceId = cProv.getInt(cProv.getColumnIndexOrThrow(ProvinceDBContract.ProvinceColumns._ID));
        cProv.close();

        ContentValues v = new ContentValues();
        v.put(CityDBContract.CityColumns.NAME, name);
        v.put(CityDBContract.CityColumns.PROVINCE_ID, provinceId);
        cityHelper.insert(v);
    }

    private void saveToActivity() {
        int organizerId = getIntent().getIntExtra("USER_ID", -1);
        String image = (selectedImageUri != null) ? selectedImageUri.toString() : null;
        String title = binding.etNamaKegiatan.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        // Simpan LocalDate sebagai String (ISO 8601, e.g., "2025-06-21")
        String dateStringForDb = (selectedDate != null) ? selectedDate.toString() : null;
        Integer maxPeople = binding.etMaxPeople.getText().toString().trim().isEmpty() ? null
                : Integer.parseInt(binding.etMaxPeople.getText().toString().trim());
        String description = binding.etDescription.getText().toString().trim();
        String cityName = binding.autoCity.getText().toString().trim();

        int cityId = -1;
        int provinceId = -1;

        Cursor cursorCity = cityHelper.queryByName(cityName);
        if (cursorCity != null && cursorCity.moveToFirst()) {
            cityId = cursorCity.getInt(cursorCity.getColumnIndexOrThrow(CityDBContract.CityColumns._ID));
            provinceId = cursorCity.getInt(cursorCity.getColumnIndexOrThrow(CityDBContract.CityColumns.PROVINCE_ID));
            cursorCity.close();
        } else {
            if (cursorCity != null) cursorCity.close();
            showToast("Kota tidak ditemukan di database.");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ActivityDBContract.ActivityColumns.ORGANIZER_ID, organizerId);
        values.put(ActivityDBContract.ActivityColumns.IMAGE, image);
        values.put(ActivityDBContract.ActivityColumns.TITLE, title);
        values.put(ActivityDBContract.ActivityColumns.ADDRESS, address);
        values.put(ActivityDBContract.ActivityColumns.DATE, dateStringForDb); // Menggunakan ISO 8601 LocalDate String
        values.put(ActivityDBContract.ActivityColumns.MAX_PEOPLE, maxPeople);
        values.put(ActivityDBContract.ActivityColumns.DESCRIPTION, description);
        values.put(ActivityDBContract.ActivityColumns.CITY_ID, cityId);
        values.put(ActivityDBContract.ActivityColumns.PROVINCE_ID, provinceId);
        values.put(ActivityDBContract.ActivityColumns.CREATED_AT, LocalDateTime.now().toString()); // Tetap LocalDateTime untuk timestamp
        values.put(ActivityDBContract.ActivityColumns.UPDATED_AT, LocalDateTime.now().toString()); // Tetap LocalDateTime untuk timestamp
        activityHelper.insert(values);
    }

    private void setupView() {
        int userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId != -1) loadUserProfile(userId);
        else showToast("ID pengguna tidak ditemukan.");
    }

    private void setupWilayahApi() {
        wilayahApiClient = new WilayahApiClient();
    }

    private void loadUserProfile(int userId) {
        if (!userHelper.isOpen()) userHelper.open();
        try (Cursor cursor = userHelper.search(userId)) {
            if (cursor != null && cursor.moveToFirst()) {
                User user = UserMappingHelper.mapCursorToObject(cursor);
                if (user != null) {
                    showUserToView(user);
                }
            }
        }
    }

    private void showUserToView(User user) {
        binding.idDisplay.setText(String.valueOf(user.getId()));
        binding.etNamaPenyelenggara.setText(user.getName());
        binding.etNamaPenyelenggara.setEnabled(false);
    }

    private void showSelectedImage(Uri uri) {
        binding.etImage.setImageURI(uri);
        binding.uriImage.setVisibility(VISIBLE);
        binding.uriImage.setText("sumber: " + uri);
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, requiredPermission)
                == PackageManager.PERMISSION_GRANTED) openGallery();
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, requiredPermission)) {
            showToast("Aplikasi butuh akses galeri.");
            requestPermLauncher.launch(requiredPermission);
        } else {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void startCrop(Uri sourceUri) {
        String fileName = "cropped_" + System.currentTimeMillis() + ".jpg";
        Uri destUri = Uri.fromFile(new File(getCacheDir(), fileName));
        UCrop.of(sourceUri, destUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            selectedImageUri = UCrop.getOutput(data);
            if (selectedImageUri != null) showSelectedImage(selectedImageUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable error = UCrop.getError(data);
            String msg = (error != null) ? error.getMessage() : "Terjadi kesalahan tidak diketahui.";
            showToast("Gagal crop: " + msg);
        }
    }

    private void loadProvinces() {
        wilayahApiClient.getProvinces(new WilayahApiClient.WilayahApiCallback<Province>() {
            @Override public void onSuccess(List<Province> data) {
                runOnUiThread(() -> {
                    provinces.clear();
                    provinces.addAll(data);
                    provinceAdapter.notifyDataSetChanged();
                    if (!provinces.isEmpty()) {
                        binding.autoProvince.setText(provinces.get(0).getName(), false);
                        selectedProvince = provinces.get(0);
                        loadCities(String.valueOf(selectedProvince.getId()));
                    }
                });
            }
            @Override public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    showToast("Gagal memuat provinsi: " + e.getMessage());
                    binding.autoCity.setEnabled(false);
                });
            }
        });
    }

    private void loadCities(String provinceId) {
        wilayahApiClient.getCities(provinceId, new WilayahApiClient.WilayahApiCallback<City>() {
            @Override public void onSuccess(List<City> data) {
                runOnUiThread(() -> {
                    cities.clear();
                    cities.addAll(data);
                    cityAdapter.notifyDataSetChanged();
                    binding.autoCity.setEnabled(true);
                    if (!cities.isEmpty()) {
                        binding.autoCity.setText(cities.get(0).getName(), false);
                        selectedCity = cities.get(0);
                    } else {
                        binding.autoCity.setText("Tidak ada kota", false);
                        binding.autoCity.setEnabled(false);
                        selectedCity = null;
                    }
                });
            }
            @Override public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    showToast("Gagal memuat kota: " + e.getMessage());
                    cities.clear();
                    cityAdapter.notifyDataSetChanged();
                    binding.autoCity.setText("Gagal memuat kota", false);
                    binding.autoCity.setEnabled(false);
                    selectedCity = null;
                });
            }
        });
    }

    // Ubah ini untuk hanya DatePickerDialog
    private void showDatePicker() {
        // Mendapatkan tanggal saat ini dari selectedDate
        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue() - 1; // Month is 0-indexed
        int day = selectedDate.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    // Update the year, month, day of selectedDate
                    selectedDate = LocalDate.of(y, m + 1, d); // Hanya tanggal
                    updateDateEditText();
                }, year, month, day);
        datePickerDialog.show();
    }

    // Metode untuk memperbarui etDate dengan format yang diinginkan
    private void updateDateEditText() {
        // Menggunakan formatter hanya untuk tanggal
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
        binding.etDate.setText(selectedDate.format(displayFormatter));
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showTduWarning(String pesan) {
        View v = getLayoutInflater().inflate(R.layout.dialog_warning_tdu, null);
        AlertDialog d = new AlertDialog.Builder(this)
                .setView(v)
                .setCancelable(true)
                .create();
        if (d.getWindow() != null)
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ((TextView) v.findViewById(R.id.tduMessage)).setText(pesan);
        v.findViewById(R.id.tduOk).setOnClickListener(vw -> d.dismiss());
        d.show();
    }

    private void showSuccessDialog(String pesan) {
        View v = getLayoutInflater().inflate(R.layout.dialog_success, null);
        AlertDialog d = new AlertDialog.Builder(this)
                .setView(v)
                .setCancelable(false)
                .create();

        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ((TextView) v.findViewById(R.id.successMessage)).setText(pesan);
        v.findViewById(R.id.successOk).setOnClickListener(vw -> {
            d.dismiss();
            finish();
        });

        d.show();
    }
}