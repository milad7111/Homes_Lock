package com.projects.company.homes_lock.utils.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;

import com.projects.company.homes_lock.R;

import java.util.Objects;

public class ProgressDialogHelper {

    //region Declare Objects
    private static ProgressDialog mProgressDialog;
    //endregion Declare Objects

    //region Declare Methods
    public static void openProgressDialog(Context context, String title, String message) {
        Handler mHandler = new Handler();
        show(context, title, message);
        mHandler.postDelayed(ProgressDialogHelper::closeProgressDialog, 20000);
    }

    public static void closeProgressDialog() {
        hide();
    }

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
    //endregion Declare Methods

    //region NotUsed
    public static void handleEnableLocationDialog(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enable_location);
        dialog.setCancelable(false);

        Button btnNoDialogEnableLocation = dialog.findViewById(R.id.btn_no_dialog_enable_location);
        Button btnYesDialogEnableLocation = dialog.findViewById(R.id.btn_yes_dialog_enable_location);

        btnNoDialogEnableLocation.setOnClickListener(v -> dialog.dismiss());

        btnYesDialogEnableLocation.setOnClickListener(v -> {
            BleHelper.enableLocation(activity);
            dialog.dismiss();
        });

        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setAttributes(ViewHelper.getDialogLayoutParams(dialog));
    }
    //endregion NotUsed
}
