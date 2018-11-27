package com.projects.company.homes_lock.ui.device.fragment.addlock;

import com.projects.company.homes_lock.database.tables.UserLock;

import okhttp3.ResponseBody;

/**
 * This is AddLockFragment Interface
 */
public interface IAddLockFragment {
    void onFindLockInOnlineDataBase(Boolean response);

    void onInsertUserLockSuccessful(UserLock response);
}
