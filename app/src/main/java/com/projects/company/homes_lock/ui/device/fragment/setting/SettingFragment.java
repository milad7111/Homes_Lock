package com.projects.company.homes_lock.ui.device.fragment.setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.projects.company.homes_lock.R;
import com.projects.company.homes_lock.database.tables.Device;

import static com.projects.company.homes_lock.utils.helper.DataHelper.CHANGE_OFFLINE_PASSWORD;
import static com.projects.company.homes_lock.utils.helper.DataHelper.CHANGE_ONLINE_PASSWORD;
import static com.projects.company.homes_lock.utils.helper.DataHelper.convertJsonToObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements View.OnClickListener {

    //region Declare Constants
    private static final String ARG_PARAM = "param";
    //endregion Declare Constants

    //region Declare Views
    private TextView txvChangePasswordOnlineDescriptionMoreInfoFragment;
    //endregion Declare Views

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private static Device mDevice;
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
        txvChangePasswordOnlineDescriptionMoreInfoFragment = view.findViewById(R.id.txv_change_password_online_description_more_info_fragment);
        //endregion Initialize Views

        //region Setup Views
        txvChangePasswordOnlineDescriptionMoreInfoFragment.setOnClickListener(this);
        //endregion Setup Views
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txv_change_password_online_description_more_info_fragment:
                handleChangePassword(CHANGE_ONLINE_PASSWORD);
                break;
        }
    }

    private void handleChangePassword(int changeStatus) {
        switch (changeStatus) {
            case CHANGE_ONLINE_PASSWORD:
                handleDialogChangePasswordOnline();
                break;
            case CHANGE_OFFLINE_PASSWORD:
                break;
        }
    }
    //endregion Main Callbacks

    //region Declare Methods
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
    //endregion Declare Methods
}
