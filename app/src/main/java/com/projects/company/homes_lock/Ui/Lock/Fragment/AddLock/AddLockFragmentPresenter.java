package com.projects.company.homes_lock.Ui.Lock.Fragment.AddLock;

/**
 * This is Presenter for {@link AddLockFragment}
 */

public class AddLockFragmentPresenter implements AddLockFragmentContract.mMvpPresenter {

    //region Declare Constants
    //endregion Declare Constants

    //region Declare Variables
    //endregion Declare Variables

    //region Declare Objects
    private AddLockFragmentContract.mMvpView mAddLockFragmentView;
    //endregion Declare Objects

    //region Declare Views
    //endregion Declare Views

    AddLockFragmentPresenter(AddLockFragmentContract.mMvpView _mAddLockFragmentView) {
        this.mAddLockFragmentView = _mAddLockFragmentView;
    }
}