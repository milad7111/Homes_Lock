package com.projects.company.homes_lock.utils.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.utils.ble.BleDeviceAdapter;
import com.projects.company.homes_lock.utils.ble.IBleScanListener;

import java.util.Collections;
import java.util.List;

public class DialogHelper implements IBleScanListener {

    //region Declare Objects
    private static ProgressDialog mProgressDialog;
    private BleDeviceAdapter mBleDeviceAdapter;
    private DialogHelper selfObject;
    //endregion Declare Objects

    //region Declare Methods
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

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    public void handleAddNewLockDialogOffline(Fragment fragment) {
        Dialog dialog = new Dialog(fragment.getContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_available_devices);

        mBleDeviceAdapter =
                new BleDeviceAdapter((AppCompatActivity) fragment.getActivity(), Collections.singletonList(new ScannedDeviceModel()));

        selfObject = this;
        BleHelper.findDevices(this, fragment);

        RecyclerView rcvDialogAvailableDevices = dialog.findViewById(R.id.rcv_dialog_available_devices);
        Button btnCancelDialogAvailableDevices = dialog.findViewById(R.id.btn_cancel_dialog_available_devices);
        Button btnScanDialogAvailableDevices = dialog.findViewById(R.id.btn_scan_dialog_available_devices);

        rcvDialogAvailableDevices.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        rcvDialogAvailableDevices.setItemAnimator(new DefaultItemAnimator());
        rcvDialogAvailableDevices.setAdapter(mBleDeviceAdapter);

        btnCancelDialogAvailableDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnScanDialogAvailableDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selfObject != null) {
                    mBleDeviceAdapter.setBleDevices(Collections.singletonList(new ScannedDeviceModel()));
                    BleHelper.findDevices(selfObject, fragment);
                } else
                    dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    public static void handleAddNewLockDialogOnline(Activity activity) {
        Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_new_lock);

        Button btnCancelDialogEnableLocation = dialog.findViewById(R.id.btn_cancel_dialog_add_new_lock);
        Button btnAddDialogEnableLocation = dialog.findViewById(R.id.btn_add_new_lock_dialog_add_new_lock);

        btnCancelDialogEnableLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAddDialogEnableLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO add online lock
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    public static void handleAddNewLockDialog(Activity activity, BluetoothDevice device) {
        Dialog dialog = new Dialog(activity);

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
                DialogHelper.handleProgressDialog(activity, null, "Pairing ...", true);
                device.setPin(txieSecurityCodeDialogPairWithBleDevice.getText().toString().getBytes());
                device.createBond();
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    public static Dialog handlePairWithBleDeviceDialog(Activity activity, BluetoothDevice device) {
        Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pair_with_ble_device);

        Button btnCancelDialogPairWithBleDevice = dialog.findViewById(R.id.btn_cancel_dialog_pair_with_ble_device);
        Button btnPairDialogPairWithBleDevice = dialog.findViewById(R.id.btn_pair_dialog_pair_with_ble_device);

        TextInputEditText txieLockNameDialogPairWithBleDevice = dialog.findViewById(R.id.tiet_lock_name_dialog_pair_with_ble_device);
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
                DialogHelper.handleProgressDialog(activity, null, "Pairing ...", true);
                device.setPin(txieSecurityCodeDialogPairWithBleDevice.getText().toString().getBytes());
                device.createBond();
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

        return dialog;
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

    //region Ble Scan Callbacks
    @Override
    public void onFindBleSuccess(List devices) {
        if (mBleDeviceAdapter != null)
            mBleDeviceAdapter.setBleDevices(
                    devices.size() == 0 ?
                            Collections.singletonList(new ScannedDeviceModel()) :
                            (List<ScannedDeviceModel>) devices);
    }

    @Override
    public void onFindBleFault() {
        mBleDeviceAdapter.setBleDevices(Collections.singletonList(new ScannedDeviceModel()));
    }
    //endregion Ble Scan Callbacks
}
