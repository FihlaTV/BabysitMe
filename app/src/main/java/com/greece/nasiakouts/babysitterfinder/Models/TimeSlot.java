package com.greece.nasiakouts.babysitterfinder.Models;

import com.google.firebase.database.Exclude;
import com.greece.nasiakouts.babysitterfinder.Constants;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeSlot implements Serializable {
    @Exclude
    String day;
    private String specificDate;
    private double timeFrom;
    private double timeTo;
    private boolean isAllDay;
    private boolean isForever;

    public TimeSlot() {
    }

    public TimeSlot(String day, String specificDate,
                    double timeFrom, double timeTo,
                    boolean isAllDay, boolean isForever) {
        this.specificDate = specificDate;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.day = day;
        this.isForever = isForever;
        this.isAllDay = isAllDay;
    }

    @Exclude
    public String getDay() {
        return day;
    }

    @Exclude
    public void setDay(String day) {
        this.day = day;
    }

    public String getSpecificDate() {
        return specificDate;
    }

    public void setSpecificDate(String specificDate) {
        this.specificDate = specificDate;
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

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public boolean isForever() {
        return isForever;
    }

    public void setForever(boolean forever) {
        isForever = forever;
    }

    @Override
    public String toString() {
        if (isForever && isAllDay) return day;
        if (isForever) return day + " " + timeFrom + " - " + timeTo;
        return isAllDay ? day.substring(0, 3) + ", " + specificDate :
                day.substring(0, 3) + ", " +
                        specificDate + " " + timeFrom + " - " + timeTo;
    }
}
