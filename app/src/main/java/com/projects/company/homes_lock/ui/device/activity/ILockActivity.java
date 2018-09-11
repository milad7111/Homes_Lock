package com.projects.company.homes_lock.ui.device.activity;

import com.projects.company.homes_lock.database.tables.Device;

/**
 * This is LockActivity Interface
 */

public interface ILockActivity {
    void getAllDevices();

    void insertDevice(Device device);

    void deleteDevice(Device device);
}
