package com.example.volunity.Activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.volunity.Api.WilayahApiClient;
import com.example.volunity.Database_config.City.CityDBContract;
import com.example.volunity.Database_config.DatabaseHelper;
import com.example.volunity.Database_config.Province.ProvinceDBContract;
import com.example.volunity.Database_config.User.UserHelper;
import com.example.volunity.Database_config.User.UserMappingHelper;
import com.example.volunity.Database_config.UserDetail.UserDetailHelper;
import com.example.volunity.Database_config.UserDetail.UserDetailMappingHelper;
import com.example.volunity.Models.City;
import com.example.volunity.Models.Province;
import com.example.volunity.Models.User;
import com.example.volunity.Models.UserDetail;
import com.example.volunity.databinding.ActivityDetailProfileBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DetailProfileActivity extends AppCompatActivity {

    private static final String DOB_FORMAT = "dd-MM-yyyy";

    private ActivityDetailProfileBinding binding;
    private UserHelper userHelper;
    private UserDetailHelper userDetailsHelper;
    private DatabaseHelper dbHelper;

    private final ExecutorService executor = Executors.newFixedThreadPool(2); // Thread untuk database & network
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Nullable
    private User currentUser;
    @Nullable
    private UserDetail currentUserDetails;

    private List<Province> provinces = new ArrayList<>();
    private List<City> cities = new ArrayList<>();
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<City> cityAdapter;

    @Nullable
    private Province selectedProvince;
    @Nullable
    private City selectedCity;

    private boolean isEditMode = false;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Gagal memuat profil: ID pengguna tidak valid.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeHelpers();
        initializeAdapters();
        setupListeners();
        loadInitialData();
        setEditMode(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        binding = null;
    }

    private void initializeHelpers() {
        userHelper = UserHelper.getInstance(this);
        userDetailsHelper = UserDetailHelper.getInstance(this);
        dbHelper = new DatabaseHelper(this);
    }

    private void initializeAdapters() {
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, provinces);
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        binding.autoProvince.setAdapter(provinceAdapter);
        binding.autoCity.setAdapter(cityAdapter);
    }

    private void setupListeners() {
        binding.btnEdit.setOnClickListener(v -> toggleEditMode());
        binding.btnSave.setOnClickListener(v -> saveChanges());
        binding.etDob.setOnClickListener(v -> showDatePicker());

        binding.autoProvince.setOnItemClickListener((parent, view, position, id) -> {
            selectedProvince = (Province) parent.getItemAtPosition(position);
            if (selectedProvince != null) {
                binding.autoCity.setText("", false); // Kosongkan pilihan kota
                selectedCity = null;
                loadCitiesForProvince(selectedProvince);
            }
        });

        binding.autoCity.setOnItemClickListener((parent, view, position, id) ->
                selectedCity = (City) parent.getItemAtPosition(position));
    }

    private void loadInitialData() {
        // Tampilkan loading indicator jika ada
        executor.execute(() -> {
            // Muat profil pengguna dari database lokal
            loadUserProfileFromDB();
            // Muat provinsi dari API
            loadProvincesFromApi();
        });
    }

    private void loadUserProfileFromDB() {
        try {
            userHelper.open();
            userDetailsHelper.open();
            Cursor userCursor = userHelper.search(userId);
            if (userCursor != null) {
                currentUser = UserMappingHelper.mapCursorToObject(userCursor);
                userCursor.close();
            }

            Cursor detailCursor = userDetailsHelper.search(userId);
            if (detailCursor != null) {
                currentUserDetails = UserDetailMappingHelper.mapCursorToObject(detailCursor);
                detailCursor.close();
            }


            mainThreadHandler.post(this::populateUserData);

        } catch (ParseException e) {
            mainThreadHandler.post(() -> Toast.makeText(this, "Gagal memuat data profil.", Toast.LENGTH_SHORT).show());
        } finally {
            userHelper.close();
            userDetailsHelper.close();
        }
    }

    private void loadProvincesFromApi() {
        new WilayahApiClient().getProvinces(new WilayahApiClient.WilayahApiCallback<Province>() {
            @Override
            public void onSuccess(List<Province> data) {

                executor.execute(() -> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    List<Province> localProvinces = new ArrayList<>();
                    for (Province apiProvince : data) {
                        int localId = getOrInsertProvince(db, apiProvince.getName());
                        localProvinces.add(new Province(localId, apiProvince.getName()));
                    }
                    db.close();

                    mainThreadHandler.post(() -> {
                        provinces.clear();
                        provinces.addAll(localProvinces);
                        provinceAdapter.notifyDataSetChanged();
                        // Coba set provinsi yang sudah tersimpan
                        preselectLocation();
                    });
                });
            }

            @Override
            public void onFailure(Exception e) {
                mainThreadHandler.post(() -> Toast.makeText(DetailProfileActivity.this, "Gagal memuat data provinsi.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Mengambil daftar kota untuk provinsi yang dipilih.
     @param province Provinsi yang dipilih.
     @param province Provinsi yang dipilih.
     */
    private void loadCitiesForProvince(Province province) {
        binding.autoCity.setEnabled(false);
        new WilayahApiClient().getCities(String.valueOf(province.getId()), new WilayahApiClient.WilayahApiCallback<City>() {
            @Override
            public void onSuccess(List<City> data) {
                executor.execute(()-> {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    List<City> localCities = new ArrayList<>();
                    for (City apiCity : data) {
                        int localId = getOrInsertCity(db, apiCity.getName(), province.getId());



                        localCities.add(new City(localId, apiCity.getName(), province.getId()));
                    }
                    db.close();

                    mainThreadHandler.post(() -> {
                        cities.clear();
                        cities.addAll(localCities);
                        cityAdapter.notifyDataSetChanged();
                        binding.autoCity.setEnabled(true);

                        preselectLocation();
                    });
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("API_ERROR", "Gagal memuat data kota: ", e); // <-- TAMBAHKAN BARIS INI

                mainThreadHandler.post(() -> {
                    cities.clear();
                    cityAdapter.notifyDataSetChanged();
                    Toast.makeText(DetailProfileActivity.this, "Gagal memuat data kota.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void populateUserData() {
        if (currentUser == null) {
            Toast.makeText(this, "Pengguna tidak ditemukan.", Toast.LENGTH_LONG).show();

            binding.tvDetailUsername.setText("N/A");
            binding.tvDetailEmail.setText("N/A");
            binding.tvUsername.setText("N/A");
            binding.tvMail.setText("N/A");
            binding.tvPhoneNumber.setText("N/A");
            return;
        }

        binding.tvDetailUsername.setText(currentUser.getName());
        binding.tvDetailEmail.setText(currentUser.getEmail());
        binding.tvUsername.setText(currentUser.getName());
        binding.tvMail.setText(currentUser.getEmail());
        binding.tvPhoneNumber.setText(currentUser.getPhone_number());


        binding.etUsername.setText(currentUser.getName());
        binding.etEmail.setText(currentUser.getEmail());
        binding.etPhoneNumber.setText(currentUser.getPhone_number());

        if (currentUserDetails != null) {
            binding.tvGender.setText(orDefault(currentUserDetails.getGender(), "Belum diisi"));
            binding.date.setText(formatDate(currentUserDetails.getDateOfBirth()));
            binding.etGender.setText(currentUserDetails.getGender());
            binding.etDob.setText(formatDate(currentUserDetails.getDateOfBirth()));
        } else {
            binding.tvGender.setText("Belum diisi");
            binding.date.setText("Belum diisi");
        }

        preselectLocation();
    }

    private void preselectLocation() {
        if (currentUserDetails == null || provinces.isEmpty()) {
            return;
        }

        if (selectedProvince == null) {
            for (Province p : provinces) {
                if (p.getId() == currentUserDetails.getProvinceId()) {
                    selectedProvince = p;
                    binding.autoProvince.setText(p.getName(), false);
                    loadCitiesForProvince(p); // Muat kota untuk provinsi ini
                    break;
                }
            }
        }

        // Set Kota (setelah daftar kota dimuat)
        if (selectedCity == null && !cities.isEmpty()) {
            for (City c : cities) {
                if (c.getId() == currentUserDetails.getCityId()) {
                    selectedCity = c;
                    binding.autoCity.setText(c.getName(), false);
                    break;
                }
            }
        }
    }


    /**
     * Mengubah state UI antara mode lihat dan mode edit.
     */
    private void toggleEditMode() {
        isEditMode = !isEditMode;
        setEditMode(isEditMode);
        if (isEditMode) {
            binding.btnEdit.setText("Batal");
        } else {
            binding.btnEdit.setText("Edit");
            // Kembalikan data ke kondisi semula jika dibatalkan
            populateUserData();
        }
    }

    /**
     * Mengatur visibilitas dan status enabled/disabled dari view.
     * @param enabled true untuk mode edit, false untuk mode lihat.
     */
    private void setEditMode(boolean enabled) {
        // Tampilkan/Sembunyikan TextView
        int textViewVisibility = enabled ? View.GONE : View.VISIBLE;
        binding.tvDetailUsername.setVisibility(textViewVisibility);
        binding.tvDetailEmail.setVisibility(textViewVisibility);
        binding.tvUsername.setVisibility(textViewVisibility);
        binding.tvMail.setVisibility(textViewVisibility);
        binding.tvPhoneNumber.setVisibility(textViewVisibility);
        binding.tvGender.setVisibility(textViewVisibility);
        binding.date.setVisibility(textViewVisibility);

        // Tampilkan/Sembunyikan EditText
        int editTextVisibility = enabled ? View.VISIBLE : View.GONE;
        binding.etUsername.setVisibility(editTextVisibility);
        binding.etEmail.setVisibility(editTextVisibility);
        binding.etPhoneNumber.setVisibility(editTextVisibility);
        binding.etGender.setVisibility(editTextVisibility);
        binding.etDob.setVisibility(editTextVisibility);

        // Aktifkan/Nonaktifkan input
        binding.etUsername.setEnabled(enabled);
        binding.etEmail.setEnabled(enabled);
        binding.etPhoneNumber.setEnabled(enabled);
        binding.etGender.setEnabled(enabled);
        binding.etDob.setEnabled(enabled);
        binding.autoProvince.setEnabled(enabled);
        // autoCity di-handle terpisah berdasarkan pilihan provinsi

        binding.btnSave.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    /**
     * Menampilkan dialog pemilih tanggal.
     */
    private void showDatePicker() {
        if (!isEditMode) return;

        Calendar cal = Calendar.getInstance();
        try {
            Date currentDate = parseDate(binding.etDob.getText().toString());
            if (currentDate != null) {
                cal.setTime(currentDate);
            }
        } catch (Exception ignored) {}

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(year, month, dayOfMonth);
            binding.etDob.setText(formatDate(picked.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Validasi input sebelum menyimpan.
     */
    private boolean validateFields() {
        if (TextUtils.isEmpty(binding.etUsername.getText().toString().trim())) {
            binding.etUsername.setError("Nama tidak boleh kosong!");
            return false;
        }
        if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
            binding.etEmail.setError("Email tidak boleh kosong!");
            return false;
        }
        if (selectedProvince == null) {
            binding.autoProvince.setError("Provinsi belum dipilih!");
            return false;
        }
        if (selectedCity == null) {
            binding.autoCity.setError("Kota belum dipilih!");
            return false;
        }
        return true;
    }

    /**
     * Mengumpulkan data dari input dan menyimpannya ke database.
     */
    private void saveChanges() {
        if (!validateFields()) {
            return;
        }

        // Jalankan update di background thread
        executor.execute(() -> {
            boolean success = updateUserProfileInDB();
            mainThreadHandler.post(() -> {
                if (success) {
                    Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                    // Muat ulang data yang sudah diupdate dan matikan mode edit
                    isEditMode = false;
                    setEditMode(false);
                    binding.btnEdit.setText("Edit");
                    loadInitialData();
                } else {
                    Toast.makeText(this, "Gagal memperbarui profil.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Melakukan operasi update ke database.
     * Dieksekusi di background thread.
     * @return true jika berhasil, false jika gagal.
     */
    private boolean updateUserProfileInDB() {
        if (currentUser == null) return false;

        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhoneNumber.getText().toString().trim();

        try {
            userHelper.open();
            userDetailsHelper.open();

            // Update tabel User
            int resultUser = userHelper.updateUserProfile(userId, username, email, phone);

            // Siapkan data UserDetail
            UserDetail details = new UserDetail();
            details.setUserId(userId);
            details.setName(username); // Selalu update nama sesuai input terbaru
            details.setGender(binding.etGender.getText().toString().trim());
            details.setDateOfBirth(parseDate(binding.etDob.getText().toString().trim()));
            details.setProvinceId(selectedProvince != null ? selectedProvince.getId() : 0);
            details.setCityId(selectedCity != null ? selectedCity.getId() : 0);

            // Update atau Insert tabel UserDetail
            int resultDetail;
            if (userDetailsHelper.existsForUserId(userId)) {
                resultDetail = userDetailsHelper.updateUserDetails(details);
            } else {
                resultDetail = (int) userDetailsHelper.insertUserDetails(details);
            }

            return resultUser > 0 && resultDetail > 0;

        } finally {
            userHelper.close();
            userDetailsHelper.close();
        }
    }

    // --- Utility Methods ---

    private int getOrInsertProvince(SQLiteDatabase db, String provinceName) {
        int id = -1;
        Cursor cursor = db.query(
                ProvinceDBContract.TABLE_NAME, new String[]{ProvinceDBContract.ProvinceColumns._ID},
                ProvinceDBContract.ProvinceColumns.NAME + " = ?", new String[]{provinceName},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0);
            cursor.close();
        } else {
            ContentValues values = new ContentValues();
            values.put(ProvinceDBContract.ProvinceColumns.NAME, provinceName);
            id = (int) db.insert(ProvinceDBContract.TABLE_NAME, null, values);
        }
        return id;
    }

    private int getOrInsertCity(SQLiteDatabase db, String cityName, int provinceId) {
        int id = -1;
        Cursor cursor = db.query(
                CityDBContract.TABLE_NAME, new String[]{CityDBContract.CityColumns._ID},
                CityDBContract.CityColumns.NAME + " = ? AND " + CityDBContract.CityColumns.PROVINCE_ID + " = ?",
                new String[]{cityName, String.valueOf(provinceId)},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getInt(0);
            cursor.close();
        } else {
            ContentValues values = new ContentValues();
            values.put(CityDBContract.CityColumns.NAME, cityName);
            values.put(CityDBContract.CityColumns.PROVINCE_ID, provinceId);
            id = (int) db.insert(CityDBContract.TABLE_NAME, null, values);
        }
        return id;
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return new SimpleDateFormat(DOB_FORMAT, Locale.getDefault()).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatDate(@Nullable Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(DOB_FORMAT, Locale.getDefault()).format(date);
    }

    private String orDefault(@Nullable String value, String defaultValue) {
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }
}