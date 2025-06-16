package com.example.volunity.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Favorite implements Parcelable {
    private int id;
    private int userId;
    private int activitiesId;

    public Favorite(int id, int userId, int activitiesId) {
        this.id = id;
        this.userId = userId;
        this.activitiesId = activitiesId;
    }

    protected Favorite(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        activitiesId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(userId);
        dest.writeInt(activitiesId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Favorite> CREATOR = new Creator<Favorite>() {
        @Override
        public Favorite createFromParcel(Parcel in) {
            return new Favorite(in);
        }

        @Override
        public Favorite[] newArray(int size) {
            return new Favorite[size];
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

    public int getActivitiesId() {
        return activitiesId;
    }

    public void setActivitiesId(int activitiesId) {
        this.activitiesId = activitiesId;
    }
}
