package com.example.volunity.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Province implements Parcelable {
    private int id;
    private String name;

    public Province(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Province(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<Province> CREATOR = new Creator<Province>() {
        @Override
        public Province createFromParcel(Parcel in) {
            return new Province(in);
        }

        @Override
        public Province[] newArray(int size) {
            return new Province[size];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
