package com.greece.nasiakouts.babysitterfinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class TimeSlot implements Parcelable {

    public static final Creator<TimeSlot> CREATOR = new Creator<TimeSlot>() {
        @Override
        public TimeSlot createFromParcel(Parcel in) {
            return new TimeSlot(in);
        }

        @Override
        public TimeSlot[] newArray(int size) {
            return new TimeSlot[size];
        }
    };

    private String day;
    private boolean allDay;
    private double timeFrom;
    private double timeTo;
    private boolean forever;
    private String specificDay;

    public TimeSlot() {
    }

    public TimeSlot(String day,
                    boolean allDay,
                    boolean forever,
                    double timeFrom,
                    double timeTo,
                    String specificDay) {
        this.day = day;
        this.allDay = allDay;
        this.forever = forever;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.specificDay = specificDay;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    protected TimeSlot(Parcel in) {
        this.day = in.readString();
        this.allDay = in.readInt() == 1;
        this.forever = in.readInt() == 1;
        this.timeFrom = in.readDouble();
        this.timeTo = in.readDouble();
        this.specificDay = in.readString();
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public boolean isForever() {
        return forever;
    }

    public double getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(double timeFrom) {
        this.timeFrom = timeFrom;
    }

    public double getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(double timeTo) {
        this.timeTo = timeTo;
    }

    public void setForever(boolean forever) {
        this.forever = forever;
    }

    public String getSpecificDay() {
        return specificDay;
    }

    public void setSpecificDay(String specificDay) {
        this.specificDay = specificDay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(day);
        dest.writeInt(allDay ? 1 : 0);
        dest.writeInt(forever ? 1 : 0);
        dest.writeDouble(timeFrom);
        dest.writeDouble(timeTo);
        dest.writeString(specificDay);
    }
}
