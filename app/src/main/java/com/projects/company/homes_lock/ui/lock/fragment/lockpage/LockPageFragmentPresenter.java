package com.projects.company.homes_lock.ui.lock.fragment.lockpage;

/**
 * This is Presenter for {@link LockPageFragment}
 */

public class LockPageFragmentPresenter implements LockPageFragmentContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private LockPageFragmentContract.mMvpView mLockPageFragmentView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    LockPageFragmentPresenter(LockPageFragmentContract.mMvpView _mLockPageFragmentView) {
        this.mLockPageFragmentView = _mLockPageFragmentView;
    }
}