package com.projects.company.homes_lock.ui.login.fragment.login;

import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyModel;

/**
 * This is LoginFragment Interface
 */
public interface ILoginFragment {
    void onLoginSuccessful(User user);

    void onLoginFailed(FailureModel response);

    void onDataInsert(Object id);

    void onFindLockInOnlineDataBaseSuccessful(String response);

    void onFindLockInOnlineDataBaseFailed(ResponseBodyFailureModel response);

    void onInsertUserLockSuccessful(UserLock response);

    void onInsertUserLockFailed(FailureModel response);

    void onAddLockToUserLockSuccessful(Boolean response);

    void onAddLockToUserLockFailed(ResponseBodyFailureModel response);

    void onAddUserLockToUserSuccessful(Boolean response);

    void onAddUserLockToUserFailed(ResponseBodyFailureModel response);

    void onGetUserSuccessful(User response);

    void onGetUserFailed(FailureModel response);

    void onDeviceRegistrationForPushNotificationSuccessful(ResponseBodyModel responseBodyModel);

    void onDeviceRegistrationForPushNotificationFailed(ResponseBodyFailureModel response);
}
