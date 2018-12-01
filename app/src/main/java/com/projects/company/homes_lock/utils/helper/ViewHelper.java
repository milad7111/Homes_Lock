package com.projects.company.homes_lock.utils.helper;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.projects.company.homes_lock.R;

import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_SCAN_MODE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.SEARCHING_TIMEOUT_MODE;

/**
 * This is Helper Class helps Views
 */

public class ViewHelper {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private static Context mContext;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    //region Declare Methods
    public static void setContext(Context mContext) {
        ViewHelper.mContext = mContext;
    }

    public static void setFragment(AppCompatActivity parent, int containerId, Fragment fragment) {
        FragmentManager mFragmentManager = parent.getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();

        mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        mTransaction.add(containerId, fragment, fragment.getClass().getName());
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }

    public static void setLockStatusImage(ImageView imageViewLock, int lockStatus) {
        switch (lockStatus) {
            case 0:
                imageViewLock.setImageResource(R.drawable.ic_lock_open);
                break;
            case 1:
                imageViewLock.setImageResource(R.drawable.ic_lock_close);
                break;
            case 2:
                imageViewLock.setImageResource(R.drawable.ic_lock_idle);
                break;
        }
    }

    public static void setBleConnectionStatusImage(ImageView imageViewBle, boolean bleConnectionStatus) {
        imageViewBle.setImageResource(bleConnectionStatus ? R.drawable.ic_ble_connect : R.drawable.ic_ble_disconnect);
    }

    public static void setBatteryStatusImage(ImageView imgBatteryStatusLockPage, Integer batteryPercentage) {
        if (batteryPercentage < 25)
            imgBatteryStatusLockPage.setImageResource(R.drawable.ic_battery_zero);
        else if (batteryPercentage >= 30 && batteryPercentage < 60)
            imgBatteryStatusLockPage.setImageResource(R.drawable.ic_battery_low);
        else if (batteryPercentage >= 60 && batteryPercentage < 90)
            imgBatteryStatusLockPage.setImageResource(R.drawable.ic_battery_middle);
        else if (batteryPercentage >= 90)
            imgBatteryStatusLockPage.setImageResource(R.drawable.ic_battery_full);
        else
            imgBatteryStatusLockPage.setImageResource(R.drawable.ic_battery_full);
    }

    public static void setConnectionStatusImage(ImageView imgConnectionStatusLockPage, int wifiStatus, Boolean internetStatus, Integer wifiStrength) {
        if (wifiStatus == 0)
            imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_off_disable);
        else if (wifiStatus == 1)
            imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_off_enable);
        else {
            if (!internetStatus) {
                if (wifiStrength > -60)
                    imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_no_internet_full);
                else if (wifiStrength <= -60 && wifiStrength > -71)
                    imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_no_internet_middle);
                else if (wifiStrength <= -71 && wifiStrength > -85)
                    imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_no_internet_low);
                else // wifiStrength <= -85
                    imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_no_internet_zero);
            } else {
                if (wifiStrength > -60)
                    imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_internet_full);
                else if (wifiStrength <= -60 && wifiStrength > -71)
                    imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_internet_middle);
                else if (wifiStrength <= -71 && wifiStrength > -85)
                    imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_internet_low);
                else // wifiStrength <= -85
                    imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_internet_zero);
            }
        }
    }

    public static void setRSSIImage(ImageView imgBleDeviceRSSI, Integer RSSIPercentage) {
        if (RSSIPercentage < 25)
            imgBleDeviceRSSI.setImageResource(R.drawable.ic_rssi_zero);
        else if (RSSIPercentage >= 25 && RSSIPercentage < 50)
            imgBleDeviceRSSI.setImageResource(R.drawable.ic_rssi_low);
        else if (RSSIPercentage >= 50 && RSSIPercentage < 75)
            imgBleDeviceRSSI.setImageResource(R.drawable.ic_rssi_middle);
        else if (RSSIPercentage >= 75 && RSSIPercentage <= 100)
            imgBleDeviceRSSI.setImageResource(R.drawable.ic_rssi_full);
        else if (RSSIPercentage == SEARCHING_SCAN_MODE || RSSIPercentage == SEARCHING_TIMEOUT_MODE)
            imgBleDeviceRSSI.setImageDrawable(null);
    }

    static WindowManager.LayoutParams getDialogLayoutParams(Dialog dialog) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return layoutParams;
    }
    //endregion Declare Methods
}
