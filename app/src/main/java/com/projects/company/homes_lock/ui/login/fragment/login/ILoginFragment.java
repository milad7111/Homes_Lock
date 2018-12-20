package com.projects.company.homes_lock.ui.login.fragment.login;

import com.projects.company.homes_lock.models.datamodels.response.FailureModel;

/**
 * This is LoginFragment Interface
 */
public interface ILoginFragment<T> {
    void onLoginSuccessful(T response);

    void onLoginFailed(FailureModel response);

    void onDataInsert(Long id);
}
