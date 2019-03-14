package com.projects.company.homes_lock.utils.helper;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.ederdoski.simpleble.models.BluetoothLE;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.utils.ble.CustomBluetoothLEHelper;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.projects.company.homes_lock.utils.helper.DataHelper.REQUEST_CODE_ACCESS_COARSE_LOCATION;
import static com.projects.company.homes_lock.utils.helper.DataHelper.subArrayByte;

public class BleHelper {

    //region Declare Constants
    public static final UUID SERVICE_UUID_SERIAL = UUID.fromString("927c9cb0-cd09-11e8-b568-0800200c9a66");
    public static final UUID CHARACTERISTIC_UUID_TX = UUID.fromString("927c9cb2-cd09-11e8-b568-0800200c9a66");
    public static final UUID CHARACTERISTIC_UUID_RX = UUID.fromString("927c9cb3-cd09-11e8-b568-0800200c9a66");

    public static final String LOCK_STATUS_LOCK = "lock";
    public static final String LOCK_STATUS_UNLOCK = "unlock";

    private static final String PREFS_LOCATION_NOT_REQUIRED = "location_not_required";
    private static final String PREFS_PERMISSION_REQUESTED = "permission_requested";

    public static final int SEARCHING_SCAN_MODE = 1000;
    public static final int SEARCHING_TIMEOUT_MODE = 2000;

    public static final boolean DOOR_INSTALLATION_SETTING_RIGHT_HANDED = true;
    public static final boolean DOOR_INSTALLATION_SETTING_LEFT_HANDED = false;

    public static final int LOCK_STAGES_NINETY_DEGREES = 0;
    public static final int LOCK_STAGES_ONE_STAGE = 1;
    public static final int LOCK_STAGES_TWO_STAGE = 2;
    public static final int LOCK_STAGES_THREE_STAGE = 3;

    public static final int TIMES_TO_SCAN_BLE_DEVICES = 3;
    //endregion Declare Constants

    //region Declare Objects
    //endregion Declare Objects

    //region Declare Methods

    /**
     * Checks whether Bluetooth is enabled.
     *
     * @return true if Bluetooth is enabled, false otherwise.
     */
    private static boolean isBleEnabled() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }

    /**
     * Checks for required permissions.
     *
     * @return true if permissions are already granted, false otherwise.
     */
    private static boolean isLocationPermissionsGranted(final Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Returns true if location permission has been requested at least twice and
     * user denied it, and checked 'Don't ask again'.
     *
     * @param activity the activity
     * @return true if permission has been denied and the popup will not come up any more, false otherwise
     */
    private static boolean isLocationPermissionDeniedForever(final Activity activity) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        return !isLocationPermissionsGranted(activity) // Location permission must be denied
                && preferences.getBoolean(PREFS_PERMISSION_REQUESTED, false) // Permission must have been requested before
                && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION); // This method should return false
    }

    /**
     * On some devices running Android Marshmallow or newer location services must be enabled in order to scan for Bluetooth LE devices.
     * This method returns whether the Location has been enabled or not.
     *
     * @return true on Android 6.0+ if location mode is different than LOCATION_MODE_OFF. It always returns true on Android versions prior to Marshmallow.
     */
    public static boolean isLocationEnabled(final Context context) {
        if (isMarshmallowOrAbove()) {
            int locationMode = Settings.Secure.LOCATION_MODE_OFF;
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (final Settings.SettingNotFoundException e) {
                // do nothing
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }

        return true;
    }

    /**
     * Location enabled is required on some phones running Android Marshmallow or newer (for example on Nexus and Pixel devices).
     *
     * @param context the context
     * @return false if it is known that location is not required, true otherwise
     */
    private static boolean isLocationRequired(final Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PREFS_LOCATION_NOT_REQUIRED, isMarshmallowOrAbove());
    }

    /**
     * The first time an app requests a permission there is no 'Don't ask again' checkbox and
     * {@link ActivityCompat#shouldShowRequestPermissionRationale(Activity, String)} returns false.
     * This situation is similar to a permission being denied forever, so to distinguish both cases
     * a flag needs to be saved.
     *
     * @param context the context
     */
    private static void markLocationPermissionRequested(final Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(PREFS_PERMISSION_REQUESTED, true).apply();
    }

    private static boolean isMarshmallowOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    static void enableLocation(Activity context) {
        final Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    private static void enableBluetooth(Activity activity) {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivity(enableIntent);
    }

    private static void grantLocationPermission(Activity context) {
        BleHelper.markLocationPermissionRequested(context);
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_COARSE_LOCATION);
    }

    private static void handlePermissionSettings(Activity context) {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

    public static byte[] createWriteMessage(String command, byte remain) {
        return mergeArrays(new byte[]{0x00, Byte.valueOf(String.valueOf(0x40 | remain))}, mergeArrays(command.getBytes(), new byte[]{0x00}));
    }

    public static byte[] createBleReadMessage(String key, int remain) {
        return mergeArrays(new byte[]{0x00, Byte.valueOf(String.valueOf(0x40 | remain))}, mergeArrays((("{\"" + key + "\":null}")).getBytes(), new byte[]{0x00}));
    }

    public static byte[] getBleCommandPart(byte[] command, int part, int remain) {
        return mergeArrays(
                mergeArrays(mergeArrays(new byte[]{command[0]},
                        new byte[]{Byte.valueOf(String.valueOf(0x40 | remain))}
                ), subArrayByte(
                        subArrayByte(command, 2, command.length - 2),
                        part * 17,
                        part * 17 + 16)),
                new byte[]{command[command.length - 1]});//every 20bytes data is a frame, 3 bytes is out of data
    }

    private static byte[] mergeArrays(byte[] firstArray, byte[] secondArray) {
        byte[] finalArray = new byte[firstArray.length + secondArray.length];
        System.arraycopy(firstArray, 0, finalArray, 0, firstArray.length);
        System.arraycopy(secondArray, 0, finalArray, firstArray.length, secondArray.length);

        return finalArray;
    }

    private byte createChecksum(byte[] commandArray) {
//            byte checksum = 0x00;
//
//            if(commandArray.length == 0)
//                return 0x00;
//
//            for(byte i=0; i<commandArray.length; i++) {
//                checksum = checksum + (commandArray[i] );
//            }
//
//            return(0xff - checksum);
//        }

        return 0x00;
    }

    public static void findDevices(Fragment fragment, CustomBluetoothLEHelper mBluetoothLEHelper) {
        if (mBluetoothLEHelper != null && !mBluetoothLEHelper.isScanning()) {
            mBluetoothLEHelper.setScanPeriod(1000);
            mBluetoothLEHelper.scanLeDevice(true);

            Handler mHandler = new Handler();
            mHandler.postDelayed(() -> {
                List<ScannedDeviceModel> tempList = getListOfScannedDevices(mBluetoothLEHelper);
                if (tempList.size() == 0)
                    ((IBleScanListener<Object>) fragment).onFindBleFault();
                else
                    ((IBleScanListener<Object>) fragment).onFindBleSuccess(tempList);
            }, mBluetoothLEHelper.getScanPeriod());
        }
    }

    private static List<ScannedDeviceModel> getListOfScannedDevices(CustomBluetoothLEHelper mBluetoothLEHelper) {
        List<ScannedDeviceModel> mScannedDeviceModelList = new ArrayList<>();

        for (BluetoothLE device : mBluetoothLEHelper.getListDevices())
            mScannedDeviceModelList.add(new ScannedDeviceModel(device));

        return mScannedDeviceModelList;
    }

    public static boolean getScanPermission(Fragment fragment) {
        if (isLocationRequired(fragment.getContext())) {
            if (isLocationPermissionsGranted(fragment.getContext())) {
                if (isBleEnabled())
                    return true;
                else enableBluetooth(Objects.requireNonNull(fragment.getActivity()));
            } else {
                final boolean deniedForever = isLocationPermissionDeniedForever(fragment.getActivity());
                if (!deniedForever)
                    grantLocationPermission(fragment.getActivity());

                if (deniedForever)
                    handlePermissionSettings(Objects.requireNonNull(fragment.getActivity()));
            }
        } else {
            if (isBleEnabled())
                return true;
            else enableBluetooth(Objects.requireNonNull(fragment.getActivity()));
        }

        return false;
    }
    //endregion Declare Methods
}
