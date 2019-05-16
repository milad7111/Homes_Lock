package com.projects.company.homes_lock.utils.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {

    //region Constants
    public static final int VALIDATION_OK = 0;
    public static final int VALIDATION_EMPTY = 1;

    public static final int VALIDATION_REGISTER_PASS_LENGTH = 100;
    public static final int VALIDATION_REGISTER_NO_MATCH = 101;

    public static final int VALIDATION_EMAIL_FORMAT = 200;

    public static final int VALIDATION_MOBILE_NUMBER_FORMAT = 300;
    //endregion Constants

    //region Public Methods
    public static int validateUserName(String userName) {
        return checkEmptyValue(userName) ? VALIDATION_EMPTY : VALIDATION_OK;
    }

    public static int validateUserEmail(String email) {
        if (checkEmptyValue(email))
            return VALIDATION_EMPTY;
        else if (!checkEmailFormat(email))
            return VALIDATION_EMAIL_FORMAT;

        return VALIDATION_OK;
    }

    public static int validateDeviceNameForUser(String name) {
        return (checkEmptyValue(name)) ? VALIDATION_EMPTY : VALIDATION_OK;
    }

    public static int validateMobileNumber(String mobileNumber) {
        return !checkMobileNumberFormat(mobileNumber) ? VALIDATION_MOBILE_NUMBER_FORMAT : VALIDATION_OK;
    }

    public static int validateUserPassword(String password1, String password2) {
        if (!checkUserPasswordLength(password1))
            return VALIDATION_REGISTER_PASS_LENGTH;
        else if (!checkEquality(password1, password2))
            return VALIDATION_REGISTER_NO_MATCH;

        return VALIDATION_OK;
    }
    //endregion Public Methods

    // region Public Methods
    private static boolean checkEquality(String param1, String param2) {
        return param1.equals(param2);
    }

    private static boolean checkUserPasswordLength(String password) {
        return password.length() >= 8;
    }

    private static boolean checkEmptyValue(String param) {
        return param.isEmpty();
    }

    private static boolean checkEmailFormat(String email) {
        String regExpn = "^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(]?)$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private static boolean checkMobileNumberFormat(String mobileNumber) {
        Pattern pattern = Pattern.compile("^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$");
        Matcher matcher = pattern.matcher(mobileNumber);

        return matcher.matches();
    }
    //endregion Public Methods
}
