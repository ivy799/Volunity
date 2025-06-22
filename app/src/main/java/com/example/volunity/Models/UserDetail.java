package com.example.volunity.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class UserDetail implements Parcelable {
    private int id, userId, cityId, provinceId;
    private String name, photo_profile, gender;
    private Date dateOfBirth;

    public UserDetail(int id, int userId, int cityId, int provinceId, String name, String photo_profile, String gender, Date dateOfBirth) {
        this.id = id;
        this.userId = userId;
        this.cityId = cityId;
        this.provinceId = provinceId;
        this.name = name;
        this.photo_profile = photo_profile;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    protected UserDetail(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        cityId = in.readInt();
        provinceId = in.readInt();
        name = in.readString();
        photo_profile = in.readString();
        gender = in.readString();
    }

    public UserDetail() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(cityId);
        dest.writeInt(provinceId);
        dest.writeString(name);
        dest.writeString(photo_profile);
        dest.writeString(gender);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserDetail> CREATOR = new Creator<UserDetail>() {
        @Override
        public UserDetail createFromParcel(Parcel in) {
            return new UserDetail(in);
        }

        @Override
        public UserDetail[] newArray(int size) {
            return new UserDetail[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_profile() {
        return photo_profile;
    }

    public void setPhoto_profile(String photo_profile) {
        this.photo_profile = photo_profile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
