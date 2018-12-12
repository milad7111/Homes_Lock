package com.projects.company.homes_lock.ui.device.fragment.setting;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.viewmodels.DeviceViewModel;
import com.projects.company.homes_lock.utils.helper.DataHelper;
import com.projects.company.homes_lock.utils.helper.DialogHelper;
import com.projects.company.homes_lock.utils.helper.ViewHelper;

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
        implements ISettingFragment, View.OnClickListener {

    //region Declare Constants
    private static final String ARG_PARAM = "param";
    //endregion Declare Constants

    //region Declare Views
    private TextView txvDoorInstallationSettingFragment;
    private TextView txvDoorInstallationDescriptionSettingFragment;
    private TextView txvLockStagesSettingFragment;
    private TextView txvLockStagesDescriptionSettingFragment;
    private TextView txvProServicesSettingFragment;
    private TextView txvProServicesDescriptionSettingFragment;
    private TextView txvRemoveLockSettingFragment;
    private TextView txvRemoveLockDescriptionSettingFragment;
    private TextView txvChangePasswordOnlineSettingFragment;
    private TextView txvChangePasswordOnlineDescriptionSettingFragment;
    private TextView txvChangePairingPasswordSettingFragment;
    private TextView txvChangePairingPasswordDescriptionSettingFragment;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private Fragment mFragment;
    public static DeviceViewModel mDeviceViewModel;
    private static Device mDevice;
    private Dialog doorInstallationDialog;
    private Dialog lockStagesDialog;
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
        txvDoorInstallationSettingFragment = view.findViewById(R.id.txv_door_installation_setting_fragment);
        txvDoorInstallationDescriptionSettingFragment = view.findViewById(R.id.txv_door_installation_description_setting_fragment);
        txvLockStagesSettingFragment = view.findViewById(R.id.txv_lock_stages_setting_fragment);
        txvLockStagesDescriptionSettingFragment = view.findViewById(R.id.txv_lock_stages_description_setting_fragment);
        txvProServicesSettingFragment = view.findViewById(R.id.txv_pro_services_setting_fragment);
        txvProServicesDescriptionSettingFragment = view.findViewById(R.id.txv_pro_services_description_setting_fragment);
        txvRemoveLockSettingFragment = view.findViewById(R.id.txv_remove_lock_setting_fragment);
        txvRemoveLockDescriptionSettingFragment = view.findViewById(R.id.txv_remove_lock_description_setting_fragment);
        txvChangePasswordOnlineSettingFragment = view.findViewById(R.id.txv_change_password_online_setting_fragment);
        txvChangePasswordOnlineDescriptionSettingFragment = view.findViewById(R.id.txv_change_password_online_description_setting_fragment);
        txvChangePairingPasswordSettingFragment = view.findViewById(R.id.txv_change_pairing_password_setting_fragment);
        txvChangePairingPasswordDescriptionSettingFragment = view.findViewById(R.id.txv_change_pairing_password_description_setting_fragment);
        //endregion Initialize Views

        //region Setup Views
        txvDoorInstallationSettingFragment.setOnClickListener(this);
        txvDoorInstallationDescriptionSettingFragment.setOnClickListener(this);
        txvLockStagesSettingFragment.setOnClickListener(this);
        txvLockStagesDescriptionSettingFragment.setOnClickListener(this);
        txvProServicesSettingFragment.setOnClickListener(this);
        txvProServicesDescriptionSettingFragment.setOnClickListener(this);
        txvRemoveLockSettingFragment.setOnClickListener(this);
        txvRemoveLockDescriptionSettingFragment.setOnClickListener(this);
        txvChangePasswordOnlineSettingFragment.setOnClickListener(this);
        txvChangePasswordOnlineDescriptionSettingFragment.setOnClickListener(this);
        txvChangePairingPasswordSettingFragment.setOnClickListener(this);
        txvChangePairingPasswordDescriptionSettingFragment.setOnClickListener(this);
        //endregion Setup Views

        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txv_door_installation_setting_fragment:
                handleDoorInstallation();
                break;
            case R.id.txv_door_installation_description_setting_fragment:
                handleDoorInstallation();
                break;
            case R.id.txv_lock_stages_setting_fragment:
                handleLockStages();
                break;
            case R.id.txv_lock_stages_description_setting_fragment:
                handleLockStages();
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
    public void onSetDoorInstallationSetting(boolean value) {
        DialogHelper.handleProgressDialog(null, null, null, false);
    }

    @Override
    public void onSetLockStagesSetting(boolean value) {
    }
    //endregion ISettingFragment Callbacks

    //region Declare Methods
    private void initViews() {
        if (mDevice.getMemberAdminStatus() == DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            txvDoorInstallationSettingFragment.setVisibility(View.GONE);
            txvDoorInstallationDescriptionSettingFragment.setVisibility(View.GONE);
            txvLockStagesSettingFragment.setVisibility(View.GONE);
            txvLockStagesDescriptionSettingFragment.setVisibility(View.GONE);
            txvProServicesSettingFragment.setVisibility(View.GONE);
            txvProServicesDescriptionSettingFragment.setVisibility(View.GONE);
            txvChangePasswordOnlineSettingFragment.setVisibility(View.GONE);
            txvChangePasswordOnlineDescriptionSettingFragment.setVisibility(View.GONE);
            txvChangePairingPasswordSettingFragment.setVisibility(View.GONE);
            txvChangePairingPasswordDescriptionSettingFragment.setVisibility(View.GONE);
        }
    }

    private void handleDoorInstallation() {
        if (mDevice.getMemberAdminStatus() != DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            doorInstallationDialog = new Dialog(getActivity());
            doorInstallationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            doorInstallationDialog.setContentView(R.layout.dialog_door_installation);

            RadioGroup rdgMainDialogDoorInstallation =
                    doorInstallationDialog.findViewById(R.id.rdg_main_dialog_door_installation);

            Button btnCancelDialogDoorInstallation =
                    doorInstallationDialog.findViewById(R.id.btn_cancel_dialog_door_installation);
            Button btnApplyDialogDoorInstallation =
                    doorInstallationDialog.findViewById(R.id.btn_apply_dialog_door_installation);

            btnCancelDialogDoorInstallation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doorInstallationDialog.dismiss();
                    doorInstallationDialog = null;
                }
            });

            btnApplyDialogDoorInstallation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.handleProgressDialog(mFragment.getContext(), null, "Door installation setup ...", true);
                    mDeviceViewModel.setDeviceDoorInstallationSetting(mFragment, findSelectedDoorInstallationOption(rdgMainDialogDoorInstallation));
                }
            });
        }

        doorInstallationDialog.show();
        doorInstallationDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(doorInstallationDialog));
    }

    private void handleLockStages() {
        if (mDevice.getMemberAdminStatus() != DataHelper.MEMBER_STATUS_NOT_ADMIN) {
            lockStagesDialog = new Dialog(getActivity());
            lockStagesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            lockStagesDialog.setContentView(R.layout.dialog_lock_stages);

            RadioGroup rdgMainDialogLockStages =
                    lockStagesDialog.findViewById(R.id.rdg_main_dialog_lock_stages);

            Button btnCancelDialogLockStages =
                    lockStagesDialog.findViewById(R.id.btn_cancel_dialog_lock_stages);
            Button btnApplyDialogLockStages =
                    lockStagesDialog.findViewById(R.id.btn_apply_dialog_lock_stages);

            btnCancelDialogLockStages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lockStagesDialog.dismiss();
                    lockStagesDialog = null;
                }
            });

            btnApplyDialogLockStages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHelper.handleProgressDialog(mFragment.getContext(), null, "Lock stages setup ...", true);
                    mDeviceViewModel.setDeviceLockStagesSetting(mFragment, findSelectedLockStagesOption(rdgMainDialogLockStages));
                }
            });
        }

        lockStagesDialog.show();
        lockStagesDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(lockStagesDialog));
    }

    private void handleProServices() {
    }

    private void handleRemoveLock() {
    }

    private void handleChangePassword(int changeStatus) {
        switch (changeStatus) {
            case CHANGE_ONLINE_PASSWORD:
                handleDialogChangePasswordOnline();
                break;
            case CHANGE_PAIRING_PASSWORD:
                handleDialogChangePairingPassword();
                break;
        }
    }

    private void handleDialogChangePasswordOnline() {
//        if (deviceWifiNetworkDialog == null) {
//            deviceWifiNetworkDialog = new Dialog(getActivity());
//            deviceWifiNetworkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            deviceWifiNetworkDialog.setContentView(R.layout.dialog_device_wifi_network_connect);
//
//            if (mWifiNetworksAdapter == null)
//                mWifiNetworksAdapter = new WifiNetworksAdapter(this, Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
//
//            Spinner spnWifiTypeDialogDeviceWifiNetworkConnect =
//                    deviceWifiNetworkDialog.findViewById(R.id.spn_wifi_type_dialog_device_wifi_network_connect);
//            TextInputEditText tietWifiPasswordDialogDeviceWifiNetworkConnect =
//                    deviceWifiNetworkDialog.findViewById(R.id.tiet_wifi_password_dialog_device_wifi_network_connect);
//
//            Button btnCancelDialogDeviceWifiNetworkConnect =
//                    deviceWifiNetworkDialog.findViewById(R.id.btn_cancel_dialog_device_wifi_network_connect);
//            Button btnConnectDialogDeviceWifiNetworkConnect =
//                    deviceWifiNetworkDialog.findViewById(R.id.btn_connect_dialog_device_wifi_network_connect);
//
//            btnCancelDialogDeviceWifiNetworkConnect.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    deviceWifiNetworkDialog.dismiss();
//                    deviceWifiNetworkDialog = null;
//                }
//            });
//
//            btnConnectDialogDeviceWifiNetworkConnect.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    wifiNetwork.setPassword(tietWifiPasswordDialogDeviceWifiNetworkConnect.getText().toString());
//                    wifiNetwork.setAuthenticateType(spnWifiTypeDialogDeviceWifiNetworkConnect.getSelectedItemPosition());
//                    mDeviceViewModel.setDeviceWifiNetwork(getParentFragment(), wifiNetwork);
//                }
//            });
//
//            mDeviceViewModel.getAvailableWifiNetworksCountAroundDevice(this);
//        } else {
//            mWifiNetworksAdapter.setAvailableNetworks(Collections.singletonList(new WifiNetworksModel(SEARCHING_SCAN_MODE)));
//            mWifiNetworksAdapter.setAvailableNetworks(mWifiNetworkList);
//        }
//
//        if (!deviceWifiNetworkDialog.isShowing())
//            deviceWifiNetworkDialog.show();
//
//        deviceWifiNetworkDialog.getWindow().setAttributes(ViewHelper.getDialogLayoutParams(deviceWifiNetworkDialog));
    }

    private void handleDialogChangePairingPassword() {
    }

    private int findSelectedDoorInstallationOption(RadioGroup radioGroup) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rdb_left_handed_dialog_door_installation:
                return DOOR_INSTALLATION_SETTING_LEFT_HANDED;
            case R.id.rdb_right_handed_dialog_door_installation:
                return DOOR_INSTALLATION_SETTING_RIGHT_HANDED;
            default:
                return -1;
        }
    }

    private int findSelectedLockStagesOption(RadioGroup radioGroup) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rdb_ninety_degrees_dialog_lock_stages:
                return LOCK_STAGES_NINETY_DEGREES;
            case R.id.rdb_one_stage_dialog_lock_stages:
                return LOCK_STAGES_ONE_STAGE;
            case R.id.rdb_two_stage_dialog_lock_stages:
                return LOCK_STAGES_TWO_STAGE;
            case R.id.rdb_three_stage_dialog_lock_stages:
                return LOCK_STAGES_THREE_STAGE;
            default:
                return -1;
        }
    }
    //endregion Declare Methods
}
