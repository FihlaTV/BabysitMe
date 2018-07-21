package com.greece.nasiakouts.babysitterfinder.Models;

import java.io.Serializable;

public class Appointment implements Serializable {
    private int totalKids;
    private double minAge;
    private String streetAddress;
    private TimeSlot slot;
    private int sitterSex;

    private boolean acceptedBySitter = false;
    private String sitterId;

    private String customerId;

    public Appointment() {
    }

    public Appointment(int totalKids, double minAge, String streetAddress,
                       TimeSlot slots, int sitterSex, String customerId) {
        this.totalKids = totalKids;
        this.minAge = minAge;
        this.streetAddress = streetAddress;
        this.slot = slots;
        this.sitterSex = sitterSex;
        this.customerId = customerId;
    }

    public int getTotalKids() {
        return totalKids;
    }

    public void setTotalKids(int totalKids) {
        this.totalKids = totalKids;
    }

    public double getMinAge() {
        return minAge;
    }

    public void setMinAge(double minAge) {
        this.minAge = minAge;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public void setSlot(TimeSlot slot) {
        this.slot = slot;
    }

    public int getSitterSex() {
        return sitterSex;
    }

    public void setSitterSex(int sitterSex) {
        this.sitterSex = sitterSex;
    }

    public boolean isAcceptedBySitter() {
        return acceptedBySitter;
    }

    public void setAcceptedBySitter(boolean acceptedBySitter) {
        this.acceptedBySitter = acceptedBySitter;
    }

    public String getSitterId() {
        return sitterId;
    }

    public void setSitterId(String sitterId) {
        this.sitterId = sitterId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

}
