package com.projects.company.homes_lock.ui.device.fragment.gatewaypage;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.AvailableBleDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;

/**
 * This is GatewayPageFragment Interface
 */
public interface IGatewayPageFragment {
    void onFindNewNetworkAroundDevice(WifiNetworksModel wifiNetworksModel);

    void onSetDeviceWifiNetworkSSIDSuccessful();

    void onSetDeviceWifiNetworkSSIDFailed();

    void onSetDeviceWifiNetworkPasswordSuccessful();

    void onSetDeviceWifiNetworkPasswordFailed();

    void onSetDeviceWifiNetworkAuthenticationTypeSuccessful();

    void onSetDeviceWifiNetworkAuthenticationTypeFailed();

    void onSetDeviceWifiNetworkSuccessful();

    void onSetDeviceWifiNetworkFailed();

    void onGetUpdatedDevice(Device response);

    void onSendRequestGetAvailableWifiSuccessful();

    void onGetNewConnectedDevice(ConnectedDeviceModel connectedDeviceModel);

    void onDeviceDisconnectedSuccessfully();

    void onGetNewAvailableBleDevice(AvailableBleDeviceModel availableBleDeviceModel);

    void onWriteServerMacAddressForGateWaySuccessful(AvailableBleDeviceModel availableBleDeviceModel);

    void onWriteServerPasswordForGateWaySuccessful();

    void onConnectCommandSentToGateWaySuccessful();

    void onConnectToServerSuccessful(boolean isrState, boolean isqState);

    void onConnectToMqttServerSuccessful(boolean isrState, boolean isqState);
}
