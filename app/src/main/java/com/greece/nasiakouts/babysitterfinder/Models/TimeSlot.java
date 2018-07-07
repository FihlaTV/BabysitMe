package com.greece.nasiakouts.babysitterfinder.Models;

import java.io.Serializable;

import static com.greece.nasiakouts.babysitterfinder.Constants.ANOKATOTELEIA;
import static com.greece.nasiakouts.babysitterfinder.Constants.DASH;

public class TimeSlot implements Serializable {
    private String day;
    private String fromHour;
    private String toHour;
    private boolean allDay;

    public TimeSlot(String day, String fromHour, String toHour, boolean allDay) {
        this.day = day;
        this.fromHour = fromHour;
        this.toHour = toHour;
        this.allDay = allDay;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHourRange() {
        return fromHour + DASH + toHour;
    }

    public boolean isAllDay(){
        return allDay;
    }

    public void setAllDay(boolean allDay){
        this.allDay = allDay;
    }

    public String getFromHour() {
        return fromHour;
    }

    public void setFromHour(String fromHour) {
        this.fromHour = fromHour;
    }

    public String getToHour() {
        return toHour;
    }

    public void setToHour(String toHour) {
        this.toHour = toHour;
    }

    public int getFromOnlyHour() {
        return Integer.parseInt(fromHour.split(ANOKATOTELEIA)[0]);
    }

    public int getFromOnlyMins() {
        return Integer.parseInt(fromHour.split(ANOKATOTELEIA)[1]);
    }

    public double getFromHourDouble() {
        return Integer.parseInt(fromHour.split(ANOKATOTELEIA)[0] +
                (Integer.parseInt(fromHour.split(ANOKATOTELEIA)[1]) / 100));
    }

    public int getToOnlyHourInt() {
        return Integer.parseInt(toHour.split(ANOKATOTELEIA)[0]);
    }

    public int getToOnlySecInt() {
        return Integer.parseInt(toHour.split(ANOKATOTELEIA)[1]);
    }

    public double getToHourDouble() {
        return Integer.parseInt(toHour.split(ANOKATOTELEIA)[0] +
                (Integer.parseInt(toHour.split(ANOKATOTELEIA)[1]) / 100));
    }
}
