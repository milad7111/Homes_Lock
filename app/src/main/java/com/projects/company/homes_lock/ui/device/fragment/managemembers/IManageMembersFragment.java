package com.projects.company.homes_lock.ui.device.fragment.managemembers;

import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.database.tables.UserLock;
import com.projects.company.homes_lock.models.datamodels.MemberModel;
import com.projects.company.homes_lock.models.datamodels.response.FailureModel;
import com.projects.company.homes_lock.models.datamodels.response.ResponseBodyFailureModel;

import java.util.List;

/**
 * This is ManageMembersFragment Interface
 */

public interface IManageMembersFragment {
    void onGetUserLockDataSuccessful(List<User> response);

    void onGetUserLockDataFailed(Object response);

    void onActionUserClick(MemberModel member);

    void onRemoveMemberSuccessful(Long deletionTime);

    void onRemoveMemberFailed(ResponseBodyFailureModel response);

    void onGetUserListWithEmailAddressSuccessful(List<User> response);

    void onGetUserListWithEmailAddressFailed(FailureModel response);

    void onInsertUserLockSuccessful(UserLock response);

    void onAddLockToUserLockSuccessful(boolean response);

    void onAddUserLockToUserSuccessful(boolean response);

    void onInsertUserLockFailed(FailureModel response);

    void onAddLockToUserLockFailed(ResponseBodyFailureModel response);

    void onAddUserLockToUserFailed(ResponseBodyFailureModel response);
}
