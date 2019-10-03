package com.projects.company.homes_lock.ui.device.fragment.gatewaypage;

import android.bluetooth.BluetoothGatt;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.AvailableBleDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;
import com.projects.company.homes_lock.models.datamodels.ble.WifiStateModel;

/**
 * This is GatewayPageFragment Interface
 */
public interface IGatewayPageFragment {
    void onFindNewNetworkAroundDevice(WifiNetworksModel wifiNetworksModel);

    void onSetDeviceWifiNetworkSuccessful();

    void onGetUpdatedDevice(Device response);

    void onGetNewConnectedDevice(ConnectedDeviceModel connectedDeviceModel);

    void onDeviceDisconnectedSuccessfully();

    void onGetNewAvailableBleDevice(AvailableBleDeviceModel availableBleDeviceModel);

    void onWriteServerMacAddressForGateWaySuccessful(AvailableBleDeviceModel availableBleDeviceModel);

    void onWriteServerPasswordForGateWaySuccessful();

    void onConnectCommandSentToGateWaySuccessful();

    void onConnectToServerSuccessful(boolean isrState, boolean isqState);

    void onConnectToMqttServerSuccessful(boolean isrState, boolean isqState);

    void onInternetStatusChange(boolean isiState);

    void onWifiStatusChange(boolean iswStatus);

    void onGetAvailableBleDevicesEnd();

    void onGetConnectedDevicesEnd();

    void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);

    void onReadWifiState(WifiStateModel wifiState);

    void onCancelDeviceWifiNetworkSuccessful();
}
