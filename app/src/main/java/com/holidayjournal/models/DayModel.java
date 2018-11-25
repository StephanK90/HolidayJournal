package com.holidayjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DayModel implements Parcelable {

    private int nr;
    private String date;
    private String title;
    private String description;
    private int rating;
    private LocationModel location;

    public DayModel() {

    }

    DayModel(int nr, String date) {
        this.nr = nr;
        this.date = date;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.nr);
        out.writeString(this.date);
        out.writeString(this.title);
        out.writeString(this.description);
        out.writeInt(this.rating);
        out.writeParcelable(this.location, flags);
    }

    public static final Parcelable.Creator<DayModel> CREATOR
            = new Parcelable.Creator<DayModel>() {

        public DayModel createFromParcel(Parcel in) {
            return new DayModel(in);
        }

        public DayModel[] newArray(int size) {
            return new DayModel[size];
        }
    };

    private DayModel(Parcel in) {
        this.nr = in.readInt();
        this.date = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.rating = in.readInt();
        this.location = in.readParcelable(getClass().getClassLoader());
    }
}
