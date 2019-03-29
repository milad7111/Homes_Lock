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

import java.util.Objects;

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

    public static void addFragment(AppCompatActivity parent, int containerId, Fragment fragment) {
        FragmentManager mFragmentManager = parent.getSupportFragmentManager();
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();

        mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        mTransaction.add(containerId, fragment, fragment.getClass().getName());
        mTransaction.addToBackStack(fragment.getClass().getName());
        mTransaction.commit();
    }

    public static void setIsLockedImage(ImageView imageViewIsLocked, int isLocked) {
        switch (isLocked) {
            case 0:
                imageViewIsLocked.setImageResource(R.drawable.ic_homes_lock_open);
                break;
            case 1:
                imageViewIsLocked.setImageResource(R.drawable.ic_homes_lock_close);
                break;
            case 2:
                imageViewIsLocked.setImageResource(R.drawable.ic_homes_lock_idle);
                break;
        }
    }

    public static void setAvailableBleDevicesStatusImage(ImageView imageViewConnectedDevices, int connectedDevicesCount, boolean setDefault) {
        if (setDefault) {
            if (connectedDevicesCount == 0)
                imageViewConnectedDevices.setImageResource(R.drawable.ic_invalid_connected_devices);
            else
                imageViewConnectedDevices.setImageResource(R.drawable.ic_invalid_connected_devices);
        } else {
            if (connectedDevicesCount == 0)
                imageViewConnectedDevices.setImageResource(R.drawable.ic_valid_no_connected_devices);
            else
                imageViewConnectedDevices.setImageResource(R.drawable.ic_valid_connected_devices);
        }
    }

    public static void setBleConnectionStatusImage(ImageView imageViewBle, boolean bleConnectionStatus) {
        imageViewBle.setImageResource(bleConnectionStatus ? R.drawable.ic_ble_connect : R.drawable.ic_ble_disconnect);
    }

    public static void setBleMoreInfoImage(ImageView imgMoreInfoLockPage, boolean setDefault) {
        imgMoreInfoLockPage.setImageResource(setDefault ? R.drawable.ic_invalid_more_info : R.drawable.ic_valid_more_info);
    }

    public static void setBatteryStatusImage(boolean setDefault, ImageView imgBatteryStatusLockPage, Integer batteryPercentage) {
        if (batteryPercentage >= 0 && batteryPercentage < 15)
            imgBatteryStatusLockPage.setImageResource(setDefault ? R.drawable.ic_invalid_battery_zero : R.drawable.ic_valid_battery_zero);
        else if (batteryPercentage >= 15 && batteryPercentage < 25)
            imgBatteryStatusLockPage.setImageResource(setDefault ? R.drawable.ic_invalid_battery_very_low : R.drawable.ic_valid_battery_very_low);
        else if (batteryPercentage >= 25 && batteryPercentage < 45)
            imgBatteryStatusLockPage.setImageResource(setDefault ? R.drawable.ic_invalid_battery_low : R.drawable.ic_valid_battery_low);
        else if (batteryPercentage >= 45 && batteryPercentage < 65)
            imgBatteryStatusLockPage.setImageResource(setDefault ? R.drawable.ic_invalid_battery_middle : R.drawable.ic_valid_battery_middle);
        else if (batteryPercentage >= 65 && batteryPercentage < 85)
            imgBatteryStatusLockPage.setImageResource(setDefault ? R.drawable.ic_invalid_battery_high : R.drawable.ic_valid_battery_high);
        else if (batteryPercentage >= 85)
            imgBatteryStatusLockPage.setImageResource(setDefault ? R.drawable.ic_invalid_battery_full : R.drawable.ic_valid_battery_full);
    }

    public static void setGatewayInternetConnectionStatusImage(
            ImageView imgConnectionStatusLockPage, boolean setDefault, boolean wifiStatus, boolean internetStatus, Integer wifiStrength) {
        if (!wifiStatus)
            imgConnectionStatusLockPage.setImageResource(setDefault ? R.drawable.ic_invalid_wifi_off : R.drawable.ic_valid_wifi_off);
        else {
            if (!internetStatus) {
                if (wifiStrength > -60)
                    imgConnectionStatusLockPage.setImageResource(setDefault ?
                            R.drawable.ic_invalid_wifi_no_internet_full : R.drawable.ic_valid_wifi_no_internet_full);
                else if (wifiStrength > -71)
                    imgConnectionStatusLockPage.setImageResource(setDefault ?
                            R.drawable.ic_invalid_wifi_no_internet_middle : R.drawable.ic_valid_wifi_no_internet_middle);
                else if (wifiStrength > -85)
                    imgConnectionStatusLockPage.setImageResource(setDefault ?
                            R.drawable.ic_invalid_wifi_no_internet_low : R.drawable.ic_valid_wifi_no_internet_low);
                else
                    imgConnectionStatusLockPage.setImageResource(setDefault ?
                            R.drawable.ic_invalid_wifi_no_internet_zero : R.drawable.ic_valid_wifi_no_internet_zero);
            } else {
                if (wifiStrength > -60)
                    imgConnectionStatusLockPage.setImageResource(setDefault ?
                            R.drawable.ic_invalid_wifi_internet_full : R.drawable.ic_valid_wifi_internet_full);
                else if (wifiStrength > -71)
                    imgConnectionStatusLockPage.setImageResource(setDefault ?
                            R.drawable.ic_invalid_wifi_internet_middle : R.drawable.ic_valid_wifi_internet_middle);
                else if (wifiStrength > -85)
                    imgConnectionStatusLockPage.setImageResource(setDefault ?
                            R.drawable.ic_invalid_wifi_internet_low : R.drawable.ic_valid_wifi_internet_low);
                else
                    imgConnectionStatusLockPage.setImageResource(setDefault ?
                            R.drawable.ic_invalid_wifi_internet_zero : R.drawable.ic_valid_wifi_internet_zero);
            }
        }
    }

    public static void setConnectedDevicesStatusImage(
            ImageView imgConnectionStatusLockPage, boolean setDefault, int connectedDevicesCount) {
        if (connectedDevicesCount - 1 <= 0) // -1 added to connectedDevicesCount because my phone always is one of connected devices
            imgConnectionStatusLockPage.setImageResource(setDefault ?
                    R.drawable.ic_invalid_connected_devices_not_exist : R.drawable.ic_valid_connected_devices_not_exist);
        else
            imgConnectionStatusLockPage.setImageResource(setDefault ?
                    R.drawable.ic_invalid_connected_devices_exist : R.drawable.ic_valid_connected_devices_exist);
    }

    public static void setRSSIImage(ImageView imgBleDeviceRSSI, Integer RSSIPercentage) {
        if (RSSIPercentage >= 0 && RSSIPercentage < 25)
            imgBleDeviceRSSI.setImageResource(R.drawable.ic_valid_rssi_zero);
        else if (RSSIPercentage >= 25 && RSSIPercentage < 50)
            imgBleDeviceRSSI.setImageResource(R.drawable.ic_valid_rssi_low);
        else if (RSSIPercentage >= 50 && RSSIPercentage < 75)
            imgBleDeviceRSSI.setImageResource(R.drawable.ic_valid_rssi_middle);
        else if (RSSIPercentage >= 75 && RSSIPercentage <= 100)
            imgBleDeviceRSSI.setImageResource(R.drawable.ic_valid_rssi_full);
        else if (RSSIPercentage == SEARCHING_SCAN_MODE || RSSIPercentage == SEARCHING_TIMEOUT_MODE)
            imgBleDeviceRSSI.setImageDrawable(null);
        else
            imgBleDeviceRSSI.setImageResource(R.drawable.ic_valid_rssi_zero);
    }

    public static void setLockPositionStatusImage(ImageView lockPositionStatusImage, boolean status) {
        lockPositionStatusImage.setImageResource(status ? R.drawable.ic_checked_status_true : R.drawable.ic_checked_status_false);
    }

    public static WindowManager.LayoutParams getDialogLayoutParams(Dialog dialog) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return layoutParams;
    }
    //endregion Declare Methods
}
