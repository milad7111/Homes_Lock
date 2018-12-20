package com.projects.company.homes_lock.ui.device.fragment.setting;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.base.BaseApplication;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.utils.helper.DataHelper;
import com.projects.company.homes_lock.utils.helper.DialogHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

import java.util.Objects;

import static com.projects.company.homes_lock.utils.helper.BleHelper.DOOR_INSTALLATION_SETTING_LEFT_HANDED;
import static com.projects.company.homes_lock.utils.helper.BleHelper.DOOR_INSTALLATION_SETTING_RIGHT_HANDED;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_STAGES_NINETY_DEGREES;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_STAGES_ONE_STAGE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_STAGES_THREE_STAGE;
import static com.projects.company.homes_lock.utils.helper.BleHelper.LOCK_STAGES_TWO_STAGE;
import static com.projects.company.homes_lock.utils.helper.DataHelper.CHANGE_ONLINE_PASSWORD;
import static com.projects.company.homes_lock.utils.helper.DataHelper.CHANGE_PAIRING_PASSWORD;
import static com.projects.company.homes_lock.utils.helper.DataHelper.convertJsonToObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment
        implements
        ISettingFragment,
        View.OnClickListener {

    //region Declare Constants
    private static final String ARG_PARAM = "param";
    //endregion Declare Constants

    //region Declare Views
    private TextView txvDeviceSettingSettingFragment;
    private TextView txvDeviceSettingDescriptionSettingFragment;
    private TextView txvProServicesSettingFragment;
    private TextView txvProServicesDescriptionSettingFragment;
    private TextView txvChangePasswordOnlineSettingFragment;
    private TextView txvChangePasswordOnlineDescriptionSettingFragment;
    private TextView txvChangePairingPasswordSettingFragment;
    private TextView txvChangePairingPasswordDescriptionSettingFragment;
    private TextView txvRemoveLockSettingFragment;
    private TextView txvRemoveLockDescriptionSettingFragment;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private Fragment mFragment;
    private DeviceViewModel mDeviceViewModel;
    private Device mDevice;
    private Dialog deviceSettingDialog;
    private Dialog changeOnlinePasswordDialog;
    private Dialog changePairingPasswordDialog;
    private Dialog removeLockDialog;
    //endregion Declare Objects

    //region Constructor
    public SettingFragment() {
    }

    public static SettingFragment newInstance(Device device) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM, new Gson().toJson(device));
        fragment.setArguments(args);

        return fragment;
    }
    //endregion Constructor

    //region Main Callbacks
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region Initialize Variables
        //endregion Initialize Variables

        //region Initialize Objects
        mFragment = this;

        this.mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);

        mDevice = getArguments() != null ?
                (Device) convertJsonToObject(getArguments().getString(ARG_PARAM), Device.class.getName())
                : null;
        //endregion Initialize Objects
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Initialize Views
        txvDeviceSettingSettingFragment = view.findViewById(R.id.txv_device_setting_setting_fragment);
        txvDeviceSettingDescriptionSettingFragment = view.findViewById(R.id.txv_device_setting_description_setting_fragment);
        txvProServicesSettingFragment = view.findViewById(R.id.txv_pro_services_setting_fragment);
        txvProServicesDescriptionSettingFragment = view.findViewById(R.id.txv_pro_services_description_setting_fragment);
        txvChangePasswordOnlineSettingFragment = view.findViewById(R.id.txv_change_password_online_setting_fragment);
        txvChangePasswordOnlineDescriptionSettingFragment = view.findViewById(R.id.txv_change_password_online_description_setting_fragment);
        txvChangePairingPasswordSettingFragment = view.findViewById(R.id.txv_change_pairing_password_setting_fragment);
        txvChangePairingPasswordDescriptionSettingFragment = view.findViewById(R.id.txv_change_pairing_password_description_setting_fragment);
        txvRemoveLockSettingFragment = view.findViewById(R.id.txv_remove_lock_setting_fragment);
        txvRemoveLockDescriptionSettingFragment = view.findViewById(R.id.txv_remove_lock_description_setting_fragment);
        //endregion Initialize Views

        //region Setup Views
        txvDeviceSettingSettingFragment.setOnClickListener(this);
        txvDeviceSettingDescriptionSettingFragment.setOnClickListener(this);
        txvProServicesSettingFragment.setOnClickListener(this);
        txvProServicesDescriptionSettingFragment.setOnClickListener(this);
        txvChangePasswordOnlineSettingFragment.setOnClickListener(this);
        txvChangePasswordOnlineDescriptionSettingFragment.setOnClickListener(this);
        txvChangePairingPasswordSettingFragment.setOnClickListener(this);
        txvChangePairingPasswordDescriptionSettingFragment.setOnClickListener(this);
        txvRemoveLockSettingFragment.setOnClickListener(this);
        txvRemoveLockDescriptionSettingFragment.setOnClickListener(this);
        //endregion Setup Views

        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txv_device_setting_setting_fragment:
                handleDeviceSetting();
                break;
            case R.id.txv_device_setting_description_setting_fragment:
                handleDeviceSetting();
                break;
            case R.id.txv_pro_services_setting_fragment:
                handleProServices();
                break;
            case R.id.txv_pro_services_description_setting_fragment:
                handleProServices();
                break;
            case R.id.txv_remove_lock_setting_fragment:
                handleRemoveLock();
                break;
            case R.id.txv_remove_lock_description_setting_fragment:
                handleRemoveLock();
                break;
            case R.id.txv_change_password_online_setting_fragment:
                handleChangePassword(CHANGE_ONLINE_PASSWORD);
                break;
            case R.id.txv_change_password_online_description_setting_fragment:
                handleChangePassword(CHANGE_ONLINE_PASSWORD);
                break;
            case R.id.txv_change_pairing_password_setting_fragment:
                handleChangePassword(CHANGE_PAIRING_PASSWORD);
                break;
            case R.id.txv_change_pairing_password_description_setting_fragment:
                handleChangePassword(CHANGE_PAIRING_PASSWORD);
                break;
        }
    }
    //endregion Main Callbacks

    //region ISettingFragment Callbacks
    @Override
    public void onSetDeviceSetting(boolean value) {
        DialogHelper.handleProgressDialog(null, null, null, false);
        if (removeLockDialog != null) {
            removeLockDialog.dismiss();
            removeLockDialog = null;
        }
    }

    @Override
    public void onChangeOnlinePassword(boolean value) {
        DialogHelper.handleProgressDialog(null, null, null, false);
        if (changeOnlinePasswordDialog != null) {
            changeOnlinePasswordDialog.dismiss();
            changeOnlinePasswordDialog = null;
        }
    }

    @Override
    public void onChangePairingPassword(boolean value) {
        DialogHelper.handleProgressDialog(null, null, null, false);
        if (changePairingPasswordDialog != null) {
            changePairingPasswordDialog.dismiss();
            changePairingPasswordDialog = null;
        }
    }

    @Override
    public void onRemoveAllLockMembers(String count) {
        DialogHelper.handleProgressDialog(null, null, null, false);
        if (removeLockDialog != null) {
            removeLockDialog.dismiss();
            removeLockDialog = null;
        }

        logoutLocally();
    }

    private void logoutLocally() {
        Objects.requireNonNull(getActivity()).finish();
    }

    @Override
    public void onRemoveDeviceForOneMember(String count) {
        DialogHelper.handleProgressDialog(null, null, null, false);
        if (removeLockDialog != null) {
            removeLockDialog.dismiss();
            removeLockDialog = null;
        }

        logoutLocally();
    }
    //endregion ISettingFragment Callbacks

    //region Declare Methods
    private void initViews() {
        if (mDevice.getMemberAdminStatus() == DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            txvDeviceSettingSettingFragment.setVisibility(View.GONE);
            txvDeviceSettingDescriptionSettingFragment.setVisibility(View.GONE);
            txvProServicesSettingFragment.setVisibility(View.GONE);
            txvProServicesDescriptionSettingFragment.setVisibility(View.GONE);
            txvChangePasswordOnlineSettingFragment.setVisibility(View.GONE);
            txvChangePasswordOnlineDescriptionSettingFragment.setVisibility(View.GONE);
            txvChangePairingPasswordSettingFragment.setVisibility(View.GONE);
            txvChangePairingPasswordDescriptionSettingFragment.setVisibility(View.GONE);
        }

        if (!BaseApplication.isUserLoggedIn()) {
            txvRemoveLockSettingFragment.setVisibility(View.GONE);
            txvRemoveLockDescriptionSettingFragment.setVisibility(View.GONE);
        }
    }

    private void handleDeviceSetting() {
        if (mDevice.getMemberAdminStatus() != DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            deviceSettingDialog = new Dialog(Objects.requireNonNull(mFragment.getContext()));
            deviceSettingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deviceSettingDialog.setContentView(R.layout.dialog_device_setting);

            RadioGroup rdgDoorInstallationDialogDeviceSetting =
                    deviceSettingDialog.findViewById(R.id.rdg_door_installation_dialog_device_setting);
            RadioGroup rdgLockStagesDialogDeviceSetting =
                    deviceSettingDialog.findViewById(R.id.rdg_lock_stages_dialog_device_setting);

            Button btnCancelDialogDeviceSetting =
                    deviceSettingDialog.findViewById(R.id.btn_cancel_dialog_device_setting);
            Button btnApplyDialogDeviceSetting =
                    deviceSettingDialog.findViewById(R.id.btn_apply_dialog_device_setting);

            btnCancelDialogDeviceSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deviceSettingDialog.dismiss();
                    deviceSettingDialog = null;
                }
            });

            btnApplyDialogDeviceSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.handleProgressDialog(mFragment.getContext(), null, "Device setting setup ...", true);
                    mDeviceViewModel.setDeviceSetting(
                            mFragment,
                            findSelectedDoorInstallationOption(rdgDoorInstallationDialogDeviceSetting),
                            findSelectedLockStagesOption(rdgLockStagesDialogDeviceSetting));
                }
            });
        }

        deviceSettingDialog.show();
        deviceSettingDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(deviceSettingDialog));
    }

    private void handleProServices() {
    }

    private void handleRemoveLock() {
        if (mDevice.getMemberAdminStatus() != DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            removeLockDialog = new Dialog(Objects.requireNonNull(mFragment.getContext()));
            removeLockDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            removeLockDialog.setContentView(R.layout.dialog_remove_lock);

            CheckBox chbRemoveAllMembersDialogRemoveLock =
                    removeLockDialog.findViewById(R.id.chb_remove_all_members_dialog_remove_lock);

            if (mDevice.getAdminMembersCount() == 1) {//TODO I must get this from server
                chbRemoveAllMembersDialogRemoveLock.setChecked(true);
                chbRemoveAllMembersDialogRemoveLock.setEnabled(false);
            }

            if (mDevice.getMemberAdminStatus() == DataHelper.MEMBER_STATUS_NOT_ADMIN) {
                chbRemoveAllMembersDialogRemoveLock.setChecked(false);
                chbRemoveAllMembersDialogRemoveLock.setVisibility(View.GONE);
            }

            Button btnCancelDialogRemoveLock =
                    removeLockDialog.findViewById(R.id.btn_cancel_dialog_remove_lock);
            Button btnRemoveDialogRemoveLock =
                    removeLockDialog.findViewById(R.id.btn_remove_dialog_remove_lock);

            btnCancelDialogRemoveLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeLockDialog.dismiss();
                    removeLockDialog = null;
                }
            });

            btnRemoveDialogRemoveLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.handleProgressDialog(mFragment.getContext(), null, "Remove lock ...", true);
                    mDeviceViewModel.removeDevice(
                            mFragment,
                            chbRemoveAllMembersDialogRemoveLock.isChecked(),
                            mDevice);
                }
            });
        }

        removeLockDialog.show();
        removeLockDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(removeLockDialog));
    }

    private void handleChangePassword(int changeStatus) {
        switch (changeStatus) {
            case CHANGE_ONLINE_PASSWORD:
                handleDialogChangeOnlinePassword();
                break;
            case CHANGE_PAIRING_PASSWORD:
                handleDialogChangePairingPassword();
                break;
        }
    }

    private void handleDialogChangeOnlinePassword() {
        if (mDevice.getMemberAdminStatus() != DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            changeOnlinePasswordDialog = new Dialog(Objects.requireNonNull(mFragment.getContext()));
            changeOnlinePasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            changeOnlinePasswordDialog.setContentView(R.layout.dialog_change_online_password);

            TextInputEditText tietOldPasswordDialogChangeOnlinePassword =
                    changeOnlinePasswordDialog.findViewById(R.id.tiet_old_password_dialog_change_online_password);
            TextInputEditText tietNewPasswordDialogChangeOnlinePassword =
                    changeOnlinePasswordDialog.findViewById(R.id.tiet_new_password_dialog_change_online_password);

            Button btnCancelDialogChangeOnlinePassword =
                    changeOnlinePasswordDialog.findViewById(R.id.btn_cancel_dialog_change_online_password);
            Button btnApplyDialogChangeOnlinePassword =
                    changeOnlinePasswordDialog.findViewById(R.id.btn_apply_dialog_change_online_password);

            btnCancelDialogChangeOnlinePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeOnlinePasswordDialog.dismiss();
                    changeOnlinePasswordDialog = null;
                }
            });

            btnApplyDialogChangeOnlinePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.handleProgressDialog(mFragment.getContext(), null, "Change online password ...", true);
                    mDeviceViewModel.changeOnlinePasswordViaBle(mFragment,
                            Objects.requireNonNull(tietOldPasswordDialogChangeOnlinePassword.getText()).toString(),
                            Objects.requireNonNull(tietNewPasswordDialogChangeOnlinePassword.getText()).toString());
                }
            });
        }

        changeOnlinePasswordDialog.show();
        changeOnlinePasswordDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(changeOnlinePasswordDialog));
    }

    private void handleDialogChangePairingPassword() {
        if (mDevice.getMemberAdminStatus() != DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            changePairingPasswordDialog = new Dialog(Objects.requireNonNull(mFragment.getContext()));
            changePairingPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            changePairingPasswordDialog.setContentView(R.layout.dialog_change_pairing_password);

            TextInputEditText tietOldPasswordDialogChangePairingPassword =
                    changePairingPasswordDialog.findViewById(R.id.tiet_old_password_dialog_change_pairing_password);
            TextInputEditText tietNewPasswordDialogChangePairingPassword =
                    changePairingPasswordDialog.findViewById(R.id.tiet_new_password_dialog_change_pairing_password);

            Button btnCancelDialogChangePairingPassword =
                    changePairingPasswordDialog.findViewById(R.id.btn_cancel_dialog_change_pairing_password);
            Button btnApplyDialogChangePairingPassword =
                    changePairingPasswordDialog.findViewById(R.id.btn_apply_dialog_change_pairing_password);

            btnCancelDialogChangePairingPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePairingPasswordDialog.dismiss();
                    changePairingPasswordDialog = null;
                }
            });

            btnApplyDialogChangePairingPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.handleProgressDialog(mFragment.getContext(), null, "Change pairing password ...", true);
                    mDeviceViewModel.changePairingPasswordViaBle(mFragment,
                            Objects.requireNonNull(tietOldPasswordDialogChangePairingPassword.getText()).toString(),
                            Objects.requireNonNull(tietNewPasswordDialogChangePairingPassword.getText()).toString());
                }
            });
        }

        changePairingPasswordDialog.show();
        changePairingPasswordDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(changePairingPasswordDialog));
    }

    private int findSelectedDoorInstallationOption(RadioGroup radioGroup) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rdb_right_handed_dialog_device_setting:
                return DOOR_INSTALLATION_SETTING_LEFT_HANDED;
            case R.id.rdb_left_handed_dialog_device_setting:
                return DOOR_INSTALLATION_SETTING_RIGHT_HANDED;
            default:
                return -1;
        }
    }

    private int findSelectedLockStagesOption(RadioGroup radioGroup) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rdb_ninety_degrees_dialog_device_setting:
                return LOCK_STAGES_NINETY_DEGREES;
            case R.id.rdb_one_stage_dialog_device_setting:
                return LOCK_STAGES_ONE_STAGE;
            case R.id.rdb_two_stages_dialog_device_setting:
                return LOCK_STAGES_TWO_STAGE;
            case R.id.rdb_three_stages_dialog_device_setting:
                return LOCK_STAGES_THREE_STAGE;
            default:
                return -1;
        }
    }
    //endregion Declare Methods
}
