package com.greece.nasiakouts.babysitterfinder.Models;

public class TimeSlot {
    private String day;
    private String hours;
    private boolean allDay;

    public TimeSlot(String day, String hours, boolean allDay) {
        this.day = day;
        this.hours = hours;
        this.allDay = allDay;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public boolean isAllDay(){
        return allDay;
    }

    public void setAllDay(boolean allDay){
        this.allDay = allDay;
    }

    public int getStartHourInt() {
        return Integer.parseInt(hours.split("-")[0].split(":")[0]);
    }

    public int getStartSecondsInt() {
        return Integer.parseInt(hours.split("-")[0].split(":")[1]);
    }

    public double getStartHoursDouble(){
        return Integer.parseInt(hours.split("-")[0].split(":")[0] +
                (Integer.parseInt(hours.split("-")[0].split(":")[1]) / 100));
    }

    public int getFinishHourInt() {
        return Integer.parseInt(hours.split("-")[1].split(":")[0]);
    }

    public int getFinishSecondsInt() {
        return Integer.parseInt(hours.split("-")[1].split(":")[1]);
    }

    public double getFinishHoursDouble(){
        return Integer.parseInt(hours.split("-")[1].split(":")[0] +
                (Integer.parseInt(hours.split("-")[1].split(":")[1]) / 100));
    }
}
