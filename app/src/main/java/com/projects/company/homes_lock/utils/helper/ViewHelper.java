package com.projects.company.homes_lock.utils.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.projects.company.homes_lock.R;

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
        mTransaction.add(containerId, fragment, fragment.getTag());
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }

    public static void setLockStatusImage(ImageView imageViewLock, boolean lockStatus) {
        imageViewLock.setImageResource(lockStatus ? R.drawable.ic_lock_close : R.drawable.ic_lock_open);
    }

    public static void setBleConnectionStatusImage(ImageView imageViewBle, boolean bleConnectionStatus) {
        imageViewBle.setImageResource(bleConnectionStatus ? R.drawable.ic_ble_connect : R.drawable.ic_ble_disconnect);
    }

    public static void setBatteryStatusImage(ImageView imgBatteryStatusLockPage, Integer batteryStatus) {
        if (batteryStatus < 25)
            imgBatteryStatusLockPage.setImageResource(R.drawable.ic_battery_zero);
        else if (batteryStatus >= 30 && batteryStatus < 60)
            imgBatteryStatusLockPage.setImageResource(R.drawable.ic_battery_low);
        else if (batteryStatus >= 60 && batteryStatus < 90)
            imgBatteryStatusLockPage.setImageResource(R.drawable.ic_battery_middle);
        else if (batteryStatus >= 90)
            imgBatteryStatusLockPage.setImageResource(R.drawable.ic_battery_full);
    }

    public static void setConnectionStatusImage(ImageView imgConnectionStatusLockPage, Boolean wifiStatus, Boolean internetStatus, Integer wifiStrength) {
        if (!wifiStatus)
            imgConnectionStatusLockPage.setImageResource(R.drawable.ic_wifi_off);
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
    //endregion Declare Methods
}
