package com.example.volunity.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.Timestamp;

public class Registration_form implements Parcelable {
    private int id;
    private int userId;
    private int activityId;
    private String address;
    private int cityId;
    private int provinceId;
    private String reasons;
    private String experiences;
    private String status;
    private Timestamp createdAt;

    public Registration_form(int id, int userId, int activityId, String address, int cityId, int provinceId, String reasons, String experiences, String status, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.activityId = activityId;
        this.address = address;
        this.cityId = cityId;
        this.provinceId = provinceId;
        this.reasons = reasons;
        this.experiences = experiences;
        this.status = status;
        this.createdAt = createdAt;
    }

    protected Registration_form(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        activityId = in.readInt();
        address = in.readString();
        cityId = in.readInt();
        provinceId = in.readInt();
        reasons = in.readString();
        experiences = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(activityId);
        dest.writeString(address);
        dest.writeInt(cityId);
        dest.writeInt(provinceId);
        dest.writeString(reasons);
        dest.writeString(experiences);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Registration_form> CREATOR = new Creator<Registration_form>() {
        @Override
        public Registration_form createFromParcel(Parcel in) {
            return new Registration_form(in);
        }

        @Override
        public Registration_form[] newArray(int size) {
            return new Registration_form[size];
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

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
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

    public String getReasons() {
        return reasons;
    }

    public void setReasons(String reasons) {
        this.reasons = reasons;
    }

    public String getExperiences() {
        return experiences;
    }

    public void setExperiences(String experiences) {
        this.experiences = experiences;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
