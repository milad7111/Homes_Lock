package com.projects.company.homes_lock.ui.device.fragment.addlock;

import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;

/**
 * This is AddLockFragment Interface
 */
public interface IAddLockFragment {
    void onFindLockInOnlineDataBase(String response);

    void onInsertUserLockSuccessful(UserLock response);

    void onAddLockToUserLockSuccessful(Boolean response);

    void onAddUserLockToUserSuccessful(Boolean response);

    void onGetUserSuccessful(User response);

    void onDataInsert(Long id);
}
