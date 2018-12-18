package com.projects.company.homes_lock.models.datamodels;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.projects.company.homes_lock.R;

import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_NOT_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_PRIMARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_SECONDARY_ADMIN;

public class MemberModel {

    private int mMemberAvatarDrawableId;
    private String mMemberName;
    private int mMemberAdminStatus;

    public MemberModel(int mMemberDrawableId, String mMemberName, int mMemberAdminStatus) {
        this.mMemberAvatarDrawableId = mMemberDrawableId;
        this.mMemberName = mMemberName;
        this.mMemberAdminStatus = mMemberAdminStatus;
    }

    public int getMemberAvatarDrawableId() {
        return mMemberAvatarDrawableId;
    }

    public void setMemberAvatarDrawableId(int mMemberDrawableId) {
        this.mMemberAvatarDrawableId = mMemberDrawableId;
    }

    public String getMemberName() {
        return mMemberName;
    }

    public void setMemberName(String mUserName) {
        this.mMemberName = mUserName;
    }

    public int getMemberAdminStatus() {
        return mMemberAdminStatus;
    }

    public void setMemberAdminStatus(int mAdminStatus) {
        this.mMemberAdminStatus = mAdminStatus;
    }

    public int getMemberActionDrawableId() {
        switch (mMemberAdminStatus) {
            case MEMBER_STATUS_PRIMARY_ADMIN:
                return R.drawable.ic_admin_user_status_primary;
            case MEMBER_STATUS_SECONDARY_ADMIN:
                return R.drawable.ic_admin_user_status_secondary;
            case MEMBER_STATUS_NOT_ADMIN:
                return R.drawable.ic_delete;
            default:
                return -1;
        }
    }
}