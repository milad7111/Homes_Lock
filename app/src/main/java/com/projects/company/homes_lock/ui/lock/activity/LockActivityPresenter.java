package com.projects.company.homes_lock.ui.lock.activity;

/**
 * This is Presenter for {@link LockActivity}
 */

public class LockActivityPresenter implements LockActivityContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LockActivityContract.mMvpView mLockActivityView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    LockActivityPresenter(LockActivityContract.mMvpView _mLockActivityView) {
        this.mLockActivityView = _mLockActivityView;
    }
}
