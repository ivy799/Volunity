package com.example.volunity.Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.time.LocalDate; // PERUBAHAN: Import LocalDate
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // Tambahkan ini untuk penanganan parsing

public class Activity implements Parcelable {
    private int id;
    private int organizerId;
    private String image;
    private String title;
    private String address;
    private int cityId;
    private int provinceId;
    private LocalDate date;
    private Integer maxPeople;
    private String description;
    private String Category;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Formatter untuk LocalDate saat menulis/membaca dari Parcel
    // Gunakan ISO_LOCAL_DATE karena kita sepakat untuk menyimpan tanggal saja
    private static final DateTimeFormatter DATE_PARCEL_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    // Formatter untuk LocalDateTime (createdAt, updatedAt)
    private static final DateTimeFormatter DATETIME_PARCEL_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    public Activity(int id, int organizerId, String image, String title, String address, int cityId, int provinceId, LocalDate date, Integer maxPeople, String description, String category, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.organizerId = organizerId;
        this.image = image;
        this.title = title;
        this.address = address;
        this.cityId = cityId;
        this.provinceId = provinceId;
        this.date = date;
        this.maxPeople = maxPeople;
        this.description = description;
        Category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected Activity(Parcel in) {
        id = in.readInt();
        organizerId = in.readInt();
        image = in.readString();
        title = in.readString();
        address = in.readString();
        cityId = in.readInt();
        provinceId = in.readInt();
        if (in.readByte() == 0) {
            maxPeople = null;
        } else {
            maxPeople = in.readInt();
        }
        description = in.readString();
        Category = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(organizerId);
        dest.writeString(image);
        dest.writeString(title);
        dest.writeString(address);
        dest.writeInt(cityId);
        dest.writeInt(provinceId);
        if (maxPeople == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(maxPeople);
        }
        dest.writeString(description);
        dest.writeString(Category);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int organizerId) {
        this.organizerId = organizerId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(Integer maxPeople) {
        this.maxPeople = maxPeople;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}