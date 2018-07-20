package com.greece.nasiakouts.babysitterfinder.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Appointment implements Serializable {
    private int totalKids;
    private double minAge;
    private String streetAddress;
    private ArrayList<TimeSlot> slots;
    private int sitterSex;

    private boolean acceptedBySitter = false;
    private String sitterId;

    private String customerId;

    public Appointment(int totalKids, double minAge, String streetAddress, ArrayList<TimeSlot> slots, int sitterSex) {
        this.totalKids = totalKids;
        this.minAge = minAge;
        this.streetAddress = streetAddress;
        this.slots = slots;
        this.sitterSex = sitterSex;
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

    public ArrayList<TimeSlot> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<TimeSlot> slots) {
        this.slots = slots;
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
