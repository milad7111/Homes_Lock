package com.projects.company.homes_lock.utils.helper;

import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.InternetStatusMode;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.SecurityAlarm;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.projects.company.homes_lock.models.datamodels.ble.InternetStatusMode.CONNECTED;
import static com.projects.company.homes_lock.models.datamodels.ble.InternetStatusMode.JUST_WIFI;
import static com.projects.company.homes_lock.models.datamodels.ble.InternetStatusMode.NOT_CONNECTED;
import static com.projects.company.homes_lock.models.datamodels.ble.SecurityAlarm.ATTENTION;
import static com.projects.company.homes_lock.models.datamodels.ble.SecurityAlarm.OPEN;
import static com.projects.company.homes_lock.models.datamodels.ble.SecurityAlarm.SECURE;
import static com.projects.company.homes_lock.models.datamodels.ble.SecurityAlarm.UNSECURE;

public class DataHelper {

    //region Declare Constants
    public static final int CHANGE_ONLINE_PASSWORD = 1000;
    public static final int CHANGE_PAIRING_PASSWORD = 2000;

    public static final int LOCK_MEMBERS_SYNCING_MODE = -1;
    public static final int MEMBER_STATUS_PRIMARY_ADMIN = 1000;
    public static final int MEMBER_STATUS_SECONDARY_ADMIN = 2000;
    public static final int MEMBER_STATUS_NOT_ADMIN = 3000;

    static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 0;

    public static final int NOT_DEFINED_INTEGER_NUMBER = -1;
    //endregion Declare Constants

    //region Declare Methods
    public static int getNibble(byte number, boolean highNibble) {
        return highNibble ? (number & 0xf0) >> 4 : number & 0x0f;
    }

    public static boolean isInstanceOfList(Object object, String className) {
        try {
            if (object instanceof List && ((List) object).size() != 0) {
                for (Object obj : (List) object) {
                    if (Class.forName(className).isInstance(obj))
                        continue;
                    else
                        return false;
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmptyList(Object object) {
        try {
            if (object instanceof List && ((List) object).size() == 0)
                return true;

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean containsValueInListOfObjects(List<?> listOfObjects, String substring) {
        // This method search a list of object's Model (every model has getSpecialValue method)
        // Return TRUE if find special substring else return false
        // See com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel class
        if (listOfObjects.size() == 0)
            return false;
        else if (listOfObjects.get(0) instanceof ScannedDeviceModel)
            for (int i = 0; i < listOfObjects.size(); i++)
                if (((ScannedDeviceModel) listOfObjects.get(i)).getSpecialValue().equals(substring))
                    return true;

        return false;
    }

    public static ArrayList<String> convertListOfObjectsToArrayList(List<?> listOfObjects) {
        ArrayList<String> mArray = new ArrayList<>();
        if (listOfObjects.size() == 0)
            return mArray;
        else if (listOfObjects.get(0) instanceof ScannedDeviceModel)
            for (int i = 0; i < listOfObjects.size(); i++)
                mArray.add(((ScannedDeviceModel) listOfObjects.get(i)).getName());

        return mArray;
    }

    public static Object convertJsonToObject(String data, String className) {
        try {
            return new Gson().fromJson(data, (Type) Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
            return new Device();
        }
    }

    public static String getLockBriefStatusText(boolean isLocked, boolean isDoorClosed) {
        switch (getSecurityAlarmMode(isLocked, isDoorClosed)) {
            case SECURE:
                return "Door is Secure";
            case UNSECURE:
                return "Door is UnSecure";
            case OPEN:
                return "Door is Open";
            case ATTENTION:
                return "Attention Required";
            default:
                return null;
        }
    }

    public static String getGatewayBriefStatusText(boolean internetStatus, boolean wifiStatus) {
        switch (getInternetStatusMode(internetStatus, wifiStatus)) {
            case CONNECTED:
                return "Connected to server";
            case JUST_WIFI:
                return "No internet";
            case NOT_CONNECTED:
                return "No network";
            default:
                return null;
        }
    }

    public static int getGatewayBriefStatusColor(boolean internetStatus, boolean wifiStatus) {
        switch (getInternetStatusMode(internetStatus, wifiStatus)) {
            case CONNECTED:
                return R.color.md_green_700;
            case JUST_WIFI:
                return R.color.md_yellow_400;
            case NOT_CONNECTED:
                return R.color.md_red_500;
            default:
                return R.color.md_black_1000;
        }
    }

    private static InternetStatusMode getInternetStatusMode(boolean internetStatus, boolean wifiStatus) {
        if (internetStatus)
            return CONNECTED;
        else {
            if (wifiStatus)
                return JUST_WIFI;
            else
                return NOT_CONNECTED;
        }
    }

    public static int getLockBriefStatusColor(boolean isLocked, boolean isDoorClosed) {
        switch (getSecurityAlarmMode(isLocked, isDoorClosed)) {
            case SECURE:
                return R.color.md_green_700;
            case UNSECURE:
                return R.color.md_yellow_400;
            case OPEN:
                return R.color.md_white_1000;
            case ATTENTION:
                return R.color.md_red_500;
            default:
                return R.color.md_black_1000;
        }
    }

    private static SecurityAlarm getSecurityAlarmMode(boolean isLocked, boolean isDoorClosed) {
        if (isLocked)
            if (isDoorClosed)
                return SECURE;
            else
                return ATTENTION;
        else {
            if (isDoorClosed)
                return UNSECURE;
            else
                return OPEN;
        }
    }

    public static byte[] subArrayByte(byte[] data, int start, int end) {
        byte[] temp = new byte[end - start + 1]; //contains data[start] and data[end]

        int i = 0;
        for (int j = start; j <= end; j++)
            temp[i++] = data[j];

        return temp;
    }

    public static int calculateRSSI(int rssi) {
        return (int) (100.0f * (127.0f + rssi) / (127.0f + 20.0f));
    }

    public static int getRandomPercentNumber(int level, int total) {
        return getRandomNumber((level - 1) * (100 / total), level * (100 / total));
    }

    private static int getRandomNumber(int min, int max) {
        if (min >= max)
            return -1;

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    //endregion Declare Methods
}
