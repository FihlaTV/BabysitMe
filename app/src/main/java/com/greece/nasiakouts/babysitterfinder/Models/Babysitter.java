package com.greece.nasiakouts.babysitterfinder.Models;

import java.util.ArrayList;

public class Babysitter extends User {
    private String streetAddress;
    private double charges;
    private String currency;

    private int maxKids;
    private double minAge;
    private String introduction;
    private String photoUri;

    public Babysitter(String emailAddress, String password) {
        super(emailAddress, password);
    }

    public Babysitter(User user, String streetAddress,
                      double charges, String currency, ArrayList<TimeSlot> workingTimeSlots) {
        super(user);
        this.streetAddress = streetAddress;
        this.charges = charges;
        this.currency = currency;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public double getCharges() {
        return charges;
    }

    public void setCharges(double charges) {
        this.charges = charges;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getMaxKids() {
        return maxKids;
    }

    public void setMaxKids(int maxKids) {
        this.maxKids = maxKids;
    }

    public double getMinAge() {
        return minAge;
    }

    public void setMinAge(double minAge) {
        this.minAge = minAge;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
