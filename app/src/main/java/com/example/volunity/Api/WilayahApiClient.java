// app/src/main/java/com/example/volunity/Api/WilayahApiClient.java
package com.example.volunity.Api;

import com.example.volunity.Models.Province;
import com.example.volunity.Models.City; // Ganti Regency dengan City
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WilayahApiClient {

    private static final String BASE_URL = "https://www.emsifa.com/api-wilayah-indonesia/api/";
    private final OkHttpClient client;
    private final Gson gson;
    private final ExecutorService executorService;

    public WilayahApiClient() {
        client = new OkHttpClient();
        gson = new Gson();
        executorService = Executors.newSingleThreadExecutor();
    }

    public interface WilayahApiCallback<T> {
        void onSuccess(List<T> data);
        void onFailure(Exception e);
    }

    public void getProvinces(WilayahApiCallback<Province> callback) {
        String url = BASE_URL + "provinces.json";
        fetchData(url, new TypeToken<List<Province>>() {}.getType(), callback);
    }

    // Perhatikan parameter provinceId di sini tetap String karena URL API membutuhkannya
    public void getCities(String provinceId, WilayahApiCallback<City> callback) { // Ganti getRegencies menjadi getCities
        String url = BASE_URL + "regencies/" + provinceId + ".json"; // URL tetap menggunakan ID provinsi
        fetchData(url, new TypeToken<List<City>>() {}.getType(), callback); // Ganti Regency dengan City
    }

    private <T> void fetchData(String url, Type type, WilayahApiCallback<T> callback) {
        executorService.execute(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    List<T> data = gson.fromJson(json, type);
                    if (data == null) {
                        data = Collections.emptyList();
                    }
                    callback.onSuccess(data);
                } else {
                    callback.onFailure(new IOException("Request failed with code: " + response.code() + " and message: " + response.message()));
                }
            } catch (IOException e) {
                callback.onFailure(e);
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}