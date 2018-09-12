package com.projects.company.homes_lock.utils;

import java.util.List;

public class DataHelper {
    public static boolean isInstanceOf(Object object, String className) {
        try {
            if (object instanceof List)
                for (Object obj : (List) object) {
                    if (Class.forName(className).isInstance(obj))
                        continue;
                    else
                        return false;
                }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
