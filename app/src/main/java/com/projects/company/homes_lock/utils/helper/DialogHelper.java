package com.projects.company.homes_lock.utils.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.projects.company.homes_lock.R;

public class DialogHelper {

    //region Declare Objects
    private static ProgressDialog mProgressDialog;
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

        dialog.show();
    }

    public static void handleAddNewLockDialog(Activity activity) {
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

        dialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    public static void handlePairWithBleDeviceDialog(Activity activity, BluetoothDevice device) {
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
                DialogHelper.handleProgressDialog(activity, null, null, true);
                device.setPin(txieSecurityCodeDialogPairWithBleDevice.getText().toString().getBytes());
                device.createBond();
                dialog.dismiss();
            }
        });

        dialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    public static void handleProgressDialog(Context context, String title, String message, boolean show) {
        if (show)
            ProgressDialogHelper.show(context, title, message);
    }

    public static void handleProgressDialog(boolean show) {
        if (!show)
            ProgressDialogHelper.hide();
    }
    //endregion Declare Methods

    //region Declare Classes
    private static class ProgressDialogHelper {
        private ProgressDialogHelper() {
        }

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
}
