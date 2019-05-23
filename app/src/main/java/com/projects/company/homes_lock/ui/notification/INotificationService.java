package com.projects.company.homes_lock.ui.notification;

import com.projects.company.homes_lock.database.tables.Device;
import com.projects.company.homes_lock.database.tables.Notification;
import com.projects.company.homes_lock.models.datamodels.ble.ConnectedDeviceModel;

/**
 * This is GatewayPageFragment Interface
 */
public interface INotificationService {
    void onDataInsert(Notification response);
}
