package com.example.volunity.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.security.Timestamp;
import java.time.LocalDateTime;

public class Activity implements Parcelable {
    private int id;
    private String image;
    private String title;
    private String address;
    private int cityId;
    private int provinceId;
    private LocalDateTime date;
    private Integer maxPeople;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Activity(int id, String image, String title, String address, int cityId, int provinceId, LocalDateTime date, Integer maxPeople, String description, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.address = address;
        this.cityId = cityId;
        this.provinceId = provinceId;
        this.date = date;
        this.maxPeople = maxPeople;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected Activity(Parcel in) {
        id = in.readInt();
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
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
    }
}
