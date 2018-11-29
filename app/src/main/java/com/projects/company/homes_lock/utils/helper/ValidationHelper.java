package com.projects.company.homes_lock.utils.helper;

public class ValidationHelper {

    public static boolean validateEquality(String param1, String param2) {
        if (param1.equals(param2))
            return true;

        return false;
    }
}
