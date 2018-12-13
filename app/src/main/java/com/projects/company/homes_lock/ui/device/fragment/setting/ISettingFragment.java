package com.projects.company.homes_lock.ui.device.fragment.setting;

/**
 * This is SettingFragment Interface
 */
public interface ISettingFragment {
    void onChangeOnlinePassword(boolean value);

    void onChangePairingPassword(boolean value);

    void onSetDeviceSetting(boolean value);

    void onRemoveAllLockMembers(String count);

    void onRemoveDeviceForOneMember(String count);
}
