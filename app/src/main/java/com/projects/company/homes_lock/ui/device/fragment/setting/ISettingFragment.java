package com.projects.company.homes_lock.ui.device.fragment.setting;

import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;

/**
 * This is SettingFragment Interface
 */
public interface ISettingFragment {
    void onChangeOnlinePassword(boolean value);

    void onSetDeviceSetting(boolean value);

    void onRemoveAllLockMembersSuccessful(String count);

    void onRemoveDeviceForOneMemberSuccessful(String count);

    void onRemoveDeviceForOneMemberFailed(ResponseBodyFailureModel response);

    void onRemoveAllLockMembersFailed(ResponseBodyFailureModel response);

    void onSetDoorInstallationSuccessful();

    void onSetDoorInstallationFailed();

    void onSetLockStagesSuccessful();

    void onSetLockStagesFailed();

    void onCheckOldPairingPasswordSuccessful();

    void onCheckOldPairingPasswordFailed(String string);

    void onChangePairingPasswordSuccessful();

    void onChangePairingPasswordFailed(String string);
}
