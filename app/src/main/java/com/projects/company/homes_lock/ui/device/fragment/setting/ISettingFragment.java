package com.projects.company.homes_lock.ui.device.fragment.setting;

/**
 * This is SettingFragment Interface
 */
public interface ISettingFragment {
    void onSetDoorInstallationSetting(boolean value);

    void onSetLockStagesSetting(boolean value);

    void onChangeOnlinePassword(boolean value);

    void onChangePairingPassword(boolean value);

    void onRemoveLock(boolean value);
}
