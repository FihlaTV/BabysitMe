package com.greece.nasiakouts.babysitterfinder;

public class Constants {

    // Account info - Inputs Indices
    public static final int INDEX_MAIL_INPUT = 0;
    public static final int INDEX_PASSWORD_INPUT = 1;
    public static final int INDEX_CONFIRM_PASSWORD_INPUT = 2;

    // Personal info - Inputs Indices
    public static final int INDEX_NAME_INPUT = 0;
    public static final int INDEX_PHONE_INPUT = 1;
    public static final int INDEX_DATE_BORN_INPUT = 2;

    // Working info - Inputs Indices
    public static final int INDEX_STREET_ADDRESS_INPUT = 0;
    public static final int INDEX_CHARGES_INPUT = 1;
    public static final int INDEX_CURRENCY_INPUT = 2;

    // Sitter Additional info - Inputs Indices
    public static final int INDEX_MAX_KIDS_INPUT = 0;
    public static final int INDEX_MIN_AGE_INPUT = 1;
    public static final int INDEX_INTRODUCTION_INPUT = 2;

    // Fragments
    public static final int ACCOUNT_INFO_FRAGMENT_SEQ = 0;
    public static final int PERSONAL_INFO_FRAGMENT_SEQ = 1;
    public static final int WORKING_INFO_FRAGMENT_SEQ = 2;
    public static final int SITTER_ADDITIONAL_INFO_FRAGMENT_SEQ = 3;

    // Find Sitter info - Inputs Indices
    public static final int INDEX_TOTAL_KIDS_INPUT = 0;
    public static final int INDEX_YOUNGEST_KID_INPUT = 1;
    public static final int INDEX_STREET_INPUT = 2;

    // Find Sitter - Radios Indices
    public static final int INDEX_RADIO_FEMALE = 0;
    public static final int INDEX_RADIO_MALE = 1;
    public static final int INDEX_RADIO_ANY = 2;

    // RV Available Sitters
    public static final int INDEX_NAME = 0;
    public static final int INDEX_PRICE = 1;
    public static final int INDEX_CURRENCY = 2;

    // RV Appointments
    public static final int INDEX_PERIOD = 0;
    public static final int INDEX_ADDRESS = 1;
    public static final int INDEX_INFO = 2;

    // Intent Codes
    public static final String INT_CODE = "int";
    public static final String UID_CODE = "registered_uid";

    public static final int SITTER_MODE = 1;
    public static final int USER_MODE = 2;

    public static final String FIREBASE_USER_ALL_INFO = "all_info";
    public static final String FIREBASE_USER_TYPE = "type";
    public static final String FIREBASE_SITTER_ADDRESS_ID_MAP = "sitter_address_id_map";
    public static final String FIREBASE_SITTERS = "sitter";
    public static final String FIREBASE_USERS = "user";
    public static final String FIREBASE_WORKING_DAYS = "working_day";
    public static final String FIREBASE_APPOINTMENTS = "appointment";
    public static final String FIREBASE_ALL_DAY = "allDay";
    public static final String FIREBASE_TIME_FROM = "timeFrom";
    public static final String FIREBASE_REGISTRATION_TOKEN = "registrationToken";

    public static final int USER_FEMALE = 0;
    public static final int USER_MALE = 1;
    public static final int USER_ANY = 2;

    public static final int ADD_TIMESLOT_REQUEST_CODE = 100;
    public static final int TAKE_PHOTO_REQUEST_CODE = 200;
    public static final int FROM_GALLERY_REQUEST_CODE = 201;
    public static final String DASH = " - ";
    public static final String SLASH = "/";
    public static final String ANOKATOTELEIA = ":";

    public static final int MIN_AGE_OF_USER = 17;
    public static final int DAYS_IN_DECEMBER = 31;

}
