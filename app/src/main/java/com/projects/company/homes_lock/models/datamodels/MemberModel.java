package com.projects.company.homes_lock.models.datamodels;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.projects.company.homes_lock.R;

import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_NOT_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_PRIMARY_ADMIN;
import static com.projects.company.homes_lock.utils.helper.DataHelper.MEMBER_STATUS_SECONDARY_ADMIN;

public class MemberModel {

    private Context mContext;

    private Drawable mMemberAvatar;
    private String mMemberName;
    private int mMemberAdminStatus;

    public MemberModel(Context mContext, Drawable mMemberAvatar, String mMemberName, int mMemberAdminStatus) {
        this.mContext = mContext;

        this.mMemberAvatar = mMemberAvatar;
        this.mMemberName = mMemberName;
        this.mMemberAdminStatus = mMemberAdminStatus;
    }

    public Drawable getMemberAvatar() {
        return mMemberAvatar;
    }

    public void setMemberAvatar(Drawable mUserIcon) {
        this.mMemberAvatar = mUserIcon;
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

    public Drawable getMemberActionIcon() {
        switch (mMemberAdminStatus) {
            case MEMBER_STATUS_PRIMARY_ADMIN:
                return ContextCompat.getDrawable(mContext, R.drawable.ic_admin_user_status_primary);
            case MEMBER_STATUS_SECONDARY_ADMIN:
                return ContextCompat.getDrawable(mContext, R.drawable.ic_admin_user_status_secondary);
            case MEMBER_STATUS_NOT_ADMIN:
                return ContextCompat.getDrawable(mContext, R.drawable.ic_delete);
            default:
                return null;
        }
    }
}