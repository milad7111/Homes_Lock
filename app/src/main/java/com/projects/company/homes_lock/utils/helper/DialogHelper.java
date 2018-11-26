package com.projects.company.homes_lock.utils.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.ui.device.activity.CustomDeviceAdapter;
import com.projects.company.homes_lock.ui.device.activity.LockActivity;
import com.projects.company.homes_lock.ui.device.fragment.addlock.AddLockFragment;
import com.projects.company.homes_lock.utils.ble.BleDeviceAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.projects.company.homes_lock.ui.device.activity.LockActivity.mBluetoothLEHelper;
import static com.projects.company.homes_lock.utils.helper.BleHelper.FINDING_BLE_DEVICES_SCAN_MODE;

public class DialogHelper {

    //region Declare Variables
    private static boolean saveLockAfterPaired;
    //endregion Declare Variables

    //region Declare Objects
    private static ProgressDialog mProgressDialog;
    private static BleDeviceAdapter mBleDeviceAdapter;
    private static Dialog addNewLockDialogOffline;
    //endregion Declare Objects

    //region Declare Methods
    public static Dialog handleShowListOfAvailableBleDevicesDialog(Fragment fragment, List<ScannedDeviceModel> devices) {
        if (addNewLockDialogOffline == null) {
            addNewLockDialogOffline = new Dialog(fragment.getContext());
            addNewLockDialogOffline.requestWindowFeature(Window.FEATURE_NO_TITLE);
            addNewLockDialogOffline.setContentView(R.layout.dialog_available_devices);

            if (mBleDeviceAdapter == null)
                mBleDeviceAdapter = new BleDeviceAdapter(fragment, devices);

            RecyclerView rcvDialogAvailableDevices = addNewLockDialogOffline.findViewById(R.id.rcv_dialog_available_devices);
            Button btnCancelDialogAvailableDevices = addNewLockDialogOffline.findViewById(R.id.btn_cancel_dialog_available_devices);
            Button btnScanDialogAvailableDevices = addNewLockDialogOffline.findViewById(R.id.btn_scan_dialog_available_devices);

            rcvDialogAvailableDevices.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
            rcvDialogAvailableDevices.setItemAnimator(new DefaultItemAnimator());
            rcvDialogAvailableDevices.setAdapter(mBleDeviceAdapter);

            btnCancelDialogAvailableDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBleDeviceAdapter.setBleDevices(Collections.singletonList(new ScannedDeviceModel(FINDING_BLE_DEVICES_SCAN_MODE)));
                    addNewLockDialogOffline.dismiss();
                    addNewLockDialogOffline = null;
                }
            });

            btnScanDialogAvailableDevices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBleDeviceAdapter.setBleDevices(Collections.singletonList(new ScannedDeviceModel(FINDING_BLE_DEVICES_SCAN_MODE)));
                    BleHelper.findDevices(fragment);
                }
            });

            BleHelper.findDevices(fragment);
        } else
            mBleDeviceAdapter.setBleDevices(devices);

        if (!addNewLockDialogOffline.isShowing())
            addNewLockDialogOffline.show();

        addNewLockDialogOffline.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(addNewLockDialogOffline));

        return addNewLockDialogOffline;
    }

    public static Dialog handleAddLockOfflineDialog(Fragment fragment) {
        saveLockAfterPaired = false;

        Dialog dialog = new Dialog(Objects.requireNonNull(fragment.getContext()));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_new_lock);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!saveLockAfterPaired)
                    mBluetoothLEHelper.unPairDevice(((AddLockFragment) fragment).mDevice.getDevice().getAddress());
            }
        });

        Button btnCancelDialogAddNewLock = dialog.findViewById(R.id.btn_cancel_dialog_add_new_lock);
        Button btnAddDialogAddNewLock = dialog.findViewById(R.id.btn_add_dialog_add_new_lock);

        TextInputEditText tietLockNameDialogAddNewLock = dialog.findViewById(R.id.tiet_lock_name_dialog_add_new_lock);
        TextInputEditText tietLockSerialNumberDialogAddNewLock = dialog.findViewById(R.id.tiet_lock_serial_number_dialog_add_new_lock);

        CheckBox chbLockFavoriteStatusDialogAddNewLock = dialog.findViewById(R.id.chb_lock_favorite_status_dialog_add_new_lock);

        btnCancelDialogAddNewLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAddDialogAddNewLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.handleProgressDialog(fragment.getContext(), null, "Saving ...", true);

                ((AddLockFragment) fragment).mDevice.setName(Objects.requireNonNull(tietLockNameDialogAddNewLock.getText()).toString());
                ((AddLockFragment) fragment).mDevice.setSerialNumber(Objects.requireNonNull(tietLockSerialNumberDialogAddNewLock.getText()).toString());

                ((AddLockFragment) fragment).mDeviceViewModel.getAllLocalDevices().observe(fragment, new Observer<List<Device>>() {
                    @Override
                    public void onChanged(@Nullable final List<Device> devices) {
                        ((LockActivity) fragment.getActivity()).setViewPagerAdapter(
                                new CustomDeviceAdapter(fragment.getActivity().getSupportFragmentManager(), devices));
                    }
                });
                ((AddLockFragment) fragment).mDeviceViewModel.insertLocalDevice(new Device(((AddLockFragment) fragment).mDevice));

                saveLockAfterPaired = true;

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(dialog));

        return dialog;
    }

    public static void handleAddLockOnlineDialog(Fragment fragment) {
        Dialog dialog = new Dialog(fragment.getContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_new_lock);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        Button btnCancelDialogAddNewLock = dialog.findViewById(R.id.btn_cancel_dialog_add_new_lock);
        Button btnAddDialogAddNewLock = dialog.findViewById(R.id.btn_add_dialog_add_new_lock);

        TextInputEditText tietLockNameDialogAddNewLock = dialog.findViewById(R.id.tiet_lock_name_dialog_add_new_lock);
        TextInputEditText tietLockSerialNumberDialogAddNewLock = dialog.findViewById(R.id.tiet_lock_serial_number_dialog_add_new_lock);

        CheckBox chbLockFavoriteStatusDialogAddNewLock = dialog.findViewById(R.id.chb_lock_favorite_status_dialog_add_new_lock);

        btnCancelDialogAddNewLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAddDialogAddNewLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.handleProgressDialog(fragment.getContext(), null, "Evaluating ...", true);



                ((AddLockFragment) fragment).mDevice.setName(Objects.requireNonNull(tietLockNameDialogAddNewLock.getText()).toString());
                ((AddLockFragment) fragment).mDevice.setSerialNumber(Objects.requireNonNull(tietLockSerialNumberDialogAddNewLock.getText()).toString());

//                ((AddLockFragment) fragment).mDeviceViewModel.getAllLocalDevices().observe(fragment, new Observer<List<Device>>() {
//                    @Override
//                    public void onChanged(@Nullable final List<Device> devices) {
//                        ((LockActivity) fragment.getActivity()).setViewPagerAdapter(
//                                new CustomDeviceAdapter(fragment.getActivity().getSupportFragmentManager(), devices));
//                    }
//                });
//                ((AddLockFragment) fragment).mDeviceViewModel.insertLocalDevice(new Device(((AddLockFragment) fragment).mDevice));

                saveLockAfterPaired = true;

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(dialog));
    }

    public static void handleProgressDialog(Context context, String title, String message, boolean show) {
        Handler mHandler = new Handler();
        if (show)
            ProgressDialogHelper.show(context, title, message);

        mHandler.postDelayed(() -> {
            DialogHelper.handleProgressDialog(false);
        }, 10000);

    }

    public static void handleProgressDialog(boolean show) {
        if (!show)
            ProgressDialogHelper.hide();
    }
    //endregion Declare Methods

    //region Declare Classes
    private static class ProgressDialogHelper {
        private static void show(Context context, String title, String message) {
            if (mProgressDialog != null) {
                if (!mProgressDialog.isShowing())
                    mProgressDialog.show();
            } else {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setTitle(title);
                mProgressDialog.setMessage(message);
                mProgressDialog.show();
            }
        }

        private static void hide() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }
    //endregion Declare Classes

    //region NotUsed
    public static void handleEnableLocationDialog(Activity activity) {
        Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enable_location);

        Button btnNoDialogEnableLocation = dialog.findViewById(R.id.btn_no_dialog_enable_location);
        Button btnYesDialogEnableLocation = dialog.findViewById(R.id.btn_yes_dialog_enable_location);

        btnNoDialogEnableLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYesDialogEnableLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BleHelper.enableLocation(activity);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(dialog));
    }

    public static Dialog handlePairWithBleDeviceDialog(Fragment fragment, ScannedDeviceModel mScannedDeviceModel) {
        Dialog dialog = new Dialog(Objects.requireNonNull(fragment.getContext()));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pair_with_ble_device);

        Button btnCancelDialogPairWithBleDevice = dialog.findViewById(R.id.btn_cancel_dialog_pair_with_ble_device);
        Button btnPairDialogPairWithBleDevice = dialog.findViewById(R.id.btn_pair_dialog_pair_with_ble_device);

        TextInputEditText txieSecurityCodeDialogPairWithBleDevice = dialog.findViewById(R.id.tiet_security_code_dialog_pair_with_ble_device);

        btnCancelDialogPairWithBleDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnPairDialogPairWithBleDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.handleProgressDialog(fragment.getContext(), null, "Pairing ...", true);
                mScannedDeviceModel.getDevice().setPin(Objects.requireNonNull(txieSecurityCodeDialogPairWithBleDevice.getText()).toString().getBytes());
                mScannedDeviceModel.getDevice().createBond();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(dialog));

        return dialog;
    }
    //endregion NotUsed
}
