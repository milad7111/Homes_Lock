package com.projects.company.homes_lock.utils.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private static Animation ringImageViewAnimation = null;
    public static boolean changeLockStatus = false;
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

//    public static void setIsLockedImage(ImageView imageViewIsLocked, int isLocked) {
//        switch (isLocked) {
//            case 0:
//                imageViewIsLocked.setImageResource(R.drawable.ic_homes_lock_open);
//                break;
//            case 1:
//                imageViewIsLocked.setImageResource(R.drawable.ic_homes_lock_close);
//                break;
//            case 2:
//                imageViewIsLocked.setImageResource(R.drawable.ic_homes_lock_idle);
//                break;
//        }
//    }

    public static void setIsLockedImage(ImageView imageViewCenter, ImageView imageViewRing, ImageView imageViewIsLocked, int isLocked) {
        switch (isLocked) {
            case 0:
                animatedImageResourceChange(
                        imageViewCenter,
                        imageViewRing,
                        imageViewIsLocked,
                        R.drawable.ic_lock_open,
                        R.drawable.ic_ring_unlock,
                        R.drawable.ic_homes_lock_open
                );

                if (changeLockStatus)
                    disableLockCommandRingImageView(imageViewRing);
                break;
            case 1:
                animatedImageResourceChange(
                        imageViewCenter,
                        imageViewRing,
                        imageViewIsLocked,
                        R.drawable.ic_lock_close,
                        R.drawable.ic_ring_lock,
                        R.drawable.ic_homes_lock_close
                );

                if (changeLockStatus)
                    disableLockCommandRingImageView(imageViewRing);
                break;
            case 2:
                imageViewIsLocked.setImageResource(R.drawable.ic_homes_lock_idle);
                imageViewCenter.setImageResource(R.drawable.ic_lock_idle);
                imageViewRing.setImageResource(R.drawable.ic_ring_idle);
                break;
        }
    }

    public static void enableLockCommandRingImageView(Activity activity, ImageView imageViewRing, int lockCommand) {
        switch (lockCommand) {
            case 0: // UNLOCK
                rotateImageView(activity, imageViewRing, false);
                break;
            case 1: //LOCK
                rotateImageView(activity, imageViewRing, true);
                break;
        }
    }

    public static void disableLockCommandRingImageView(ImageView imgIsLockedLockPageRing) {
        if (ringImageViewAnimation != null) {
            ringImageViewAnimation.cancel();
            ringImageViewAnimation.reset();
            ringImageViewAnimation = null;
            imgIsLockedLockPageRing.clearAnimation();
            imgIsLockedLockPageRing.setAnimation(null);
            changeLockStatus = false;
        }
    }

    private static void rotateImageView(Activity activity, ImageView imageView, boolean clockWise) {
        ringImageViewAnimation = AnimationUtils.loadAnimation(activity, clockWise ? R.anim.rotate_clock_wise : R.anim.rotate_counter_clock_wise);
        imageView.startAnimation(ringImageViewAnimation);
    }

    public static void setAvailableBleDevicesStatusImage(
            ImageView imageViewConnectedClients, ImageView imageViewConnectedClientsRing, ImageView imageViewConnectedClientsCenter,
            int connectedClientsCount, boolean setDefault) {
        if (connectedClientsCount == 0) {
            imageViewConnectedClients.setImageResource(
                    setDefault ?
                            R.drawable.ic_homes_lock_idle :
                            R.drawable.ic_homes_lock_open);
            imageViewConnectedClientsRing.setImageResource(
                    setDefault ?
                            R.drawable.ic_gateway_ring_idle :
                            R.drawable.ic_gateway_ring_disconnect);
            imageViewConnectedClientsCenter.setImageResource(
                    setDefault ?
                            R.drawable.ic_gateway_connection_status_idle :
                            R.drawable.ic_gateway_connection_status_disconnect);
        } else {
            imageViewConnectedClients.setImageResource(
                    setDefault ?
                            R.drawable.ic_homes_lock_idle :
                            R.drawable.ic_homes_lock_close);
            imageViewConnectedClientsRing.setImageResource(
                    setDefault ?
                            R.drawable.ic_gateway_ring_idle :
                            R.drawable.ic_gateway_ring_connect);
            imageViewConnectedClientsCenter.setImageResource(
                    setDefault ?
                            R.drawable.ic_gateway_connection_status_idle :
                            R.drawable.ic_gateway_connection_status_connect);
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

    public static void setConnectedClientsStatusImage(
            ImageView imgConnectionStatusLockPage, boolean setDefault, int connectedClientsCount) {
        if (connectedClientsCount <= 0) // -1 added to connectedClientsCount because my phone always is one of connected devices
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null)
            view = new View(activity);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setTypeface(TextView view, String font) {
//        view.setTypeface(Typeface.createFromAsset(view.getContext().getAssets(), String.format("fonts/%s.ttf", font)));
    }

    public static void setTypeface(Button view, String font) {
        view.setTypeface(Typeface.createFromAsset(view.getContext().getAssets(), String.format("fonts/%s.ttf", font)));
    }

    private static void animatedImageResourceChange(
            ImageView imageViewCenter, ImageView imageViewRing, ImageView imageViewIsLocked,
            int imageViewCenterResource, int imageViewRingResource, int imageViewIsLockedResource) {

        Animation anim_out_imageViewCenter = AnimationUtils.loadAnimation(imageViewCenter.getContext(), android.R.anim.fade_out);
        Animation anim_in_imageViewCenter = AnimationUtils.loadAnimation(imageViewCenter.getContext(), android.R.anim.fade_in);

        anim_out_imageViewCenter.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageViewCenter.setImageResource(imageViewCenterResource);
                imageViewCenter.startAnimation(anim_in_imageViewCenter);
            }
        });

        Animation anim_out_imageViewRing = AnimationUtils.loadAnimation(imageViewRing.getContext(), android.R.anim.fade_out);
        Animation anim_in_imageViewRing = AnimationUtils.loadAnimation(imageViewRing.getContext(), android.R.anim.fade_in);

        anim_out_imageViewRing.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageViewRing.setImageResource(imageViewRingResource);
                imageViewRing.startAnimation(anim_in_imageViewRing);
            }
        });

        Animation anim_out_imageViewIsLocked = AnimationUtils.loadAnimation(imageViewIsLocked.getContext(), android.R.anim.fade_out);
        Animation anim_in_imageViewIsLocked = AnimationUtils.loadAnimation(imageViewIsLocked.getContext(), android.R.anim.fade_in);

        anim_out_imageViewIsLocked.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageViewIsLocked.setImageResource(imageViewIsLockedResource);
                imageViewIsLocked.startAnimation(anim_in_imageViewIsLocked);
            }
        });

        imageViewCenter.startAnimation(anim_out_imageViewCenter);
        imageViewRing.startAnimation(anim_out_imageViewRing);
        imageViewIsLocked.startAnimation(anim_out_imageViewIsLocked);
    }
    //endregion Declare Methods
}
