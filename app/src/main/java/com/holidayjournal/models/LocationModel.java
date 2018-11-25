package com.holidayjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationModel implements Parcelable {

    private String name;
    private double latitude;
    private double longitude;

    public LocationModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeDouble(this.latitude);
        out.writeDouble(this.longitude);
    }

    public static final Parcelable.Creator<LocationModel> CREATOR
            = new Parcelable.Creator<LocationModel>() {

        public LocationModel createFromParcel(Parcel in) {
            return new LocationModel(in);
        }

        public LocationModel[] newArray(int size) {
            return new LocationModel[size];
        }
    };

    private LocationModel(Parcel in) {
        this.name = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }
}
