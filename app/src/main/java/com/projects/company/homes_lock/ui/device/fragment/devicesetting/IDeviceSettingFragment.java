package com.projects.company.homes_lock.ui.device.fragment.devicesetting;

import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;

/**
 * This is DeviceSettingFragment Interface
 */
public interface IDeviceSettingFragment {
    void onChangeOnlinePassword(boolean value);

    void onRemoveAllDeviceMembersSuccessful(String count);

    void onRemoveDeviceForOneMemberSuccessful(String count);

    void onRemoveDeviceForOneMemberFailed(ResponseBodyFailureModel response);

    void onRemoveAllLockMembersFailed(ResponseBodyFailureModel response);

    void onSetDoorInstallationSuccessful();

    void onSetDoorInstallationFailed();

    void onCheckOldPairingPasswordSuccessful();

    void onCheckOldPairingPasswordFailed(String string);

    void onChangePairingPasswordSuccessful();

    void onChangePairingPasswordFailed(String string);

    void onResetBleDeviceSuccessful();

    void onInitializeCalibrationLockSuccessful();

    void onSetIdlePositionSuccessful();

    void onSetLatchPositionSuccessful();

    void onSetLockPositionSuccessful();

    void onSetConfigSuccessful();

    void onInitializeCalibrationLockFailed();
}
