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
    private LocalDate date; // PERUBAHAN: Ubah tipe data ke LocalDate
    private Integer maxPeople;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Formatter untuk LocalDate saat menulis/membaca dari Parcel
    // Gunakan ISO_LOCAL_DATE karena kita sepakat untuk menyimpan tanggal saja
    private static final DateTimeFormatter DATE_PARCEL_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    // Formatter untuk LocalDateTime (createdAt, updatedAt)
    private static final DateTimeFormatter DATETIME_PARCEL_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    public Activity(int id, int organizerId, String image, String title, String address, int cityId, int provinceId, LocalDate date, Integer maxPeople, String description, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.organizerId = organizerId;
        this.image = image;
        this.title = title;
        this.address = address;
        this.cityId = cityId;
        this.provinceId = provinceId;
        this.date = date; // PERUBAHAN: Sekarang menerima LocalDate
        this.maxPeople = maxPeople;
        this.description = description;
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

        // PERUBAHAN: Membaca LocalDate dari Parcel sebagai String
        String dateString = in.readString();
        if (dateString != null && !dateString.isEmpty()) {
            try {
                this.date = LocalDate.parse(dateString, DATE_PARCEL_FORMATTER); // Gunakan DATE_PARCEL_FORMATTER
            } catch (DateTimeParseException e) {
                e.printStackTrace(); // Log error jika parsing gagal
                this.date = null; // Set null atau default value jika terjadi error
            }
        } else {
            this.date = null;
        }

        if (in.readByte() == 0) {
            maxPeople = null;
        } else {
            maxPeople = in.readInt();
        }
        description = in.readString();

        // PERBAIKAN: Menambahkan pembacaan untuk createdAt dan updatedAt
        long createdAtMillis = in.readLong();
        if (createdAtMillis != -1) {
            this.createdAt = new Timestamp(createdAtMillis);
        } else {
            this.createdAt = null;
        }

        long updatedAtMillis = in.readLong();
        if (updatedAtMillis != -1) {
            this.updatedAt = new Timestamp(updatedAtMillis);
        } else {
            this.updatedAt = null;
        }
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

        // PERUBAHAN: Menulis LocalDate sebagai String ke Parcel
        dest.writeString(date != null ? date.format(DATE_PARCEL_FORMATTER) : null); // Gunakan DATE_PARCEL_FORMATTER

        if (maxPeople == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(maxPeople);
        }
        dest.writeString(description);

        // PERBAIKAN: Menulis Timestamp ke Parcel (sebagai long milliseconds)
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeLong(updatedAt != null ? updatedAt.getTime() : -1);
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

    // --- Getters and Setters (Pastikan tipe data sesuai dengan LocalDate) ---

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

    // getImage() mengembalikan Uri, asumsikan 'image' String adalah URI path
    public Uri getImage() {
        return (image != null && !image.isEmpty()) ? Uri.parse(image) : null;
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

    // PERUBAHAN: Mengembalikan LocalDate
    public LocalDate getDate() {
        return date;
    }

    // PERUBAHAN: Menerima LocalDate
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