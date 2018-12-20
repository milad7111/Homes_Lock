package com.projects.company.homes_lock.ui.device.fragment.managemembers;

import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.MemberModel;

import java.util.List;

/**
 * This is ManageMembersFragment Interface
 */

public interface IManageMembersFragment {
    void onGetUserLockData(List<User> response);

    void onAdapterItemClick(MemberModel member);

    void onActionUserClick(MemberModel member);
}
