package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;

/**
 * This is LockPageFragment Interface
 */
public interface ILockPageFragment {
    void onFindNewNetworkAroundDevice(WifiNetworksModel wifiNetworksModel);

    void onGetAvailableWifiNetworksCountAroundDevice(int count);
}
