package com.greece.nasiakouts.babysitterfinder.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.greece.nasiakouts.babysitterfinder.Utils.Constants;

public class Appointment implements Parcelable {
    private int totalKids;
    private double minAge;
    private String streetAddress;
    private TimeSlot slot;
    private int sitterSex;
    private double totalCost;

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

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

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

    protected Appointment(Parcel in) {
        this.totalKids = in.readInt();
        this.minAge = in.readDouble();
        this.streetAddress = in.readString();
        this.slot = in.readParcelable(TimeSlot.class.getClassLoader());
        this.sitterSex = in.readInt();
        this.totalCost = in.readDouble();
        this.acceptedBySitter = in.readInt() == 1;
        this.sitterId = in.readString();
        this.customerId = in.readString();
    }

    public double getTotalCost() {
        return totalCost;
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

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setTotalCostUsingPerHour(double perHourCost) {
        if (slot.isAllDay()) {
            totalCost = Constants.MULTIPLIER_WHEN_ALL_DAY * perHourCost;
        } else {
            totalCost = Math.ceil(slot.getTimeTo() - slot.getTimeTo()) * perHourCost;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(totalKids);
        dest.writeDouble(minAge);
        dest.writeString(streetAddress);
        dest.writeParcelable(slot, i);
        dest.writeInt(sitterSex);
        dest.writeDouble(totalCost);
        dest.writeInt(acceptedBySitter ? 1 : 0);
        dest.writeString(sitterId);
        dest.writeString(customerId);
    }
}
