package com.projects.company.homes_lock.ui.login.fragment.register;

import com.projects.company.homes_lock.models.datamodels.response.FailureModel;

/**
 * This is RegisterFragment Interface
 */
public interface IRegisterFragment<T> {
    void onRegisterSuccessful(T response);

    void onRegisterFailed(FailureModel response);
}
