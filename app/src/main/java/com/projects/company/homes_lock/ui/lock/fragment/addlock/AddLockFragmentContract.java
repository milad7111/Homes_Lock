package com.projects.company.homes_lock.ui.lock.fragment.addlock;

/**
 * This is Contract for {@link AddLockFragment} and {@link AddLockFragmentPresenter}
 */

public interface AddLockFragmentContract {
    interface mMvpView {
        public void saveDevice();
    }

    interface mMvpPresenter {
        public void saveDevice();
    }
}
