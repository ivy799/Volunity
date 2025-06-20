package com.example.volunity.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull; // Pastikan ini diimpor jika menggunakan @NonNull

import com.google.gson.annotations.SerializedName;

public class City implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("province_id")
    private int provinceId; // Pastikan ini provinceId
    @SerializedName("name")
    private String name;

    public City(int id, String name, int provinceId) {
        this.id = id;
        this.name = name;
        this.provinceId = provinceId;
    }

    protected City(Parcel in) {
        id = in.readInt();
        name = in.readString();
        provinceId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(provinceId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    @NonNull
    @Override
    public String toString() {
        // Ini yang PALING PENTING: Mengembalikan nama kota/kabupaten
        return name;
    }
}