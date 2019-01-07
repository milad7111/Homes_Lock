package com.projects.company.homes_lock.ui.device.fragment.addlock;

import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;

/**
 * This is AddLockFragment Interface
 */
public interface IAddLockFragment {
    void onFindLockInOnlineDataBaseSuccessful(String response);

    void onInsertUserLockSuccessful(UserLock response);

    void onAddLockToUserLockSuccessful(Boolean response);

    void onAddUserLockToUserSuccessful(Boolean response);

    void onGetUserSuccessful(User response);

    void onGetUserFailed(FailureModel response);

    void onDataInsert(Long id);

    void onFindLockInOnlineDataBaseFailed(ResponseBodyFailureModel response);

    void onInsertUserLockFailed(FailureModel response);

    void onAddLockToUserLockFailed(ResponseBodyFailureModel response);

    void onAddUserLockToUserFailed(ResponseBodyFailureModel response);

    void onDeviceRegistrationPushNotificationSuccessful(String registrationId);

    void onDeviceRegistrationPushNotificationFailed(ResponseBodyFailureModel response);
}
