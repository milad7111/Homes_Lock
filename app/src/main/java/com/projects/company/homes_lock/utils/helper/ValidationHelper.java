package com.projects.company.homes_lock.utils.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {

    public static final int VALIDATION_OK = 0;

    public static final int VALIDATION_REGISTER_PASS_LENGTH = 100;
    public static final int VALIDATION_REGISTER_NO_MATCH = 101;

    public static final int VALIDATION_EMAIL_FORMAT = 200;

    public static final int VALIDATION_MOBILE_NUMBER_FORMAT = 300;

    public static final int VALIDATION_USERNAME_LENGTH = 400;

    //region Public Methods
    public static int validateUserName(String userName) {
        if (!checkUserNameLength(userName))
            return VALIDATION_USERNAME_LENGTH;

        return VALIDATION_OK;
    }

    public static int validateUserEmail(String email) {
        if (!checkEmailFormat(email))
            return VALIDATION_EMAIL_FORMAT;

        return VALIDATION_OK;
    }

    public static int validateMobileNumber(String mobileNumber) {
        if (!checkMobileNumberFormat(mobileNumber))
            return VALIDATION_MOBILE_NUMBER_FORMAT;

        return VALIDATION_OK;
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

    private static boolean checkUserNameLength(String userName) {
        return userName.length() >= 4;
    }

    private static boolean checkEmailFormat(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

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
