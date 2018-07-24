package com.greece.nasiakouts.babysitterfinder.Models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class User implements Serializable{

    private String emailAddress;

    @Exclude
    String password;

    private String fullName;
    private String phoneNumber;
    private long dateBornTimestamp;
    private int sexCode;

    public User() {
    }

    public User(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public User(User user) {
        this.emailAddress = user.emailAddress;
        this.password = user.password;
        this.fullName = user.fullName;
        this.phoneNumber = user.phoneNumber;
        this.dateBornTimestamp = user.dateBornTimestamp;
    }

    public int getAge() {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.setTime(new Date(dateBornTimestamp));
        return currentYear - cal.get(Calendar.YEAR);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    @Exclude
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

    public long getDateBornTimestamp() {
        return dateBornTimestamp;
    }

    public void setDateBornTimestamp(long dateBornTimestamp) {
        this.dateBornTimestamp = dateBornTimestamp;
    }

    public void setDateBornTimestamp(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        dateBornTimestamp = cal.getTime().getTime();
    }

    public String getFormatedDateBorn() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.US);
        return sdf.format(new Timestamp(dateBornTimestamp));
    }

    public int getSexCode() {
        return sexCode;
    }

    public void setSexCode(int sexCode) {
        this.sexCode = sexCode;
    }
}
