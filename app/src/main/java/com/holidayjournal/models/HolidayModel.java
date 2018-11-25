package com.holidayjournal.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.holidayjournal.utils.DateFormatter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class HolidayModel implements Parcelable {

    private String id;
    private String title;
    private String description;
    private long startDate;
    private long endDate;
    private double rating;
    private String imageName;
    private String imageUri;
    private ArrayList<DayModel> days;


    public HolidayModel() {
    }

    public HolidayModel(String title, long start, long end) {
        this.days = new ArrayList<>();
        this.title = title;
        this.startDate = start;
        this.endDate = end;

        addDays();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public ArrayList<DayModel> getDays() {
        return this.days;
    }

    public void setDays(ArrayList<DayModel> days) {
        this.days = days;
    }

    public void calcRating() {
        double total = 0;
        int count = 0;

        for (DayModel day : days) {
            if (day.getRating() != 0) {
                total += day.getRating();
                count++;
            }
        }
        this.rating = Math.round((total / count) * 10) / 10.0;
    }

    private void addDays() {
        LocalDate start = DateFormatter.toDate(this.startDate).toLocalDate();
        LocalDate end = DateFormatter.toDate(this.endDate).toLocalDate();

        DateTimeFormatter dtf = DateTimeFormat.forPattern("d MMM yyyy");

        int nr = 1;
        days.add(new DayModel(nr, dtf.print(start)));

        while (!start.isAfter(end) && !start.isEqual(end)) {
            start = start.plusDays(1);
            days.add(new DayModel(++nr, dtf.print(start)));
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.id);
        out.writeString(this.title);
        out.writeString(this.description);
        out.writeLong(this.startDate);
        out.writeLong(this.endDate);
        out.writeDouble(this.rating);
        out.writeString(this.imageName);
        out.writeString(this.imageUri);
        out.writeList(this.days);
    }

    public static final Parcelable.Creator<HolidayModel> CREATOR
            = new Parcelable.Creator<HolidayModel>() {

        public HolidayModel createFromParcel(Parcel in) {
            return new HolidayModel(in);
        }

        public HolidayModel[] newArray(int size) {
            return new HolidayModel[size];
        }
    };

    private HolidayModel(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.startDate = in.readLong();
        this.endDate = in.readLong();
        this.rating = in.readDouble();
        this.imageName = in.readString();
        this.imageUri = in.readString();
        this.days = in.readArrayList(getClass().getClassLoader());
    }
}
