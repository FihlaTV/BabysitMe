package com.greece.nasiakouts.babysitterfinder.Models;

import java.io.Serializable;

public class User implements Serializable{
    private String emailAddress;
    private String password;

    private String fullName;
    private String phoneNumber;
    private String dateBorn;

    public User(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public User(User user) {
        this.emailAddress = user.emailAddress;
        this.password = user.password;
        this.fullName = user.fullName;
        this.phoneNumber = user.phoneNumber;
        this.dateBorn = user.dateBorn;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateBorn() {
        return dateBorn;
    }

    public void setDateBorn(String dateBorn) {
        this.dateBorn = dateBorn;
    }
}
