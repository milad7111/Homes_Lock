package com.projects.company.homes_lock.utils.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.ScannedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.request.UserLockModel;
import com.projects.company.homes_lock.ui.device.activity.LockActivity;
import com.projects.company.homes_lock.ui.device.fragment.adddevice.AddDeviceFragment;

import static com.projects.company.homes_lock.ui.device.activity.LockActivity.mBluetoothLEHelper;
import static com.projects.company.homes_lock.utils.helper.DataHelper.getRandomPercentNumber;

public class DialogHelper {

    //region Declare Objects
    private static ProgressDialog mProgressDialog;
    //endregion Declare Objects

    //region Declare Methods
    public static void handleProgressDialog(Context context, String title, String message, boolean show) {
        if (show) {
            Handler mHandler = new Handler();

            ProgressDialogHelper.show(context, title, message);

            mHandler.postDelayed(() -> {
                handleProgressDialog(null, null, null, false);
            }, 20000);
        } else
            ProgressDialogHelper.hide();
    }
    //endregion Declare Methods

    //region Declare Classes & Interfaces
    private static class ProgressDialogHelper {
        private static void show(Context context, String title, String message) {
            hide();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }

        private static void hide() {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }
    //endregion Declare Classes & Interfaces

    //region NotUsed
    public static void handleEnableLocationDialog(Activity activity) {
        Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enable_location);

        Button btnNoDialogEnableLocation = dialog.findViewById(R.id.btn_no_dialog_enable_location);
        Button btnYesDialogEnableLocation = dialog.findViewById(R.id.btn_yes_dialog_enable_location);

        btnNoDialogEnableLocation.setOnClickListener(v -> dialog.dismiss());

        btnYesDialogEnableLocation.setOnClickListener(v -> {
            BleHelper.enableLocation(activity);
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(dialog));
    }

    public static Dialog handlePairWithBleDeviceDialog(Fragment fragment, ScannedDeviceModel mScannedDeviceModel) {
        Dialog dialog = new Dialog(fragment.getContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pair_with_ble_device);

        Button btnCancelDialogPairWithBleDevice = dialog.findViewById(R.id.btn_cancel_dialog_pair_with_ble_device);
        Button btnPairDialogPairWithBleDevice = dialog.findViewById(R.id.btn_pair_dialog_pair_with_ble_device);

        TextInputEditText txieSecurityCodeDialogPairWithBleDevice = dialog.findViewById(R.id.tiet_security_code_dialog_pair_with_ble_device);

        btnCancelDialogPairWithBleDevice.setOnClickListener(v -> dialog.dismiss());

        btnPairDialogPairWithBleDevice.setOnClickListener(v -> {
            DialogHelper.handleProgressDialog(fragment.getContext(), null, "Pairing ...", true);
            mScannedDeviceModel.getDevice().setPin(txieSecurityCodeDialogPairWithBleDevice.getText().toString().getBytes());
            mScannedDeviceModel.getDevice().createBond();
        });

        dialog.show();
        dialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(dialog));

        return dialog;
    }
    //endregion NotUsed
}
