package com.greece.nasiakouts.babysitterfinder.Models;

import com.greece.nasiakouts.babysitterfinder.Constants;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeSlot implements Serializable {
    private String day;
    private long specificDate;
    private long timestampFrom;
    private long timestampTo;
    private boolean isAllDay;
    private boolean isForever;

    private SimpleDateFormat onlyDate = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);
    private SimpleDateFormat onlyTime = new SimpleDateFormat("HH:mm", Locale.US);
    private SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yy HH:mm", Locale.US);

    public TimeSlot(String day, int daySpecificDate, int monthSpecificDate, int yearSpecificDate,
                    int dayFrom, int monthFrom, int yearFrom, int minFrom, int secFrom,
                    int dayTo, int monthTo, int yearTo, int minTo, int secTo,
                    boolean isAllDay, boolean isForever) {

        Calendar cal = Calendar.getInstance();

        if (isAllDay) {
            timestampFrom = -1;
            timestampTo = -1;

            cal.set(Calendar.YEAR, yearSpecificDate);
            cal.set(Calendar.MONTH, monthSpecificDate);
            cal.set(Calendar.DAY_OF_MONTH, daySpecificDate);
            specificDate = cal.getTime().getTime();
        } else {
            specificDate = -1;

            cal.set(Calendar.YEAR, yearFrom);
            cal.set(Calendar.MONTH, monthFrom);
            cal.set(Calendar.DAY_OF_MONTH, dayFrom);
            cal.set(Calendar.MINUTE, minFrom);
            cal.set(Calendar.SECOND, secFrom);
            timestampFrom = cal.getTime().getTime();

            cal.set(Calendar.YEAR, yearTo);
            cal.set(Calendar.MONTH, monthTo);
            cal.set(Calendar.DAY_OF_MONTH, dayTo);
            cal.set(Calendar.MINUTE, minTo);
            cal.set(Calendar.SECOND, secTo);
            timestampTo = cal.getTime().getTime();
        }

        this.day = day;
        this.isForever = isForever;
        this.isAllDay = isAllDay;
    }

    public TimeSlot(String day, long specificDate,
                    long timestampFrom, long timestampTo,
                    boolean isAllDay, boolean isForever) {
        this.day = day;
        this.specificDate = specificDate;
        this.timestampFrom = timestampFrom;
        this.timestampTo = timestampTo;
        this.isAllDay = isAllDay;
        this.isForever = isForever;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public long getSpecificDate() {
        return specificDate;
    }

    public void setSpecificDate(long specificDate) {
        this.specificDate = specificDate;
    }

    public long getTimestampFrom() {
        return timestampFrom;
    }

    public void setTimestampFrom(long timestampFrom) {
        this.timestampFrom = timestampFrom;
    }

    public long getTimestampTo() {
        return timestampTo;
    }

    public void setTimestampTo(long timestampTo) {
        this.timestampTo = timestampTo;
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
        if (isForever) return day + " " + onlyTime.format(new Timestamp(timestampFrom))
                + " - " + onlyTime.format(new Timestamp(timestampTo));
        return isAllDay ? onlyDate.format(new Timestamp(specificDate)) :
                "From: " + sdf.format(new Timestamp(timestampFrom)) +
                        "\nTo: " + sdf.format(new Timestamp(timestampTo));
    }
}
