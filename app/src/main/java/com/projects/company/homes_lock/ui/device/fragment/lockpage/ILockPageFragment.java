package com.projects.company.homes_lock.ui.device.fragment.lockpage;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.models.datamodels.ble.WifiNetworksModel;

/**
 * This is GatewayPageFragment Interface
 */
public interface ILockPageFragment {
    void onGetUpdatedDevice(Device response);

    void onSendLockCommandSuccessful(String command);

    void onSendLockCommandFailed(String command);

    void onGetReceiveNewConnectedClientToDevice(String connectedClientMacAddress);

    void onDeviceDisconnectedSuccessfully();
}
