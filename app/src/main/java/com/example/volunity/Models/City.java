package com.example.volunity.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {
    private int id;
    private String name;
    private int province_id;

    public City(int id, String name, int province_id) {
        this.id = id;
        this.name = name;
        this.province_id = province_id;
    }

    protected City(Parcel in) {
        id = in.readInt();
        name = in.readString();
        province_id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(province_id);
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

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }
}
