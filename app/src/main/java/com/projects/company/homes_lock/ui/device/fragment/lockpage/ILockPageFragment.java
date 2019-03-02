package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;

/**
 * This is LockPageFragment Interface
 */
public interface ILockPageFragment {
    void onFindNewNetworkAroundDevice(WifiNetworksModel wifiNetworksModel);

    void onGetAvailableWifiNetworksCountAroundDevice(int count);

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

    void onSendLockCommandSuccessful(String command);

    void onSendLockCommandFailed(String command);
}
