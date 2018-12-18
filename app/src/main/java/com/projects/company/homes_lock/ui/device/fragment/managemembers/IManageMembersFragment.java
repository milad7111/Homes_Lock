package com.projects.company.homes_lock.ui.device.fragment.managemembers;

import com.projects.company.homes_lock.database.tables.User;
import com.projects.company.homes_lock.models.datamodels.MemberModel;

import java.util.List;

/**
 * This is ManageMembersFragment Interface
 */

public interface IManageMembersFragment {
    void onAdapterItemClick(MemberModel member);

    void onGetUserLockData(List<User> response);
}
